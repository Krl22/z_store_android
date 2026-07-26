package com.zeta.store.data

import com.zeta.store.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder

class SupabaseClient(
    private val supabaseUrl: String = BuildConfig.SUPABASE_URL.trimEnd('/'),
    private val anonKey: String = BuildConfig.SUPABASE_ANON_KEY,
    private val imageUploadEndpoint: String = BuildConfig.IMAGE_UPLOAD_ENDPOINT.trimEnd('/'),
) {
    val isConfigured: Boolean
        get() = supabaseUrl.startsWith("http") && anonKey.isNotBlank()

    suspend fun fetchProducts(): List<ProductRecord> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val response = request(
            path = "/rest/v1/products?select=*&order=display_order.asc",
            method = "GET",
        )

        JSONArray(response).toProducts()
    }

    suspend fun fetchProducts(session: SupabaseSession): List<ProductRecord> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val response = request(
            path = "/rest/v1/products?select=*&order=display_order.asc",
            method = "GET",
            authToken = session.accessToken,
        )

        JSONArray(response).toProducts()
    }

    suspend fun fetchHomePromos(): List<HomePromoRecord> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val response = request(
            path = "/rest/v1/home_promos?select=*&order=display_order.asc",
            method = "GET",
        )

        JSONArray(response).toHomePromos()
    }

    suspend fun fetchHomePromos(session: SupabaseSession): List<HomePromoRecord> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val response = request(
            path = "/rest/v1/home_promos?select=*&order=display_order.asc",
            method = "GET",
            authToken = session.accessToken,
        )

        JSONArray(response).toHomePromos()
    }

    suspend fun signIn(email: String, password: String): SupabaseSession =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("email", email)
                .put("password", password)

            parseSession(
                request(
                    path = "/auth/v1/token?grant_type=password",
                    method = "POST",
                    body = body,
                )
            )
        }

    suspend fun signUp(email: String, password: String): SupabaseSession =
        withContext(Dispatchers.IO) {
            val body = JSONObject()
                .put("email", email)
                .put("password", password)

            parseSession(
                request(
                    path = "/auth/v1/signup",
                    method = "POST",
                    body = body,
                )
            )
        }

    fun googleSignInUrl(redirectTo: String): String {
        val encodedRedirect = URLEncoder.encode(redirectTo, "UTF-8")
        return "$supabaseUrl/auth/v1/authorize?provider=google&redirect_to=$encodedRedirect"
    }

    suspend fun sessionFromOAuthRedirect(uriString: String): SupabaseSession? =
        withContext(Dispatchers.IO) {
            val params = parseRedirectParams(uriString)
            val error = params["error_description"] ?: params["error"]
            if (!error.isNullOrBlank()) {
                throw IllegalStateException(error)
            }

            val accessToken = params["access_token"] ?: return@withContext null
            val refreshToken = params["refresh_token"].orEmpty()
            val user = JSONObject(
                request(
                    path = "/auth/v1/user",
                    method = "GET",
                    authToken = accessToken,
                )
            )

            SupabaseSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userId = user.getString("id"),
                email = user.optString("email"),
            )
        }

    suspend fun refreshSession(refreshToken: String): SupabaseSession =
        withContext(Dispatchers.IO) {
            if (refreshToken.isBlank()) throw IllegalStateException("Tu sesion vencio. Vuelve a iniciar sesion.")

            val body = JSONObject().put("refresh_token", refreshToken)
            parseSession(
                request(
                    path = "/auth/v1/token?grant_type=refresh_token",
                    method = "POST",
                    body = body,
                )
            )
        }

    suspend fun fetchProfile(session: SupabaseSession): ProfileRecord =
        withContext(Dispatchers.IO) {
            val response = request(
                path = "/rest/v1/profiles?id=eq.${session.userId}&select=id,email,role,full_name,phone,default_address_id&limit=1",
                method = "GET",
                authToken = session.accessToken,
            )
            val array = JSONArray(response)
            if (array.length() == 0) {
                ProfileRecord(id = session.userId, email = session.email, role = "customer")
            } else {
                val item = array.getJSONObject(0)
                ProfileRecord(
                    id = item.getString("id"),
                    email = item.optString("email", session.email),
                    role = item.optString("role", "customer"),
                    fullName = item.optNullableString("full_name"),
                    phone = item.optNullableString("phone"),
                    defaultAddressId = item.optNullableString("default_address_id"),
                )
            }
        }

    suspend fun updateProfilePhone(session: SupabaseSession, phone: String): ProfileRecord =
        withContext(Dispatchers.IO) {
            if (!isConfigured) throw IllegalStateException("Configura Supabase para guardar el telefono.")

            val body = JSONObject()
                .put("phone", phone.trim())

            val response = request(
                path = "/rest/v1/profiles?id=eq.${session.userId}&select=id,email,role,full_name,phone,default_address_id",
                method = "PATCH",
                body = body,
                authToken = session.accessToken,
                prefer = "return=representation",
            )
            val item = JSONArray(response).optJSONObject(0)
                ?: throw IllegalStateException("No encontre tu perfil para guardar el telefono.")

            ProfileRecord(
                id = item.getString("id"),
                email = item.optString("email", session.email),
                role = item.optString("role", "customer"),
                fullName = item.optNullableString("full_name"),
                phone = item.optNullableString("phone"),
                defaultAddressId = item.optNullableString("default_address_id"),
            )
        }

    suspend fun fetchAddresses(session: SupabaseSession): List<AddressRecord> =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext emptyList()

            val response = request(
                path = "/rest/v1/customer_addresses?user_id=eq.${session.userId}&select=*&order=is_default.desc,created_at.desc",
                method = "GET",
                authToken = session.accessToken,
            )

            JSONArray(response).toAddresses()
        }

    suspend fun saveAddress(session: SupabaseSession, address: AddressRecord): AddressRecord =
        withContext(Dispatchers.IO) {
            if (!isConfigured) throw IllegalStateException("Configura Supabase para guardar direcciones.")

            val response = request(
                path = "/rest/v1/customer_addresses?on_conflict=id",
                method = "POST",
                body = address.copy(userId = session.userId).toJson(),
                authToken = session.accessToken,
                prefer = "resolution=merge-duplicates,return=representation",
            )

            JSONArray(response).toAddresses().firstOrNull() ?: address
        }

    suspend fun deleteAddress(session: SupabaseSession, addressId: String): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext

            request(
                path = "/rest/v1/customer_addresses?id=eq.${URLEncoder.encode(addressId, "UTF-8")}&user_id=eq.${session.userId}",
                method = "DELETE",
                authToken = session.accessToken,
                prefer = "return=minimal",
            )
        }

    suspend fun registerPushToken(session: SupabaseSession, token: String, deviceName: String = "Android"): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured || token.isBlank()) return@withContext

            val body = JSONObject()
                .put("user_id", session.userId)
                .put("token", token)
                .put("platform", "android")
                .put("device_name", deviceName)
                .put("is_active", true)

            request(
                path = "/rest/v1/device_push_tokens?on_conflict=user_id,token",
                method = "POST",
                body = body,
                authToken = session.accessToken,
                prefer = "resolution=merge-duplicates,return=minimal",
            )
        }

    suspend fun saveProduct(session: SupabaseSession, product: ProductRecord): ProductRecord =
        withContext(Dispatchers.IO) {
            val body = product.toJson()
            val response = request(
                path = "/rest/v1/products?on_conflict=id",
                method = "POST",
                body = body,
                authToken = session.accessToken,
                prefer = "resolution=merge-duplicates,return=representation",
            )
            JSONArray(response).toProducts().firstOrNull() ?: product
        }

    suspend fun saveHomePromo(session: SupabaseSession, promo: HomePromoRecord): HomePromoRecord =
        withContext(Dispatchers.IO) {
            val response = request(
                path = "/rest/v1/home_promos?on_conflict=id",
                method = "POST",
                body = promo.toJson(),
                authToken = session.accessToken,
                prefer = "resolution=merge-duplicates,return=representation",
            )
            JSONArray(response).toHomePromos().firstOrNull() ?: promo
        }

    suspend fun deleteProduct(session: SupabaseSession, id: String): Unit =
        withContext(Dispatchers.IO) {
            request(
                path = "/rest/v1/products?id=eq.${URLEncoder.encode(id, "UTF-8")}",
                method = "DELETE",
                authToken = session.accessToken,
                prefer = "return=minimal",
            )
        }

    suspend fun deleteHomePromo(session: SupabaseSession, id: String): Unit =
        withContext(Dispatchers.IO) {
            request(
                path = "/rest/v1/home_promos?id=eq.${URLEncoder.encode(id, "UTF-8")}",
                method = "DELETE",
                authToken = session.accessToken,
                prefer = "return=minimal",
            )
        }

    suspend fun fetchCartItems(session: SupabaseSession): Map<String, Int> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyMap()

        val response = request(
            path = "/rest/v1/cart_items?user_id=eq.${session.userId}&select=product_id,quantity",
            method = "GET",
            authToken = session.accessToken,
        )

        JSONArray(response).toCartItems()
    }

    suspend fun saveCartItem(session: SupabaseSession, productId: String, quantity: Int): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext

            val body = JSONObject()
                .put("user_id", session.userId)
                .put("product_id", productId)
                .put("quantity", quantity.coerceAtLeast(1))

            request(
                path = "/rest/v1/cart_items?on_conflict=user_id,product_id",
                method = "POST",
                body = body,
                authToken = session.accessToken,
                prefer = "resolution=merge-duplicates,return=minimal",
            )
        }

    suspend fun deleteCartItem(session: SupabaseSession, productId: String): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext

            request(
                path = "/rest/v1/cart_items?user_id=eq.${session.userId}&product_id=eq.${URLEncoder.encode(productId, "UTF-8")}",
                method = "DELETE",
                authToken = session.accessToken,
                prefer = "return=minimal",
            )
        }

    suspend fun fetchSavedProductIds(session: SupabaseSession): Set<String> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptySet()

        val response = request(
            path = "/rest/v1/saved_products?user_id=eq.${session.userId}&select=product_id&order=created_at.desc",
            method = "GET",
            authToken = session.accessToken,
        )

        JSONArray(response).toSavedProductIds()
    }

    suspend fun saveProductForLater(session: SupabaseSession, productId: String): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext

            val body = JSONObject()
                .put("user_id", session.userId)
                .put("product_id", productId)

            request(
                path = "/rest/v1/saved_products?on_conflict=user_id,product_id",
                method = "POST",
                body = body,
                authToken = session.accessToken,
                prefer = "resolution=ignore-duplicates,return=minimal",
            )
        }

    suspend fun deleteSavedProduct(session: SupabaseSession, productId: String): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext

            request(
                path = "/rest/v1/saved_products?user_id=eq.${session.userId}&product_id=eq.${URLEncoder.encode(productId, "UTF-8")}",
                method = "DELETE",
                authToken = session.accessToken,
                prefer = "return=minimal",
            )
        }

    suspend fun createOrder(
        session: SupabaseSession,
        items: List<OrderItemInput>,
        addressId: String? = null,
        deliveryMethod: String = "pickup",
    ): OrderRecord =
        withContext(Dispatchers.IO) {
            if (!isConfigured) throw IllegalStateException("Configura Supabase para registrar pedidos.")
            if (items.isEmpty()) throw IllegalStateException("El carrito esta vacio.")

            val itemArray = JSONArray()
            items.forEach { item ->
                itemArray.put(
                    JSONObject()
                        .put("product_id", item.productId)
                        .put("product_name", item.productName)
                        .put("quantity", item.quantity.coerceAtLeast(1))
                        .put("unit_price", item.unitPrice.coerceAtLeast(0))
                        .put("image_url", item.imageUrl)
                )
            }

            val cleanAddressId = addressId.cleanNullableParam()
            val body = JSONObject()
                .put("p_items", itemArray)
                .put("p_delivery_method", deliveryMethod)
            if (cleanAddressId == null) {
                body.put("p_address_id", JSONObject.NULL)
            } else {
                body.put("p_address_id", cleanAddressId)
            }

            val response = request(
                path = "/rest/v1/rpc/create_order_with_items",
                method = "POST",
                body = body,
                authToken = session.accessToken,
            )
            val orderId = response.toRpcUuid()

            fetchOrderById(session, orderId)
                ?: throw IllegalStateException("Pedido creado, pero no pude leerlo.")
        }

    suspend fun fetchMyOrders(session: SupabaseSession): List<OrderRecord> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val response = request(
            path = "/rest/v1/orders?user_id=eq.${session.userId}&select=*,order_items(*)&order=created_at.desc",
            method = "GET",
            authToken = session.accessToken,
        )

        JSONArray(response).toOrders()
    }

    suspend fun fetchAdminOrders(session: SupabaseSession): List<OrderRecord> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()

        val response = request(
            path = "/rest/v1/orders?select=*,order_items(*)&order=created_at.desc",
            method = "GET",
            authToken = session.accessToken,
        )

        JSONArray(response).toOrders()
    }

    suspend fun updateOrderStatus(session: SupabaseSession, orderId: String, status: String): OrderRecord? =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext null

            val response = request(
                path = "/rest/v1/orders?id=eq.${URLEncoder.encode(orderId, "UTF-8")}",
                method = "PATCH",
                body = JSONObject().put("status", status),
                authToken = session.accessToken,
                prefer = "return=representation",
            )

            JSONArray(response).toOrders().firstOrNull()
        }

    suspend fun fetchAdminNotifications(session: SupabaseSession): List<AdminNotificationRecord> =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext emptyList()

            val response = request(
                path = "/rest/v1/admin_notifications?select=*&order=created_at.desc&limit=30",
                method = "GET",
                authToken = session.accessToken,
            )

            JSONArray(response).toAdminNotifications()
        }

    suspend fun markAdminNotificationRead(session: SupabaseSession, notificationId: String): Unit =
        withContext(Dispatchers.IO) {
            if (!isConfigured) return@withContext

            request(
                path = "/rest/v1/admin_notifications?id=eq.${URLEncoder.encode(notificationId, "UTF-8")}",
                method = "PATCH",
                body = JSONObject().put("is_read", true),
                authToken = session.accessToken,
                prefer = "return=minimal",
            )
        }

    suspend fun uploadStoreImage(
        session: SupabaseSession,
        objectPath: String,
        contentType: String,
        bytes: ByteArray,
    ): String = withContext(Dispatchers.IO) {
        if (!isConfigured) throw IllegalStateException("Configura Supabase para subir imagenes.")

        if (imageUploadEndpoint.isNotBlank()) {
            return@withContext uploadImageToWorker(
                endpoint = imageUploadEndpoint,
                objectPath = objectPath,
                contentType = contentType,
                bytes = bytes,
                authToken = session.accessToken,
            )
        }

        uploadImageToSupabaseStorage(session, objectPath, contentType, bytes)
    }

    private fun parseSession(json: String): SupabaseSession {
        val root = JSONObject(json)
        val user = root.getJSONObject("user")
        if (!root.has("access_token")) {
            throw IllegalStateException("Cuenta creada. Revisa si Supabase requiere confirmar email antes de iniciar sesion.")
        }
        return SupabaseSession(
            accessToken = root.getString("access_token"),
            refreshToken = root.getString("refresh_token"),
            userId = user.getString("id"),
            email = user.optString("email"),
        )
    }

    private fun request(
        path: String,
        method: String,
        body: JSONObject? = null,
        authToken: String? = null,
        prefer: String? = null,
    ): String = requestJson(
        path = path,
        method = method,
        body = body?.toString(),
        authToken = authToken,
        prefer = prefer,
    )

    private fun requestJson(
        path: String,
        method: String,
        body: String? = null,
        authToken: String? = null,
        prefer: String? = null,
    ): String {
        val connection = (URL("$supabaseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer ${authToken ?: anonKey}")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            if (prefer != null) setRequestProperty("Prefer", prefer)
            if (body != null) {
                doOutput = true
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(body.toString())
                }
            }
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        connection.disconnect()

        if (status !in 200..299) {
            val message = response.extractErrorMessage()
            throw IllegalStateException(message.ifBlank { "Supabase respondio con error $status" })
        }

        return response
    }

    private fun requestBinary(
        path: String,
        method: String,
        contentType: String,
        bytes: ByteArray,
        authToken: String,
    ): String {
        val connection = (URL("$supabaseUrl$path").openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 20_000
            readTimeout = 20_000
            doOutput = true
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $authToken")
            setRequestProperty("Content-Type", contentType)
            setRequestProperty("Accept", "application/json")
            setRequestProperty("x-upsert", "true")
            outputStream.use { stream -> stream.write(bytes) }
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        connection.disconnect()

        if (status !in 200..299) {
            val message = response.extractErrorMessage()
            throw IllegalStateException(message.ifBlank { "Supabase Storage respondio con error $status" })
        }

        return response
    }

    private fun uploadImageToSupabaseStorage(
        session: SupabaseSession,
        objectPath: String,
        contentType: String,
        bytes: ByteArray,
    ): String {
        requestBinary(
            path = "/storage/v1/object/store-images/${objectPath.toStoragePath()}",
            method = "POST",
            contentType = contentType,
            bytes = bytes,
            authToken = session.accessToken,
        )

        return "$supabaseUrl/storage/v1/object/public/store-images/${objectPath.toStoragePath()}"
    }

    private fun uploadImageToWorker(
        endpoint: String,
        objectPath: String,
        contentType: String,
        bytes: ByteArray,
        authToken: String,
    ): String {
        val pathParam = URLEncoder.encode(objectPath, "UTF-8")
        val connection = (URL("$endpoint/upload?path=$pathParam").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 20_000
            doOutput = true
            setRequestProperty("Authorization", "Bearer $authToken")
            setRequestProperty("Content-Type", contentType)
            setRequestProperty("Accept", "application/json")
            outputStream.use { stream -> stream.write(bytes) }
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        connection.disconnect()

        if (status !in 200..299) {
            val message = response.extractErrorMessage()
            throw IllegalStateException(message.ifBlank { "Cloudflare respondio con error $status" })
        }

        return JSONObject(response).getString("url")
    }

    private fun fetchOrderById(session: SupabaseSession, orderId: String): OrderRecord? {
        val response = request(
            path = "/rest/v1/orders?id=eq.${URLEncoder.encode(orderId, "UTF-8")}&select=*,order_items(*)&limit=1",
            method = "GET",
            authToken = session.accessToken,
        )

        return JSONArray(response).toOrders().firstOrNull()
    }
}

private fun parseRedirectParams(uriString: String): Map<String, String> {
    val uri = URI(uriString)
    return listOfNotNull(uri.query, uri.fragment)
        .flatMap { part -> part.split("&") }
        .mapNotNull { pair ->
            val index = pair.indexOf("=")
            if (index <= 0) null else pair.substring(0, index) to java.net.URLDecoder.decode(pair.substring(index + 1), "UTF-8")
        }
        .toMap()
}

private fun JSONArray.toProducts(): List<ProductRecord> =
    List(length()) { index ->
        val item = getJSONObject(index)
        ProductRecord(
            id = item.getString("id"),
            name = item.getString("name"),
            latin = item.optString("latin_name"),
            format = item.optString("format"),
            category = item.optString("category"),
            price = item.optInt("price"),
            offerPrice = item.optNullableInt("offer_price"),
            isFeaturedOffer = item.optBoolean("is_featured_offer", false),
            tag = item.optString("tag"),
            rating = item.optString("rating", "4.8"),
            colorHex = item.optString("color_hex", "#426B35"),
            accentHex = item.optString("accent_hex", "#EEF4D8"),
            benefits = item.optJSONArray("benefits").toStringList(),
            stock = item.optInt("stock", 0),
            imageKeys = item.optJSONArray("image_keys").toStringList(),
            imageUrls = item.optJSONArray("image_urls").toStringList(),
            isActive = item.optBoolean("is_active", true),
            displayOrder = item.optInt("display_order", 0),
        )
    }

private fun ProductRecord.toJson(): JSONObject =
    JSONObject()
        .put("id", id)
        .put("name", name)
        .put("latin_name", latin)
        .put("format", format)
        .put("category", category)
        .put("price", price)
        .put("offer_price", offerPrice ?: JSONObject.NULL)
        .put("is_featured_offer", isFeaturedOffer)
        .put("tag", tag)
        .put("rating", rating)
        .put("color_hex", colorHex)
        .put("accent_hex", accentHex)
        .put("benefits", JSONArray(benefits))
        .put("stock", stock)
        .put("image_keys", JSONArray(imageKeys))
        .put("image_urls", JSONArray(imageUrls))
        .put("is_active", isActive)
        .put("display_order", displayOrder)

private fun JSONArray.toHomePromos(): List<HomePromoRecord> =
    List(length()) { index ->
        val item = getJSONObject(index)
        HomePromoRecord(
            id = item.getString("id"),
            eyebrow = item.optString("eyebrow"),
            title = item.optString("title"),
            detail = item.optString("detail"),
            colorHexes = item.optJSONArray("color_hexes").toStringList(),
            imageKey = item.optString("image_key", ""),
            imageUrl = item.optString("image_url", ""),
            displayOrder = item.optInt("display_order", 0),
            isActive = item.optBoolean("is_active", true),
        )
    }

private fun HomePromoRecord.toJson(): JSONObject =
    JSONObject()
        .put("id", id)
        .put("eyebrow", eyebrow)
        .put("title", title)
        .put("detail", detail)
        .put("color_hexes", JSONArray(colorHexes))
        .put("image_key", imageKey)
        .put("image_url", imageUrl)
        .put("display_order", displayOrder)
        .put("is_active", isActive)

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return List(length()) { index -> optString(index) }.filter { it.isNotBlank() }
}

private fun JSONArray.toCartItems(): Map<String, Int> =
    List(length()) { index -> getJSONObject(index) }
        .associate { item ->
            item.getString("product_id") to item.optInt("quantity", 1).coerceAtLeast(1)
        }

private fun JSONArray.toSavedProductIds(): Set<String> =
    List(length()) { index -> getJSONObject(index).getString("product_id") }.toSet()

private fun JSONArray.toAddresses(): List<AddressRecord> =
    List(length()) { index ->
        val item = getJSONObject(index)
        AddressRecord(
            id = item.getString("id"),
            userId = item.optString("user_id"),
            label = item.optString("label", "Casa"),
            recipientName = item.optString("recipient_name"),
            phone = item.optString("phone"),
            line1 = item.optString("line1"),
            line2 = item.optString("line2"),
            district = item.optString("district", "Lima"),
            city = item.optString("city", "Lima"),
            country = item.optString("country", "PE"),
            reference = item.optString("reference"),
            isDefault = item.optBoolean("is_default", false),
        )
    }

private fun AddressRecord.toJson(): JSONObject =
    JSONObject()
        .put("id", id.ifBlank { java.util.UUID.randomUUID().toString() })
        .put("user_id", userId)
        .put("label", label.ifBlank { "Casa" })
        .put("recipient_name", recipientName)
        .put("phone", phone)
        .put("line1", line1)
        .put("line2", line2)
        .put("district", district.ifBlank { "Lima" })
        .put("city", city.ifBlank { "Lima" })
        .put("country", country.ifBlank { "PE" })
        .put("reference", reference)
        .put("is_default", isDefault)

private fun JSONArray.toOrders(): List<OrderRecord> =
    List(length()) { index -> getJSONObject(index).toOrderRecord() }

private fun JSONObject.toOrderRecord(): OrderRecord =
    OrderRecord(
        id = getString("id"),
        userId = optString("user_id"),
        customerEmail = optString("customer_email"),
        status = optString("status", "paid"),
        paymentStatus = optString("payment_status", "simulated_paid"),
        subtotal = optInt("subtotal", 0),
        total = optInt("total", 0),
        addressId = optString("address_id"),
        deliveryMethod = optString("delivery_method", "pickup"),
        deliveryStatus = optString("delivery_status", "pending"),
        customerName = optString("customer_name"),
        customerPhone = optString("customer_phone"),
        deliveryAddressSnapshot = optJSONObject("delivery_address_snapshot")?.toString().orEmpty(),
        createdAt = optString("created_at"),
        items = optJSONArray("order_items").toOrderItems(),
    )

private fun JSONArray?.toOrderItems(): List<OrderItemRecord> {
    if (this == null) return emptyList()
    return List(length()) { index ->
        val item = getJSONObject(index)
        OrderItemRecord(
            productId = item.optString("product_id"),
            productName = item.optString("product_name"),
            quantity = item.optInt("quantity", 1).coerceAtLeast(1),
            unitPrice = item.optInt("unit_price", 0),
            imageUrl = item.optString("image_url"),
        )
    }
}

private fun JSONArray.toAdminNotifications(): List<AdminNotificationRecord> =
    List(length()) { index ->
        val item = getJSONObject(index)
        AdminNotificationRecord(
            id = item.getString("id"),
            orderId = item.optString("order_id"),
            title = item.optString("title", "Nuevo pedido"),
            body = item.optString("body"),
            isRead = item.optBoolean("is_read", false),
            createdAt = item.optString("created_at"),
        )
    }

private fun String.toRpcUuid(): String {
    val clean = trim()
    return when {
        clean.startsWith("\"") -> clean.trim('"')
        clean.startsWith("[") -> {
            val array = JSONArray(clean)
            if (array.length() == 0) "" else array.getJSONObject(0).optString("create_order_with_items")
        }
        clean.startsWith("{") -> JSONObject(clean).optString("create_order_with_items")
        else -> clean
    }.ifBlank { throw IllegalStateException("Supabase no devolvio el id del pedido.") }
}

private fun String.extractErrorMessage(): String {
    val body = trim()
    if (body.isBlank()) return ""
    val json = runCatching { JSONObject(body) }.getOrNull() ?: return body
    return listOf("message", "msg", "error_description", "error", "details", "hint")
        .firstNotNullOfOrNull { key -> json.optString(key).takeIf { it.isNotBlank() } }
        ?: body
}

private fun JSONObject.optNullableString(name: String): String =
    if (isNull(name)) "" else optString(name).cleanNullableParam().orEmpty()

private fun JSONObject.optNullableInt(name: String): Int? =
    if (!has(name) || isNull(name)) null else optInt(name).takeIf { it > 0 }

private fun String?.cleanNullableParam(): String? =
    this
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?.takeUnless { it.equals("null", ignoreCase = true) }

private fun String.toStoragePath(): String =
    trim('/')
        .split("/")
        .joinToString("/") { segment -> URLEncoder.encode(segment, "UTF-8").replace("+", "%20") }
