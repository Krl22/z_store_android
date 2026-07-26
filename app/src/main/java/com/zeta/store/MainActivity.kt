package com.zeta.store

import android.annotation.SuppressLint
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zeta.store.data.AddressRecord
import com.zeta.store.data.ProductRecord
import com.zeta.store.data.ProfileRecord
import com.zeta.store.data.HomePromoRecord
import com.zeta.store.data.AdminNotificationRecord
import com.zeta.store.data.OrderItemInput
import com.zeta.store.data.OrderRecord
import com.zeta.store.data.SupabaseClient
import com.zeta.store.data.SupabaseSession
import com.zeta.store.ui.theme.BosqueBackground
import com.zeta.store.ui.theme.BosqueBorder
import com.zeta.store.ui.theme.BosqueGreen
import com.zeta.store.ui.theme.BosqueGreenDeep
import com.zeta.store.ui.theme.BosqueInk
import com.zeta.store.ui.theme.BosqueLime
import com.zeta.store.ui.theme.BosqueMuted
import com.zeta.store.ui.theme.BosqueSurface
import com.zeta.store.ui.theme.BosqueSurfaceSoft
import com.zeta.store.ui.theme.BosqueSurfaceStrong
import com.zeta.store.ui.theme.BosqueWarmth
import com.zeta.store.ui.theme.ZetaTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val oauthRedirect = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oauthRedirect.value = intent?.dataString
        enableEdgeToEdge()
        setContent {
            ZetaTheme {
                BosqueVivoStore(
                    oauthRedirect = oauthRedirect.value,
                    onOAuthHandled = { oauthRedirect.value = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        oauthRedirect.value = intent.dataString
    }
}

private data class Product(
    val id: String,
    val name: String,
    val latin: String,
    val format: String,
    val category: String,
    val price: Int,
    val offerPrice: Int?,
    val isFeaturedOffer: Boolean,
    val tag: String,
    val rating: String,
    val color: Color,
    val accent: Color,
    val benefits: List<String>,
    val stock: Int,
    val imageUrls: List<String>,
)

private data class StoreImageUpload(
    val path: String,
    val contentType: String,
    val bytes: ByteArray,
)

private val imageMemoryCache = ConcurrentHashMap<String, ImageBitmap>()

private val categories = listOf("Todos", "Tinturas", "Capsulas", "Polvos", "Kits")

private enum class AppTab {
    Home,
    Profile,
    Cart,
    Admin,
}

private val StoreHeaderTop = Color(0xFFE5F4D8)
private val StoreHeaderBottom = Color(0xFFD7F1E2)
private val StoreLink = Color(0xFF2F6F46)
private val StoreActionYellow = Color(0xFFEAC64A)
private val StoreSoftMint = Color(0xFFEAF7EA)
private val StoreLine = Color(0xFFCBD7C5)

private data class HeaderOption(
    val key: String,
    val text: String,
    val leading: AmazonIcon? = null,
)

private data class ProductSortOption(
    val key: String,
    val label: String,
)

private val productSortOptions = listOf(
    ProductSortOption("relevance", "Relevancia"),
    ProductSortOption("price_low", "Precio menor"),
    ProductSortOption("price_high", "Precio mayor"),
    ProductSortOption("popular", "Populares"),
    ProductSortOption("stock", "Stock"),
)

private const val DEFAULT_PRODUCT_SORT = "relevance"

private enum class PaymentMethodOption(
    val label: String,
    val detail: String,
    val badge: String,
) {
    Yape("Yape demo", "Validacion inmediata simulada", "QR"),
    Plin("Plin demo", "Pago movil simulado", "APP"),
    Card("Tarjeta demo", "Visa/Mastercard sin cobro real", "****"),
    Cash("Contra entrega", "Pago manual al recibir", "S/"),
}

private data class CheckoutSuccess(
    val orderCode: String,
    val itemCount: Int,
    val total: Int,
    val paymentMethod: String,
)

private data class AdminColorOption(
    val name: String,
    val colorHex: String,
    val accentHex: String,
    val color: Color,
    val accent: Color,
)

private data class AdminPromoPalette(
    val name: String,
    val colors: List<String>,
    val preview: List<Color>,
)

private val adminColorOptions = listOf(
    AdminColorOption("Verde bosque", "#426B35", "#DCEBB1", Color(0xFF426B35), Color(0xFFDCEBB1)),
    AdminColorOption("Reishi tierra", "#B56B47", "#F7D7BF", Color(0xFFB56B47), Color(0xFFF7D7BF)),
    AdminColorOption("Dorado melena", "#D7A13B", "#F8E4B2", Color(0xFFD7A13B), Color(0xFFF8E4B2)),
    AdminColorOption("Vital coral", "#C85D3C", "#F5C5AF", Color(0xFFC85D3C), Color(0xFFF5C5AF)),
    AdminColorOption("Menta clara", "#2F6F46", "#EAF7EA", StoreLink, StoreSoftMint),
)

private val adminBenefitOptions = listOf("Relax", "Energia", "Foco", "Noche", "Ritual", "Balance", "Cafe", "Regalo")

private val adminPromoPalettes = listOf(
    AdminPromoPalette("Bosque natural", listOf("#234428", "#5F7E3F", "#E7C56B"), listOf(Color(0xFF234428), Color(0xFF5F7E3F), Color(0xFFE7C56B))),
    AdminPromoPalette("Tierra calida", listOf("#44291F", "#8A5A36", "#E8B967"), listOf(Color(0xFF44291F), Color(0xFF8A5A36), Color(0xFFE8B967))),
    AdminPromoPalette("Menta herbal", listOf("#1E3B45", "#3F755D", "#D8E7B5"), listOf(Color(0xFF1E3B45), Color(0xFF3F755D), Color(0xFFD8E7B5))),
    AdminPromoPalette("Verde suave", listOf("#284B2E", "#2F6F46", "#DCEBB1"), listOf(BosqueGreenDeep, StoreLink, BosqueLime)),
)

private const val ADMIN_ORDER_NOTIFICATION_CHANNEL_ID = "admin_orders"
private const val ADMIN_NOTIFICATION_PREFS = "zeta_admin_notifications"

@Composable
fun BosqueVivoStore(oauthRedirect: String? = null, onOAuthHandled: () -> Unit = {}) {
    val context = LocalContext.current
    val supabaseClient = remember { SupabaseClient() }
    val scope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedSortKey by remember { mutableStateOf(DEFAULT_PRODUCT_SORT) }
    var query by remember { mutableStateOf("") }
    var session by remember { mutableStateOf(loadSavedSession(context)) }
    var productsMessage by remember { mutableStateOf<String?>(null) }
    var centerMessage by remember { mutableStateOf<String?>(null) }
    var remoteProducts by remember { mutableStateOf<List<ProductRecord>>(emptyList()) }
    var remotePromos by remember { mutableStateOf<List<HomePromoRecord>>(emptyList()) }
    var profile by remember { mutableStateOf<ProfileRecord?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var currentTab by remember { mutableStateOf(AppTab.Home) }
    var showSplash by remember { mutableStateOf(true) }
    var productsLoading by remember { mutableStateOf(false) }
    var checkingOut by remember { mutableStateOf(false) }
    var showCheckoutConfirmation by remember { mutableStateOf(false) }
    var checkoutSuccess by remember { mutableStateOf<CheckoutSuccess?>(null) }
    var myOrders by remember { mutableStateOf<List<OrderRecord>>(emptyList()) }
    var adminOrders by remember { mutableStateOf<List<OrderRecord>>(emptyList()) }
    var adminNotifications by remember { mutableStateOf<List<AdminNotificationRecord>>(emptyList()) }
    var addresses by remember { mutableStateOf<List<AddressRecord>>(emptyList()) }
    var deliveryMethod by remember { mutableStateOf("delivery") }
    var paymentMethod by remember { mutableStateOf(PaymentMethodOption.Yape) }
    var profileTargetTab by remember { mutableStateOf<String?>(null) }
    val cart = remember { mutableStateMapOf<String, Int>() }
    val savedProducts = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(centerMessage) {
        if (centerMessage != null) {
            delay(2200)
            centerMessage = null
        }
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showAdminOrderNotifications(
                context = context,
                notifications = adminNotifications.filterNot { it.isRead },
            )
        } else {
            productsMessage = "Activa notificaciones del sistema para recibir alertas de pedidos."
        }
    }

    fun refreshProfileFromSupabase(activeSession: SupabaseSession) {
        scope.launch {
            productsMessage = runCatching {
                val freshProfile = supabaseClient.fetchProfile(activeSession)
                profile = freshProfile
                addresses = supabaseClient.fetchAddresses(activeSession)
                if (freshProfile.isAdmin) {
                    remoteProducts = supabaseClient.fetchProducts(activeSession)
                    remotePromos = supabaseClient.fetchHomePromos(activeSession)
                    adminOrders = supabaseClient.fetchAdminOrders(activeSession)
                    adminNotifications = supabaseClient.fetchAdminNotifications(activeSession)
                    "Rol actualizado: admin."
                } else {
                    "Rol actual: ${freshProfile.role}."
                }
            }.getOrElse { error ->
                error.message ?: "No pude refrescar el rol."
            }
        }
    }

    fun persistCartItem(productId: String, quantity: Int) {
        val activeSession = session ?: return
        scope.launch {
            runCatching {
                if (quantity > 0) {
                    supabaseClient.saveCartItem(activeSession, productId, quantity)
                } else {
                    supabaseClient.deleteCartItem(activeSession, productId)
                }
            }.onFailure { error ->
                productsMessage = error.message ?: "No pude actualizar el carrito."
            }
        }
    }

    fun addToCart(product: Product) {
        val next = (cart[product.id] ?: 0) + 1
        cart[product.id] = next
        persistCartItem(product.id, next)
    }

    fun removeFromCart(product: Product) {
        val next = (cart[product.id] ?: 0) - 1
        if (next <= 0) {
            cart.remove(product.id)
            persistCartItem(product.id, 0)
        } else {
            cart[product.id] = next
            persistCartItem(product.id, next)
        }
    }

    fun saveForLater(product: Product) {
        val activeSession = session
        if (activeSession == null) {
            productsMessage = "Inicia sesion para guardar productos."
            currentTab = AppTab.Profile
            return
        }

        savedProducts[product.id] = true
        cart.remove(product.id)
        scope.launch {
            productsMessage = runCatching {
                supabaseClient.saveProductForLater(activeSession, product.id)
                supabaseClient.deleteCartItem(activeSession, product.id)
                "Producto guardado para despues."
            }.getOrElse { error ->
                error.message ?: "No pude guardar el producto."
            }
        }
    }

    fun removeSavedProduct(product: Product) {
        val activeSession = session ?: return
        savedProducts.remove(product.id)
        scope.launch {
            runCatching { supabaseClient.deleteSavedProduct(activeSession, product.id) }
                .onFailure { error -> productsMessage = error.message ?: "No pude quitar el producto guardado." }
        }
    }

    fun moveSavedProductToCart(product: Product) {
        addToCart(product)
        removeSavedProduct(product)
    }

    LaunchedEffect("splash") {
        delay(1800)
        showSplash = false
    }

    LaunchedEffect(Unit) {
        if (!supabaseClient.isConfigured) {
            productsMessage = "Configura Supabase para cargar productos, anuncios e imagenes."
            return@LaunchedEffect
        }

        productsLoading = true
        productsMessage = runCatching {
            remoteProducts = supabaseClient.fetchProducts()
            remotePromos = supabaseClient.fetchHomePromos()
            if (remoteProducts.isEmpty()) "No hay productos activos en Supabase." else null
        }.getOrElse { error ->
            "No pudimos cargar la tienda. Revisa tu conexion e intenta nuevamente."
        }
        productsLoading = false
    }

    LaunchedEffect(session) {
        val activeSession = session
        if (activeSession == null) {
            profile = null
            myOrders = emptyList()
            adminOrders = emptyList()
            adminNotifications = emptyList()
            addresses = emptyList()
            savedProducts.clear()
            cart.clear()
            productsMessage = null
            clearSavedSession(context)
            return@LaunchedEffect
        }

        runCatching {
            saveSession(context, activeSession)
            profile = supabaseClient.fetchProfile(activeSession)
            val pendingCart = cart.toMap()
            val remoteCart = supabaseClient.fetchCartItems(activeSession).toMutableMap()
            pendingCart.forEach { (productId, quantity) ->
                remoteCart[productId] = (remoteCart[productId] ?: 0) + quantity
            }
            remoteCart.forEach { (productId, quantity) ->
                supabaseClient.saveCartItem(activeSession, productId, quantity)
            }
            cart.clear()
            cart.putAll(remoteCart)
            savedProducts.clear()
            savedProducts.putAll(supabaseClient.fetchSavedProductIds(activeSession).associateWith { true })
            addresses = supabaseClient.fetchAddresses(activeSession)
            myOrders = supabaseClient.fetchMyOrders(activeSession)
            if (profile?.isAdmin == true) {
                remoteProducts = supabaseClient.fetchProducts(activeSession)
                remotePromos = supabaseClient.fetchHomePromos(activeSession)
                adminOrders = supabaseClient.fetchAdminOrders(activeSession)
                adminNotifications = supabaseClient.fetchAdminNotifications(activeSession)
            }
        }.onFailure { error ->
            productsMessage = error.message ?: "No pude leer el perfil."
        }
    }

    LaunchedEffect(oauthRedirect) {
        val redirect = oauthRedirect ?: return@LaunchedEffect
        runCatching {
            supabaseClient.sessionFromOAuthRedirect(redirect)
        }.onSuccess { googleSession ->
            if (googleSession != null) {
                session = googleSession
                productsMessage = null
            }
            onOAuthHandled()
        }.onFailure { error ->
            productsMessage = error.message ?: "No se pudo completar Google login."
            onOAuthHandled()
        }
    }

    LaunchedEffect(profile?.isAdmin, adminNotifications) {
        val unreadNotifications = adminNotifications.filterNot { it.isRead }
        if (profile?.isAdmin != true || unreadNotifications.isEmpty()) return@LaunchedEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showAdminOrderNotifications(context, unreadNotifications)
        }
    }

    LaunchedEffect(session?.userId, profile?.isAdmin) {
        val activeSession = session
        if (activeSession == null || profile?.isAdmin != true) return@LaunchedEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (token.isBlank()) return@addOnSuccessListener
                context.getSharedPreferences(ZetaFirebaseMessagingService.PUSH_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putString(ZetaFirebaseMessagingService.PUSH_TOKEN_KEY, token)
                    .apply()
                scope.launch {
                    runCatching {
                        supabaseClient.registerPushToken(
                            session = activeSession,
                            token = token,
                            deviceName = "Android ${Build.MODEL}",
                        )
                    }.onFailure { error ->
                        productsMessage = error.message ?: "No pude activar notificaciones push."
                    }
                }
            }
            .addOnFailureListener { error ->
                productsMessage = error.message ?: "No pude obtener el token push."
            }
    }

    val products = remember(remoteProducts) {
        remoteProducts.filter { it.isActive }.map { it.toProduct() }
    }
    val homePromos = remember(remotePromos) {
        remotePromos.filter { it.isActive }.map { it.toPromo() }
    }
    val cartCount = cart.values.sum()
    val subtotal = products.sumOf { product -> (cart[product.id] ?: 0) * product.currentPrice }
    val defaultAddress = remember(addresses) {
        addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
    }
    val checkoutPhone = remember(profile?.phone, defaultAddress?.phone) {
        profile?.phone?.trim()?.takeIf { it.isNotBlank() }
            ?: defaultAddress?.phone?.trim()?.takeIf { it.isNotBlank() }
    }
    val visibleProducts = remember(products, selectedCategory, query, selectedSortKey) {
        val search = query.trim().lowercase()
        val filtered = products.filter { product ->
            val haystack = "${product.name} ${product.latin} ${product.format} ${product.category} ${product.tag}".lowercase()
            (selectedCategory == "Todos" || product.category == selectedCategory) &&
                (search.isBlank() || haystack.contains(search))
        }
        when (selectedSortKey) {
            "price_low" -> filtered.sortedWith(compareBy<Product> { it.currentPrice }.thenBy { it.name })
            "price_high" -> filtered.sortedWith(compareByDescending<Product> { it.currentPrice }.thenBy { it.name })
            "popular" -> filtered.sortedWith(compareByDescending<Product> { it.popularityScore() }.thenBy { it.name })
            "stock" -> filtered.sortedWith(compareByDescending<Product> { it.stock }.thenBy { it.name })
            else -> filtered
        }
    }

    fun checkoutCart() {
        val activeSession = session
        if (activeSession == null) {
            productsMessage = "Inicia sesion para completar el checkout."
            currentTab = AppTab.Profile
            return
        }

        val orderItems = products
            .filter { product -> (cart[product.id] ?: 0) > 0 }
            .map { product ->
                OrderItemInput(
                    productId = product.id,
                    productName = product.name,
                    quantity = cart[product.id] ?: 1,
                    unitPrice = product.currentPrice,
                    imageUrl = product.imageUrls.firstOrNull().orEmpty(),
                )
            }

        if (orderItems.isEmpty()) {
            productsMessage = "Agrega productos antes de hacer checkout."
            return
        }
        if (checkoutPhone.isNullOrBlank()) {
            productsMessage = "Agrega un numero de telefono en Cuenta antes de confirmar tu pedido."
            profileTargetTab = "cuenta"
            currentTab = AppTab.Profile
            return
        }

        scope.launch {
            checkingOut = true
            showCheckoutConfirmation = false
            productsMessage = "Preparando tu pedido simulado..."
            delay(850)
            productsMessage = runCatching {
                val defaultAddressId = if (deliveryMethod == "delivery") {
                    profile?.defaultAddressId?.asRealId() ?: defaultAddress?.id?.asRealId()
                } else {
                    null
                }
                productsMessage = "Validando ${paymentMethod.label.lowercase()}..."
                delay(450)
                productsMessage = "Reservando stock del pedido..."
                delay(450)
                val order = supabaseClient.createOrder(
                    session = activeSession,
                    items = orderItems,
                    addressId = defaultAddressId,
                    deliveryMethod = if (defaultAddressId == null) "pickup" else "delivery",
                )
                orderItems.forEach { item -> supabaseClient.deleteCartItem(activeSession, item.productId) }
                cart.clear()
                remoteProducts = supabaseClient.fetchProducts(activeSession)
                myOrders = supabaseClient.fetchMyOrders(activeSession)
                if (profile?.isAdmin == true) {
                    adminOrders = supabaseClient.fetchAdminOrders(activeSession)
                    adminNotifications = supabaseClient.fetchAdminNotifications(activeSession)
                }
                checkoutSuccess = CheckoutSuccess(
                    orderCode = order.id.take(8).uppercase(),
                    itemCount = orderItems.sumOf { it.quantity },
                    total = orderItems.sumOf { it.quantity * it.unitPrice },
                    paymentMethod = paymentMethod.label,
                )
                "Pago demo aprobado y pedido registrado."
            }.getOrElse { error ->
                error.message?.let { "Checkout no completado: $it" }
                    ?: "No pudimos completar el checkout. Intenta nuevamente en un momento."
            }
            checkingOut = false
        }
    }

    fun requestCheckoutConfirmation() {
        if (session == null) {
            productsMessage = "Inicia sesion para completar el checkout."
            currentTab = AppTab.Profile
            return
        }
        if (cart.values.sum() == 0) {
            productsMessage = "Agrega productos antes de hacer checkout."
            return
        }
        if (checkoutPhone.isNullOrBlank()) {
            productsMessage = "Agrega un numero de telefono en Cuenta antes de confirmar tu pedido."
            profileTargetTab = "cuenta"
            currentTab = AppTab.Profile
            return
        }
        if (deliveryMethod == "delivery" && defaultAddress == null) {
            productsMessage = "Completa tus datos de entrega antes de confirmar. Tambien puedes elegir retiro en carrito."
            profileTargetTab = "cuenta"
            currentTab = AppTab.Profile
            return
        }
        showCheckoutConfirmation = true
    }

    fun saveCustomerPhone(phone: String) {
        val activeSession = session
        if (activeSession == null) {
            productsMessage = "Inicia sesion para guardar tu telefono."
            currentTab = AppTab.Profile
            return
        }
        scope.launch {
            productsMessage = runCatching {
                profile = runCatching {
                    supabaseClient.updateProfilePhone(activeSession, phone)
                }.getOrElse { error ->
                    if (!error.isJwtExpired()) throw error
                    val refreshedSession = supabaseClient.refreshSession(activeSession.refreshToken)
                    session = refreshedSession
                    supabaseClient.updateProfilePhone(refreshedSession, phone)
                }
                "Telefono guardado para tus pedidos."
            }.getOrElse { error -> error.message ?: "No pude guardar el telefono." }
        }
    }

    fun saveCustomerAddress(address: AddressRecord) {
        val activeSession = session
        if (activeSession == null) {
            productsMessage = "Inicia sesion para guardar direcciones."
            currentTab = AppTab.Profile
            return
        }
        scope.launch {
            productsMessage = runCatching {
                supabaseClient.saveAddress(activeSession, address)
                addresses = supabaseClient.fetchAddresses(activeSession)
                profile = supabaseClient.fetchProfile(activeSession)
                "Direccion guardada para checkout."
            }.getOrElse { error -> error.message ?: "No pude guardar la direccion." }
        }
    }

    fun deleteCustomerAddress(address: AddressRecord) {
        val activeSession = session ?: return
        scope.launch {
            productsMessage = runCatching {
                supabaseClient.deleteAddress(activeSession, address.id)
                addresses = supabaseClient.fetchAddresses(activeSession)
                profile = supabaseClient.fetchProfile(activeSession)
                "Direccion eliminada."
            }.getOrElse { error -> error.message ?: "No pude eliminar la direccion." }
        }
    }

    LaunchedEffect(products, homePromos) {
        val urls = (products.flatMap { it.imageUrls } + homePromos.map { it.imageUrl })
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        urls.forEach { url ->
            if (!imageMemoryCache.containsKey(url)) {
                loadRemoteImageBitmap(url)?.let { imageMemoryCache[url] = it }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF5F2E8), BosqueBackground)))
    ) {
        when (currentTab) {
            AppTab.Home -> HomeScreen(
                cartCount = cartCount,
                session = session,
                profile = profile,
                query = query,
                onQueryChange = { query = it },
                productsMessage = productsMessage,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                selectedSortKey = selectedSortKey,
                onSortSelected = { selectedSortKey = it },
                productsLoading = productsLoading,
                products = products,
                promos = homePromos,
                visibleProducts = visibleProducts,
                cart = cart,
                onProductSelected = { selectedProduct = it },
                onAddToCart = ::addToCart,
                onCartClick = { currentTab = AppTab.Cart },
                onAdminClick = { currentTab = AppTab.Admin },
                onDismissMessage = { productsMessage = null },
            )

            AppTab.Profile -> ProfileScreen(
                client = supabaseClient,
                products = products,
                orders = myOrders,
                savedProductIds = savedProducts.keys,
                session = session,
                profile = profile,
                addresses = addresses,
                defaultAddress = defaultAddress,
                initialTab = profileTargetTab,
                productsMessage = productsMessage,
                cartCount = cartCount,
                query = query,
                selectedCategory = selectedCategory,
                onQueryChange = {
                    query = it
                    currentTab = AppTab.Home
                },
                onCategorySelected = {
                    selectedCategory = it
                    currentTab = AppTab.Home
                },
                onCartClick = { currentTab = AppTab.Cart },
                onAdminClick = { currentTab = AppTab.Admin },
                onSessionChanged = { session = it },
                onRefreshProfile = { session?.let(::refreshProfileFromSupabase) },
                onSavePhone = ::saveCustomerPhone,
                onSaveAddress = ::saveCustomerAddress,
                onDeleteAddress = ::deleteCustomerAddress,
                onInitialTabConsumed = { profileTargetTab = null },
                onMessage = { productsMessage = it },
                onDismissMessage = { productsMessage = null },
                onLogout = {
                    session = null
                    productsMessage = "Sesion cerrada."
                    currentTab = AppTab.Profile
                },
            )

            AppTab.Cart -> CartScreen(
                products = products,
                orders = myOrders,
                cart = cart,
                savedProductIds = savedProducts.keys,
                subtotal = subtotal,
                cartCount = cartCount,
                session = session,
                profile = profile,
                defaultAddress = defaultAddress,
                deliveryMethod = deliveryMethod,
                paymentMethod = paymentMethod,
                productsMessage = productsMessage,
                query = query,
                selectedCategory = selectedCategory,
                onQueryChange = {
                    query = it
                    currentTab = AppTab.Home
                },
                onCategorySelected = {
                    selectedCategory = it
                    currentTab = AppTab.Home
                },
                onCartClick = { currentTab = AppTab.Cart },
                onAdminClick = { currentTab = AppTab.Admin },
                onAdd = ::addToCart,
                onRemove = ::removeFromCart,
                onSaveForLater = ::saveForLater,
                onRemoveSaved = ::removeSavedProduct,
                onMoveSavedToCart = ::moveSavedProductToCart,
                onCheckout = ::requestCheckoutConfirmation,
                onDeliveryMethodChange = { deliveryMethod = it },
                onPaymentMethodChange = { paymentMethod = it },
                checkingOut = checkingOut,
                onShop = { currentTab = AppTab.Home },
                onDismissMessage = { productsMessage = null },
            )

            AppTab.Admin -> {
                if (profile?.isAdmin == true && session != null) {
                    AdminScreen(
                        client = supabaseClient,
                        products = remoteProducts,
                        promos = remotePromos,
                        orders = adminOrders,
                        notifications = adminNotifications,
                        cartCount = cartCount,
                        session = session,
                        profile = profile,
                        query = query,
                        selectedCategory = selectedCategory,
                        onQueryChange = {
                            query = it
                            currentTab = AppTab.Home
                        },
                        onCategorySelected = {
                            selectedCategory = it
                            currentTab = AppTab.Home
                        },
                        onCartClick = { currentTab = AppTab.Cart },
                        onAdminClick = { currentTab = AppTab.Admin },
                        onSave = { product ->
                            scope.launch {
                                runCatching {
                                    supabaseClient.saveProduct(session!!, product)
                                    remoteProducts = supabaseClient.fetchProducts(session!!)
                                    productsMessage = null
                                    centerMessage = "Producto guardado"
                                }.onFailure { error ->
                                    productsMessage = error.message ?: "No se pudo guardar el producto."
                                }
                            }
                        },
                        onSavePromo = { promo ->
                            scope.launch {
                                productsMessage = runCatching {
                                    val saved = supabaseClient.saveHomePromo(session!!, promo)
                                    remotePromos = supabaseClient.fetchHomePromos(session!!)
                                    "Anuncio guardado: ${saved.title}"
                                }.getOrElse { error -> error.message ?: "No se pudo guardar el anuncio." }
                            }
                        },
                        onDeleteProduct = { product ->
                            scope.launch {
                                productsMessage = runCatching {
                                    supabaseClient.deleteProduct(session!!, product.id)
                                    remoteProducts = supabaseClient.fetchProducts(session!!)
                                    "Producto eliminado: ${product.name}"
                                }.getOrElse { error -> error.message ?: "No se pudo eliminar el producto." }
                            }
                        },
                        onDeletePromo = { promo ->
                            scope.launch {
                                productsMessage = runCatching {
                                    supabaseClient.deleteHomePromo(session!!, promo.id)
                                    remotePromos = supabaseClient.fetchHomePromos(session!!)
                                    "Anuncio eliminado: ${promo.title}"
                                }.getOrElse { error -> error.message ?: "No se pudo eliminar el anuncio." }
                            }
                        },
                        onUpdateOrderStatus = { order, status ->
                            scope.launch {
                                productsMessage = runCatching {
                                    supabaseClient.updateOrderStatus(session!!, order.id, status)
                                    adminOrders = supabaseClient.fetchAdminOrders(session!!)
                                    "Pedido ${order.id.take(8)} actualizado."
                                }.getOrElse { error -> error.message ?: "No se pudo actualizar el pedido." }
                            }
                        },
                        onRefreshOrders = {
                            scope.launch {
                                productsMessage = runCatching {
                                    adminOrders = supabaseClient.fetchAdminOrders(session!!)
                                    adminNotifications = supabaseClient.fetchAdminNotifications(session!!)
                                    adminNotifications.filterNot { it.isRead }.forEach { notification ->
                                        supabaseClient.markAdminNotificationRead(session!!, notification.id)
                                    }
                                    adminNotifications = supabaseClient.fetchAdminNotifications(session!!)
                                    "Pedidos actualizados."
                                }.getOrElse { error -> error.message ?: "No pude actualizar pedidos." }
                            }
                        },
                        onMessage = { productsMessage = it },
                    )
                } else {
                    AdminLockedScreen(
                        onProfileClick = { currentTab = AppTab.Profile },
                        onRefreshProfile = { session?.let(::refreshProfileFromSupabase) },
                    )
                }
            }
        }

        BottomNavBar(
            selected = currentTab,
            count = cartCount,
            subtotal = subtotal,
            isAdmin = profile?.isAdmin == true,
            onSelected = { currentTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )

        if (showSplash) {
            ZetaSplashScreen(modifier = Modifier.fillMaxSize())
        }

    }

    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            relatedProducts = products.filter { it.id != product.id && (it.category == product.category || it.benefits.any { benefit -> product.benefits.contains(benefit) }) }.take(8),
            quantity = cart[product.id] ?: 0,
            onDismiss = { selectedProduct = null },
            onAdd = { addToCart(product) },
            onRelatedSelected = { related -> selectedProduct = related },
        )
    }
    if (showCheckoutConfirmation) {
        CheckoutConfirmationDialog(
            itemCount = cartCount,
            subtotal = subtotal,
            deliveryMethod = deliveryMethod,
            paymentMethod = paymentMethod,
            checkingOut = checkingOut,
            onDismiss = { showCheckoutConfirmation = false },
            onConfirm = ::checkoutCart,
        )
    }
    checkoutSuccess?.let { success ->
        CheckoutSuccessDialog(
            success = success,
            onDismiss = { checkoutSuccess = null },
            onViewOrders = {
                checkoutSuccess = null
                currentTab = AppTab.Profile
            },
        )
    }
    centerMessage?.let { message ->
        CenterStatusToastDialog(
            message = message,
            onDismiss = { centerMessage = null },
        )
    }

}

@Composable
private fun ZetaSplashScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(BosqueGreenDeep)) {
        Image(
            painter = painterResource(R.drawable.zeta_dorada_splash_display),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x66284B2E),
                            Color.Transparent,
                            Color(0xAA284B2E),
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.zeta_dorada_logo_display),
                contentDescription = "Zeta Dorada",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xEEFFFDF4))
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            )
            Box(
                modifier = Modifier
                    .width(112.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(StoreActionYellow)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun HomeScreen(
    cartCount: Int,
    session: SupabaseSession?,
    profile: ProfileRecord?,
    query: String,
    onQueryChange: (String) -> Unit,
    productsMessage: String?,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    selectedSortKey: String,
    onSortSelected: (String) -> Unit,
    productsLoading: Boolean,
    products: List<Product>,
    promos: List<Promo>,
    visibleProducts: List<Product>,
    cart: MutableMap<String, Int>,
    onProductSelected: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    val listState = rememberLazyListState()
    var showHeaderFilters by remember { mutableStateOf(true) }
    val showFeaturedOffers = selectedCategory == "Todos" &&
        selectedSortKey == DEFAULT_PRODUCT_SORT &&
        query.isBlank()
    val offerProducts = remember(products, showFeaturedOffers) {
        if (showFeaturedOffers) products.filter { it.isOfferProduct() }.take(10) else emptyList()
    }

    LaunchedEffect(listState) {
        var previousIndex = listState.firstVisibleItemIndex
        var previousOffset = listState.firstVisibleItemScrollOffset
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val scrollingDown = index > previousIndex || (index == previousIndex && offset > previousOffset)
                val scrollingUp = index < previousIndex || (index == previousIndex && offset < previousOffset)
                val nearTop = index == 0 && offset < 12

                if (nearTop || scrollingUp) {
                    showHeaderFilters = true
                } else if (scrollingDown && (index > 0 || offset > 28)) {
                    showHeaderFilters = false
                }

                previousIndex = index
                previousOffset = offset
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        stickyHeader(key = "home-header") {
            AmazonStyleHeader(
                cartCount = cartCount,
                email = session?.email,
                isAdmin = profile?.isAdmin == true,
                query = query,
                onQueryChange = onQueryChange,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                selectedSortKey = selectedSortKey,
                onSortSelected = onSortSelected,
                onCartClick = onCartClick,
                onAdminClick = onAdminClick,
                showFilters = showHeaderFilters,
            )
        }
        item {
            Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                PromoCarousel(promos = promos)
            }
        }
        productsMessage?.let { message ->
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    StatusPill(message = message, onDismiss = onDismissMessage)
                }
            }
        }
        if (offerProducts.isNotEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    SectionTitle(title = "Ofertas destacadas", detail = "${offerProducts.size} promos y combos")
                }
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(offerProducts, key = { it.id }) { product ->
                        DealTile(product = product, onClick = { onProductSelected(product) })
                    }
                }
            }
        }
        item {
            Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                SectionTitle(title = "Resultados", detail = "Entrega simulada en Lima")
            }
        }
        if (productsLoading && products.isEmpty()) {
            item {
                ProductListSkeleton(modifier = Modifier.padding(horizontal = 14.dp))
            }
        } else if (visibleProducts.isEmpty()) {
            item {
                EmptyStateCard(
                    title = if (query.isBlank()) "No hay productos en esta vista" else "Sin resultados para \"$query\"",
                    detail = "Prueba con otra categoria del navbar o busca reishi, foco, capsulas o kit.",
                    action = if (query.isBlank()) null else "Limpiar busqueda",
                    onAction = { onQueryChange("") },
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
        } else {
            items(visibleProducts, key = { it.id }) { product ->
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    ProductRow(
                        product = product,
                        quantity = cart[product.id] ?: 0,
                        onClick = { onProductSelected(product) },
                        onAdd = { onAddToCart(product) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    client: SupabaseClient,
    products: List<Product>,
    orders: List<OrderRecord>,
    savedProductIds: Set<String>,
    session: SupabaseSession?,
    profile: ProfileRecord?,
    addresses: List<AddressRecord>,
    defaultAddress: AddressRecord?,
    initialTab: String?,
    productsMessage: String?,
    cartCount: Int,
    query: String,
    selectedCategory: String,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSessionChanged: (SupabaseSession?) -> Unit,
    onRefreshProfile: () -> Unit,
    onSavePhone: (String) -> Unit,
    onSaveAddress: (AddressRecord) -> Unit,
    onDeleteAddress: (AddressRecord) -> Unit,
    onInitialTabConsumed: () -> Unit,
    onMessage: (String?) -> Unit,
    onDismissMessage: () -> Unit,
    onLogout: () -> Unit,
) {
    var profileTab by remember { mutableStateOf("cuenta") }
    LaunchedEffect(initialTab) {
        val target = initialTab ?: return@LaunchedEffect
        profileTab = target
        onInitialTabConsumed()
    }
    val profileOptions = remember(profile?.isAdmin) {
        buildList {
            add(HeaderOption("cuenta", "Cuenta"))
            add(HeaderOption("pedidos", "Pedidos"))
            if (profile?.isAdmin == true) add(HeaderOption("admin", "Admin", AmazonIcon.Menu)) else add(HeaderOption("listas", "Mi Lista"))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(if (session == null) 10.dp else 14.dp),
    ) {
        item {
            AccountHeader(
                signedIn = session != null,
            )
        }
        if (session != null) {
            item {
                AccountTabRow(
                    options = profileOptions,
                    selectedKey = profileTab,
                    onSelected = { key ->
                        if (key == "admin") {
                            onAdminClick()
                        } else {
                            profileTab = key
                        }
                    },
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
        }
        productsMessage?.let { message ->
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    StatusPill(message = message, onDismiss = onDismissMessage)
                }
            }
        }
        item {
            if (session == null) {
                ProfileAccountHub(
                    email = null,
                    role = profile?.role ?: "customer",
                    isAdmin = false,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            } else {
                AccountOverviewSection(
                    email = session.email,
                    displayName = profile?.fullName?.takeIf { it.isNotBlank() },
                    role = profile?.role ?: "customer",
                    isAdmin = profile?.isAdmin == true,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
        }
        if (session == null) {
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    AuthStrip(
                        client = client,
                        session = session,
                        onSessionChanged = onSessionChanged,
                        onMessage = onMessage,
                    )
                }
            }
        }
        when (profileTab) {
            "pedidos" -> {
                if (session != null) {
                    item {
                        ProfileOrdersSection(orders = orders, modifier = Modifier.padding(horizontal = 14.dp))
                    }
                }
            }
            "cuenta" -> {
                if (session != null) {
                    item {
                        ProfileCustomerDetailsCard(
                            email = session.email,
                            profile = profile,
                            defaultAddress = defaultAddress,
                            onSavePhone = onSavePhone,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    item {
                        AddressBookSection(
                            session = session,
                            recipientNameDefault = profile?.fullName?.trim()?.takeIf { it.isNotBlank() } ?: "Cliente Zeta",
                            addresses = addresses,
                            onSave = onSaveAddress,
                            onDelete = onDeleteAddress,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    item {
                        LogoutSection(
                            email = session.email,
                            onLogout = onLogout,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                }
            }
            "listas" -> {
                item {
                    ProfileSavedLists(
                        products = products.filter { savedProductIds.contains(it.id) },
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSavedLists(products: List<Product>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "Tus listas", detail = "${products.size} guardados")
        if (products.isEmpty()) {
            Text(text = "Los productos que guardes para despues apareceran aqui.", color = BosqueMuted, fontSize = 13.sp)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products.take(8), key = { it.id }) { product ->
                    DealTile(product = product, onClick = {})
                }
            }
        }
    }
}

@Composable
private fun ProfileAccountHub(
    email: String?,
    role: String,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
) {
    val name = email?.substringBefore("@")?.ifBlank { "cliente" } ?: "invitado"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(StoreHeaderBottom),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.User, tint = StoreLink, modifier = Modifier.size(31.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hola, $name",
                    color = BosqueInk,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (email == null) "Entra para guardar tus pedidos, direcciones y preferencias." else if (isAdmin) "Admin conectado" else "Cuenta Zeta Dorada",
                    color = BosqueMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun AccountOverviewSection(
    email: String,
    displayName: String?,
    role: String,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
) {
    val name = displayName?.takeIf { it.isNotBlank() }
        ?: email.substringBefore("@").ifBlank { "cliente" }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(StoreSoftMint)
                    .border(1.dp, BosqueBorder.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.User, tint = StoreLink, modifier = Modifier.size(30.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hola, $name",
                    color = BosqueInk,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (isAdmin) "Administrador Zeta Dorada" else "Cliente Zeta Dorada",
                    color = BosqueMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = if (isAdmin) "ADMIN" else role.uppercase(),
                color = BosqueGreenDeep,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(BosqueSurfaceStrong)
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun ProfileRoundIcon(icon: AmazonIcon) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(BosqueSurfaceSoft)
            .border(1.dp, BosqueBorder, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AmazonLineIcon(icon = icon, tint = BosqueInk, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ProfileActionPills(isAdmin: Boolean, modifier: Modifier = Modifier) {
    val labels = if (isAdmin) {
        listOf("Cuenta", "Pedidos", "Admin")
    } else {
        listOf("Cuenta", "Pedidos", "Mi Lista")
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(labels) { label ->
            Box(
                modifier = Modifier
                    .width(112.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White)
                    .border(1.dp, StoreLine, RoundedCornerShape(999.dp))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = BosqueInk,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ProfileCustomerDetailsCard(
    email: String,
    profile: ProfileRecord?,
    defaultAddress: AddressRecord?,
    onSavePhone: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPhoneEditor by remember { mutableStateOf(false) }
    val name = profile?.fullName?.takeIf { it.isNotBlank() }
        ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "Cliente Zeta" }
    val savedPhone = profile?.phone?.takeIf { it.isNotBlank() } ?: defaultAddress?.phone?.takeIf { it.isNotBlank() }
    val phone = savedPhone ?: "Pendiente"
    val addressLine = defaultAddress?.let { "${it.line1}, ${it.district}" } ?: "Agrega una direccion principal"
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "ZD" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.82f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(StoreSoftMint)
                    .border(1.dp, BosqueBorder.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = initials, color = StoreLink, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "Datos de cliente", color = BosqueInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(text = "Informacion para pedidos y entrega", color = BosqueMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Activo",
                color = BosqueGreenDeep,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(BosqueSurfaceStrong)
                    .padding(horizontal = 9.dp, vertical = 6.dp)
            )
        }
        CustomerInfoRow("Nombre", name)
        CustomerInfoRow("Email", email)
        CustomerInfoRow("Telefono", phone)
        Button(
            onClick = { showPhoneEditor = true },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (savedPhone.isNullOrBlank()) StoreActionYellow else BosqueSurfaceStrong,
                contentColor = BosqueInk,
            ),
        ) {
            Text(text = if (savedPhone.isNullOrBlank()) "Agregar telefono" else "Editar telefono", fontWeight = FontWeight.Black)
        }
        CustomerInfoRow("Direccion principal", addressLine)
        Text(
            text = "Estos datos alimentan el checkout simulado y el pedido que vera Admin.",
            color = BosqueMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        )
    }

    if (showPhoneEditor) {
        PhoneEditorDialog(
            initialPhone = savedPhone.orEmpty(),
            onDismiss = { showPhoneEditor = false },
            onSave = { phoneValue ->
                onSavePhone(phoneValue)
                showPhoneEditor = false
            },
        )
    }
}

@Composable
private fun CustomerInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BosqueSurfaceSoft)
            .border(1.dp, BosqueBorder.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(text = label, color = BosqueMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = BosqueInk, fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun PhoneEditorDialog(initialPhone: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var localDigits by remember(initialPhone) { mutableStateOf(peruMobileDigits(initialPhone)) }
    val formattedLocal = formatPeruMobileLocal(localDigits)
    val canSave = localDigits.length == 9 && localDigits.startsWith("9")
    val savedPhone = "+51 $formattedLocal"
    val statusText = when {
        localDigits.isEmpty() -> "Escribe los 9 digitos."
        !localDigits.startsWith("9") -> "Debe empezar con 9."
        localDigits.length < 9 -> "Faltan ${9 - localDigits.length} digitos."
        else -> "Telefono listo."
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .border(1.dp, BosqueBorder.copy(alpha = 0.78f), RoundedCornerShape(26.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(StoreSoftMint, BosqueSurfaceStrong)))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.86f))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    PeruFlagMark()
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(text = "Agregar telefono", color = BosqueInk, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(text = "Numero de contacto para tus pedidos.", color = BosqueMuted, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }

            PeruPhoneInput(
                digits = localDigits,
                onDigitsChange = { localDigits = it },
            )
            PeruPhonePreview(digits = localDigits, valid = canSave)
            Text(
                text = statusText,
                color = if (canSave) StoreLink else BosqueWarmth,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BosqueInk),
                ) {
                    Text(text = "Cancelar", fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = { onSave(savedPhone.trim()) },
                    enabled = canSave,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                ) {
                    Text(text = "Guardar", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun PeruFlagMark() {
    Row(
        modifier = Modifier
            .width(28.dp)
            .height(18.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp)),
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxSize().background(Color(0xFFD91023)))
        Box(modifier = Modifier.weight(1f).fillMaxSize().background(Color.White))
        Box(modifier = Modifier.weight(1f).fillMaxSize().background(Color(0xFFD91023)))
    }
}

@Composable
private fun PeruPhoneInput(digits: String, onDigitsChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BosqueSurfaceSoft)
            .border(1.dp, if (digits.length == 9 && digits.startsWith("9")) StoreLink else BosqueBorder, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            modifier = Modifier
                .width(68.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, StoreLine, RoundedCornerShape(14.dp))
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "PE", color = BosqueMuted, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(text = "+51", color = BosqueInk, fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        BasicTextField(
            value = digits,
            onValueChange = { value -> onDigitsChange(value.filter(Char::isDigit).take(9)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (digits.isBlank()) {
                        Text(text = "987654321", color = BosqueMuted.copy(alpha = 0.42f), fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun PeruPhonePreview(digits: String, valid: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(0, 3, 6).forEach { start ->
            val group = digits.drop(start).take(3)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (group.length == 3) StoreSoftMint else BosqueSurfaceSoft)
                    .border(1.dp, if (valid) StoreLink.copy(alpha = 0.65f) else BosqueBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = group.padEnd(3, '_'),
                    color = if (group.isBlank()) BosqueMuted.copy(alpha = 0.45f) else BosqueInk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

private fun peruMobileDigits(phone: String): String {
    val digits = phone.filter(Char::isDigit)
    val withoutCountry = if (digits.startsWith("51") && digits.length > 9) digits.drop(2) else digits
    return withoutCountry.takeLast(9).takeIf { it.startsWith("9") }.orEmpty()
}

private fun formatPeruMobileLocal(digits: String): String =
    digits.chunked(3).joinToString(" ")

@Composable
private fun AddressBookSection(
    session: SupabaseSession,
    recipientNameDefault: String,
    addresses: List<AddressRecord>,
    onSave: (AddressRecord) -> Unit,
    onDelete: (AddressRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingAddress by remember { mutableStateOf<AddressRecord?>(null) }
    var showNewAddress by remember { mutableStateOf(false) }
    val defaultAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(title = "Direcciones", detail = if (addresses.isEmpty()) "sin guardar" else "${addresses.size} guardadas")
        if (defaultAddress == null) {
            EmptyStateCard(
                title = "Agrega una direccion de entrega",
                detail = "Asi el checkout simulado puede crear pedidos con datos de cliente y delivery.",
                action = "Agregar direccion",
                onAction = { showNewAddress = true },
            )
        } else {
            AddressCard(
                address = defaultAddress,
                onEdit = { editingAddress = defaultAddress },
                onDelete = { onDelete(defaultAddress) },
            )
            if (addresses.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(addresses.filterNot { it.id == defaultAddress.id }, key = { it.id }) { address ->
                        CompactAddressCard(
                            address = address,
                            onClick = { editingAddress = address },
                        )
                    }
                }
            }
            Button(
                onClick = { showNewAddress = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = "Agregar otra direccion", fontWeight = FontWeight.Black)
            }
        }
    }

    if (showNewAddress) {
        AddressEditorDialog(
            session = session,
            recipientNameDefault = recipientNameDefault,
            address = null,
            onDismiss = { showNewAddress = false },
            onSave = {
                onSave(it)
                showNewAddress = false
            },
        )
    }

    editingAddress?.let { address ->
        AddressEditorDialog(
            session = session,
            recipientNameDefault = recipientNameDefault,
            address = address,
            onDismiss = { editingAddress = null },
            onSave = {
                onSave(it)
                editingAddress = null
            },
        )
    }
}

@Composable
private fun AddressCard(address: AddressRecord, onEdit: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BosqueSurfaceSoft)
            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(StoreSoftMint),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.Pin, tint = StoreLink, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${address.label} principal", color = BosqueInk, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(text = address.recipientName.ifBlank { "Cliente Zeta" }, color = BosqueMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            StatusBadge("Delivery", active = true)
        }
        Text(text = address.line1, color = BosqueInk, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(text = listOf(address.district, address.city).filter { it.isNotBlank() }.joinToString(", "), color = BosqueMuted, fontSize = 12.sp)
        if (address.reference.isNotBlank()) {
            Text(text = "Ref: ${address.reference}", color = BosqueMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CartActionChip(text = "Editar", onClick = onEdit)
            CartActionChip(text = "Eliminar", onClick = onDelete)
        }
    }
}

@Composable
private fun CompactAddressCard(address: AddressRecord, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(188.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(BosqueSurfaceSoft)
            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(text = address.label, color = BosqueInk, fontWeight = FontWeight.Black, maxLines = 1)
        Text(text = address.line1, color = BosqueMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(text = address.district, color = StoreLink, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AddressEditorDialog(
    session: SupabaseSession,
    recipientNameDefault: String,
    address: AddressRecord?,
    onDismiss: () -> Unit,
    onSave: (AddressRecord) -> Unit,
) {
    val initialRegion = remember(address?.id) { inferPeruRegion(address) }
    val initialProvince = remember(address?.id, initialRegion) { inferPeruProvince(address, initialRegion) }
    val labelOptions = listOf("Casa", "Trabajo", "Familia", "Otro")
    var label by remember(address?.id) { mutableStateOf(address?.label ?: "Casa") }
    var recipientName by remember(address?.id, recipientNameDefault) {
        mutableStateOf(address?.recipientName?.takeIf { it.isNotBlank() } ?: recipientNameDefault)
    }
    var line1 by remember(address?.id) { mutableStateOf(address?.line1 ?: "") }
    var line2 by remember(address?.id) { mutableStateOf(address?.line2 ?: "") }
    var selectedRegion by remember(address?.id) { mutableStateOf(initialRegion.name) }
    var selectedProvince by remember(address?.id) { mutableStateOf(initialProvince.name) }
    var district by remember(address?.id) { mutableStateOf(address?.district ?: initialProvince.districts.first()) }
    var reference by remember(address?.id) { mutableStateOf(address?.reference ?: "") }
    var isDefault by remember(address?.id) { mutableStateOf(address?.isDefault ?: true) }
    val region = peruRegions.firstOrNull { it.name == selectedRegion } ?: peruRegions.first()
    val province = region.provinces.firstOrNull { it.name == selectedProvince } ?: region.provinces.first()
    val canSave = line1.isNotBlank() && recipientName.isNotBlank() && district.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .border(1.dp, BosqueBorder.copy(alpha = 0.8f), RoundedCornerShape(26.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Brush.linearGradient(listOf(StoreSoftMint, BosqueSurfaceStrong)))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        AmazonLineIcon(AmazonIcon.Pin, tint = StoreLink, modifier = Modifier.size(26.dp))
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = if (address == null) "Nueva direccion" else "Editar direccion",
                            color = BosqueInk,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = "Datos de entrega en Peru.",
                            color = BosqueMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            item {
                AddressChoiceRow(
                    title = "Etiqueta",
                    options = labelOptions,
                    selected = label,
                    onSelected = { label = it },
                )
            }
            item { AdminTextField("Nombre de quien recibe", recipientName, onValueChange = { recipientName = it }) }
            item {
                PeruLocationSelector(
                    selectedRegion = selectedRegion,
                    selectedProvince = selectedProvince,
                    selectedDistrict = district,
                    onRegionSelected = { value ->
                        selectedRegion = value
                        val nextRegion = peruRegions.first { it.name == value }
                        selectedProvince = nextRegion.provinces.first().name
                        district = nextRegion.provinces.first().districts.first()
                    },
                    onProvinceSelected = { value ->
                        selectedProvince = value
                        val nextProvince = region.provinces.first { it.name == value }
                        district = nextProvince.districts.first()
                    },
                    onDistrictSelected = { district = it },
                )
            }
            item { AdminTextField("Calle, avenida, jiron o mz/lote", line1, onValueChange = { line1 = it }) }
            item { AdminTextField("Dpto, piso, interior o urbanizacion", line2, onValueChange = { line2 = it }) }
            item { AdminTextField("Referencia para el repartidor", reference, onValueChange = { reference = it }) }
            item {
                AddressSummaryCard(
                    label = label,
                    recipientName = recipientName,
                    line1 = line1,
                    line2 = line2,
                    district = district,
                    province = selectedProvince,
                    region = selectedRegion,
                )
            }
            item {
                Button(
                    onClick = { isDefault = !isDefault },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDefault) StoreActionYellow else Color.White,
                        contentColor = BosqueInk,
                    ),
                ) {
                    Text(text = if (isDefault) "Direccion principal" else "Marcar como principal", fontWeight = FontWeight.Black)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BosqueInk),
                    ) {
                        Text(text = "Cancelar", fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = {
                            onSave(
                                AddressRecord(
                                    id = address?.id.orEmpty(),
                                    userId = session.userId,
                                    label = label.ifBlank { "Casa" },
                                    recipientName = recipientName,
                                    phone = address?.phone.orEmpty(),
                                    line1 = line1,
                                    line2 = line2,
                                    district = district,
                                    city = "$selectedProvince, $selectedRegion",
                                    country = "PE",
                                    reference = reference,
                                    isDefault = isDefault,
                                )
                            )
                        },
                        enabled = canSave,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                    ) {
                        Text(text = "Guardar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

private fun inferPeruRegion(address: AddressRecord?): PeruRegionUi {
    if (address == null) return peruRegions.first()
    return peruRegions.firstOrNull { region ->
        address.city.contains(region.name, ignoreCase = true) ||
            region.provinces.any { province ->
                address.city.contains(province.name, ignoreCase = true) ||
                    province.districts.any { it.equals(address.district, ignoreCase = true) }
            }
    } ?: peruRegions.first()
}

private fun inferPeruProvince(address: AddressRecord?, region: PeruRegionUi): PeruProvinceUi {
    if (address == null) return region.provinces.first()
    return region.provinces.firstOrNull { province ->
        address.city.contains(province.name, ignoreCase = true) ||
            province.districts.any { it.equals(address.district, ignoreCase = true) }
    } ?: region.provinces.first()
}

@Composable
private fun PeruLocationSelector(
    selectedRegion: String,
    selectedProvince: String,
    selectedDistrict: String,
    onRegionSelected: (String) -> Unit,
    onProvinceSelected: (String) -> Unit,
    onDistrictSelected: (String) -> Unit,
) {
    val region = peruRegions.firstOrNull { it.name == selectedRegion } ?: peruRegions.first()
    val province = region.provinces.firstOrNull { it.name == selectedProvince } ?: region.provinces.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BosqueSurfaceSoft)
            .border(1.dp, BosqueBorder.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Ubicacion", color = BosqueInk, fontSize = 16.sp, fontWeight = FontWeight.Black)
        AddressDropdownField(
            title = "Region",
            options = peruRegions.map { it.name },
            selected = selectedRegion,
            onSelected = onRegionSelected,
        )
        AddressDropdownField(
            title = "Provincia",
            options = region.provinces.map { it.name },
            selected = selectedProvince,
            onSelected = onProvinceSelected,
        )
        AddressDropdownField(
            title = "Distrito",
            options = province.districts,
            selected = selectedDistrict,
            onSelected = onDistrictSelected,
        )
    }
}

@Composable
private fun AddressDropdownField(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val helperText = when (options.size) {
        1 -> "1 opcion"
        else -> "${options.size} opciones"
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, color = BosqueMuted, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Text(text = helperText, color = BosqueMuted.copy(alpha = 0.72f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, if (expanded) StoreLink else StoreLine, RoundedCornerShape(16.dp))
                    .clickable(role = Role.Button) { expanded = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = selected,
                    color = BosqueInk,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(text = "v", color = StoreLink, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .heightIn(max = 280.dp)
                    .background(Color.White)
                    .border(1.dp, BosqueBorder.copy(alpha = 0.7f), RoundedCornerShape(14.dp)),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (option == selected) StoreLink else BosqueInk,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                            )
                        },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressChoiceRow(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, color = BosqueMuted, fontSize = 11.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                val active = option == selected
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) BosqueGreenDeep else Color.White)
                        .border(1.dp, if (active) BosqueGreenDeep else StoreLine, RoundedCornerShape(999.dp))
                        .clickable(role = Role.Button) { onSelected(option) }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option,
                        color = if (active) Color.White else BosqueInk,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressSummaryCard(
    label: String,
    recipientName: String,
    line1: String,
    line2: String,
    district: String,
    province: String,
    region: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(StoreHeaderBottom)
            .border(1.dp, BosqueBorder.copy(alpha = 0.65f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PeruFlagMark()
            Text(text = label.ifBlank { "Direccion" }, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
        Text(
            text = recipientName.ifBlank { "Nombre pendiente" },
            color = BosqueMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = listOf(line1, line2).filter { it.isNotBlank() }.joinToString(", ").ifBlank { "Completa la direccion exacta" },
            color = BosqueInk,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(text = "$district, $province, $region", color = StoreLink, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun LogoutSection(email: String, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.75f), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BosqueSurfaceSoft)
                    .border(1.dp, BosqueBorder, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.User, tint = BosqueMuted, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Sesion activa", color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text(text = email, color = BosqueMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BosqueSurfaceStrong, contentColor = BosqueGreenDeep),
        ) {
            Text(text = "Cerrar sesion", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ProfileOptionGrid(isAdmin: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "Tu cuenta", detail = if (isAdmin) "admin activo" else "cliente")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileOptionTile("Direcciones", "Casa, oficina y retiro", AmazonIcon.Pin, Modifier.weight(1f))
            ProfileOptionTile("Pagos", "Metodo simulado", AmazonIcon.Cart, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ProfileOptionTile("Soporte", "Estado de pedidos", AmazonIcon.User, Modifier.weight(1f))
            ProfileOptionTile(if (isAdmin) "Admin" else "Preferencias", if (isAdmin) "Panel habilitado" else "Categorias favoritas", AmazonIcon.Menu, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ProfileOptionTile(title: String, detail: String, icon: AmazonIcon, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(20.dp))
            .padding(13.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(StoreSoftMint),
            contentAlignment = Alignment.Center,
        ) {
            AmazonLineIcon(icon = icon, tint = StoreLink, modifier = Modifier.size(19.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, color = BosqueInk, fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
            Text(text = detail, color = BosqueMuted, fontSize = 11.sp, lineHeight = 13.sp, maxLines = 2)
        }
    }
}

@Composable
private fun ProfileOrdersSection(orders: List<OrderRecord>, modifier: Modifier = Modifier) {
    var selectedOrder by remember { mutableStateOf<OrderRecord?>(null) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(title = "Tus pedidos", detail = "${orders.size} registrados")
        if (orders.isEmpty()) {
            OrdersEmptyState()
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge("${orders.count { it.status != "completed" }} en curso", active = true)
                StatusBadge("${orders.count { it.status == "completed" }} completados", active = false)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(orders.take(6), key = { it.id }) { order ->
                    val firstItem = order.items.firstOrNull()
                    Column(
                        modifier = Modifier
                            .width(246.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
                            .clickable(role = Role.Button) { selectedOrder = order }
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RemoteImage(
                                url = firstItem?.imageUrl.orEmpty(),
                                contentDescription = firstItem?.productName,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                background = BosqueSurfaceStrong,
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Pedido ${order.id.take(8).uppercase()}", color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                Text(text = "${order.items.sumOf { it.quantity }} items - ${formatSoles(order.total)}", color = BosqueWarmth, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        StatusBadge(formatOrderStatus(order.status), active = order.status != "cancelled")
                        Text(text = firstItem?.productName ?: "Compra Zeta Dorada", color = BosqueMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }

    selectedOrder?.let { order ->
        OrderDetailDialog(order = order, onDismiss = { selectedOrder = null })
    }
}

@Composable
private fun OrdersEmptyState() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(Color.White, Color(0xFFF6FAEE))))
            .border(1.dp, BosqueBorder.copy(alpha = 0.75f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(StoreSoftMint),
            contentAlignment = Alignment.Center,
        ) {
            AmazonLineIcon(AmazonIcon.Search, tint = StoreLink, modifier = Modifier.size(25.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(text = "Aun no tienes pedidos", color = BosqueInk, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(
                text = "Cuando completes una compra, veras aqui tu historial.",
                color = BosqueMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            Text(text = "Explora el catalogo y arma tu primer pedido demo.", color = StoreLink, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ProfileSignedInHero(email: String, role: String, isAdmin: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(StoreHeaderBottom, BosqueSurfaceStrong)))
            .border(1.dp, Color.White.copy(alpha = 0.75f), RoundedCornerShape(28.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.User, tint = StoreLink, modifier = Modifier.size(34.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Sesion activa", color = BosqueGreenDeep, fontSize = 13.sp, fontWeight = FontWeight.Black)
                Text(text = email, color = BosqueInk, fontSize = 18.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = if (isAdmin) "Administrador" else role, color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = if (isAdmin) "Tienes acceso para editar home y productos desde Admin." else "Tu cuenta esta lista para guardar compras simuladas y futuras preferencias.",
            color = BosqueInk,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun ProfileBenefits(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Por que entrar", color = BosqueInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BenefitTile("Pedidos demo", "Tu carrito se siente como una compra real.", Modifier.weight(1f))
            BenefitTile("Promos", "Recibe talleres y combos destacados.", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BenefitTile("Perfil", "Google o email, sin pasos raros.", Modifier.weight(1f))
            BenefitTile("Admin", "Si tu rol es admin, editas la tienda.", Modifier.weight(1f))
        }
    }
}

@Composable
private fun BenefitTile(title: String, detail: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(116.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(20.dp))
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(StoreSoftMint),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "✓", color = StoreLink, fontWeight = FontWeight.Black)
        }
        Text(text = title, color = BosqueInk, fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
        Text(text = detail, color = BosqueMuted, fontSize = 11.sp, lineHeight = 13.sp)
    }
}

@Composable
private fun AccountStatusCard(
    email: String,
    role: String,
    isAdmin: Boolean,
    onRefreshRole: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Estado de cuenta", color = BosqueInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
        AccountLine("Email", email)
        AccountLine("Rol", if (isAdmin) "admin" else role)
        Text(
            text = if (isAdmin) "El panel Admin aparece abajo para editar productos y banners." else "Para activar Admin, cambia tu perfil a role = admin en Supabase.",
            color = BosqueGreenDeep,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
        if (!isAdmin) {
            Button(
                onClick = onRefreshRole,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = "Refrescar rol", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun AccountLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BosqueSurfaceSoft)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = BosqueInk, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun CartScreen(
    products: List<Product>,
    orders: List<OrderRecord>,
    cart: Map<String, Int>,
    savedProductIds: Set<String>,
    subtotal: Int,
    cartCount: Int,
    session: SupabaseSession?,
    profile: ProfileRecord?,
    defaultAddress: AddressRecord?,
    deliveryMethod: String,
    paymentMethod: PaymentMethodOption,
    productsMessage: String?,
    query: String,
    selectedCategory: String,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onAdd: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    onSaveForLater: (Product) -> Unit,
    onRemoveSaved: (Product) -> Unit,
    onMoveSavedToCart: (Product) -> Unit,
    onCheckout: () -> Unit,
    onDeliveryMethodChange: (String) -> Unit,
    onPaymentMethodChange: (PaymentMethodOption) -> Unit,
    checkingOut: Boolean,
    onShop: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    val cartProducts = products.filter { (cart[it.id] ?: 0) > 0 }
    val savedProductList = products.filter { savedProductIds.contains(it.id) }
    val totalItems = cart.values.sum()
    var cartTab by remember { mutableStateOf("cart") }
    val cartOptions = remember {
        listOf(
            HeaderOption("cart", "Carrito", AmazonIcon.Cart),
            HeaderOption("lists", "Listas"),
            HeaderOption("buy_again", "Recomprar"),
            HeaderOption("keep_shopping", "Seguir comprando"),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            AmazonStyleHeader(
                cartCount = cartCount,
                email = session?.email,
                isAdmin = profile?.isAdmin == true,
                query = query,
                onQueryChange = onQueryChange,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                onCartClick = onCartClick,
                onAdminClick = onAdminClick,
                headerOptions = cartOptions,
                selectedHeaderKey = cartTab,
                onHeaderOptionSelected = { cartTab = it },
            )
        }
        productsMessage?.let { message ->
            item {
                Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                    StatusPill(message = message, onDismiss = onDismissMessage)
                }
            }
        }
        when (cartTab) {
            "cart" -> {
                item {
                    CartCheckoutSummary(
                        itemCount = totalItems,
                        subtotal = subtotal,
                        defaultAddress = defaultAddress,
                        deliveryMethod = deliveryMethod,
                        paymentMethod = paymentMethod,
                        hasItems = cartProducts.isNotEmpty(),
                        checkingOut = checkingOut,
                        onCheckout = onCheckout,
                        onShop = onShop,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (cartProducts.isEmpty()) {
                    item {
                        EmptyStateCard(
                            title = "Tu carrito esta vacio",
                            detail = "Agrega productos desde el home para ver resumen, entrega simulada y checkout como una orden real.",
                            action = "Seguir comprando",
                            onAction = onShop,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                } else {
                    item {
                        CartDeliveryMethodPicker(
                            selected = deliveryMethod,
                            onSelected = onDeliveryMethodChange,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    item {
                        CartPaymentMethodPicker(
                            selected = paymentMethod,
                            onSelected = onPaymentMethodChange,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    item {
                        CartDeliveryAddressCard(
                            address = defaultAddress,
                            deliveryMethod = deliveryMethod,
                            modifier = Modifier.padding(horizontal = 14.dp),
                        )
                    }
                    item {
                        Text(
                            text = "Seleccionar todos los items",
                            color = StoreLink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 18.dp),
                        )
                    }
                    items(cartProducts, key = { it.id }) { product ->
                        val qty = cart[product.id] ?: 0
                        AmazonCartItem(
                            product = product,
                            quantity = qty,
                            onAdd = { onAdd(product) },
                            onRemove = { onRemove(product) },
                            onSaveForLater = { onSaveForLater(product) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item {
                        CartCheckoutSummary(
                            itemCount = totalItems,
                            subtotal = subtotal,
                            defaultAddress = defaultAddress,
                            deliveryMethod = deliveryMethod,
                            paymentMethod = paymentMethod,
                            hasItems = true,
                            checkingOut = checkingOut,
                            onCheckout = onCheckout,
                            onShop = onShop,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            "lists" -> {
                item {
                    SavedForLaterSection(
                        products = savedProductList,
                        onMoveToCart = onMoveSavedToCart,
                        onRemove = onRemoveSaved,
                        onShop = onShop,
                    )
                }
            }
            "buy_again" -> {
                item {
                    BuyAgainFromOrdersSection(
                        orders = orders,
                        products = products,
                        onAdd = onAdd,
                        onShop = onShop,
                    )
                }
            }
            "keep_shopping" -> {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(text = "Sigue comprando", color = BosqueInk, fontSize = 25.sp, fontWeight = FontWeight.Black)
                        Text(text = "Vuelve al home para explorar ofertas, talleres y productos.", color = BosqueMuted, fontSize = 13.sp)
                        Button(
                            onClick = onShop,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                        ) {
                            Text(text = "Ir al home", fontWeight = FontWeight.Black)
                        }
                    }
                }
                item {
                    CartShelfSection(title = "Recomendados", detail = "Entrega simulada en Lima", products = products, onShop = onShop)
                }
            }
        }
    }
}

@Composable
private fun CartShelfSection(title: String, detail: String, products: List<Product>, onShop: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(title = title, detail = detail)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products, key = { it.id }) { product ->
                DealTile(product = product, onClick = onShop)
            }
        }
    }
}

@Composable
private fun BuyAgainFromOrdersSection(
    orders: List<OrderRecord>,
    products: List<Product>,
    onAdd: (Product) -> Unit,
    onShop: () -> Unit,
) {
    val purchasedIds = remember(orders) {
        orders.flatMap { order -> order.items.map { it.productId } }.distinct()
    }
    val purchasedProducts = products.filter { product -> purchasedIds.contains(product.id) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(title = "Comprar otra vez", detail = "${purchasedProducts.size} reales")
        if (purchasedProducts.isEmpty()) {
            Text(text = "Cuando completes un pedido, sus productos apareceran aqui.", color = BosqueMuted, fontSize = 13.sp)
            CartActionChip(text = "Explorar productos", onClick = onShop)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(purchasedProducts, key = { it.id }) { product ->
                    Column(
                        modifier = Modifier
                            .width(168.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BosqueSurfaceSoft)
                            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ProductImage(product = product, modifier = Modifier.fillMaxWidth().height(118.dp))
                        Text(text = product.name, color = BosqueInk, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        ProductPriceBlock(product = product, priceSize = 16, originalSize = 10)
                        CartActionChip(text = "Agregar", onClick = { onAdd(product) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedForLaterSection(
    products: List<Product>,
    onMoveToCart: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    onShop: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(title = "Guardado para despues", detail = "${products.size} productos")
        if (products.isEmpty()) {
            Text(text = "Todavia no guardaste productos.", color = BosqueMuted, fontSize = 13.sp)
            CartActionChip(text = "Explorar productos", onClick = onShop)
        } else {
            products.forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(BosqueSurfaceSoft)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProductImage(product = product, modifier = Modifier.size(74.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(text = product.name, color = BosqueInk, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        ProductPriceBlock(product = product, priceSize = 15, originalSize = 10)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CartActionChip(text = "Mover al carrito", onClick = { onMoveToCart(product) })
                            CartActionChip(text = "Quitar", onClick = { onRemove(product) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartTabs(modifier: Modifier = Modifier) {
    val tabs = listOf("Carrito", "Listas", "Recomprar", "Seguir comprando")

    LazyRow(
        modifier = modifier
            .background(BosqueSurface)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        items(tabs) { tab ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = tab,
                    color = BosqueInk,
                    fontSize = 16.sp,
                    fontWeight = if (tab == "Carrito") FontWeight.Black else FontWeight.Normal,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(if (tab == "Carrito") 48.dp else 0.dp)
                        .height(3.dp)
                        .background(BosqueInk)
                )
            }
        }
    }
}

@Composable
private fun CartDeliveryMethodPicker(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DeliveryChoice(
            title = "Delivery",
            detail = "Enviar a direccion",
            selected = selected == "delivery",
            modifier = Modifier.weight(1f),
            onClick = { onSelected("delivery") },
        )
        DeliveryChoice(
            title = "Retiro",
            detail = "Recojo simulado",
            selected = selected == "pickup",
            modifier = Modifier.weight(1f),
            onClick = { onSelected("pickup") },
        )
    }
}

@Composable
private fun DeliveryChoice(
    title: String,
    detail: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) StoreSoftMint else BosqueSurfaceSoft)
            .border(1.dp, if (selected) StoreLink else BosqueBorder, RoundedCornerShape(14.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = title, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black, maxLines = 1)
        Text(text = detail, color = if (selected) StoreLink else BosqueMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun CartPaymentMethodPicker(
    selected: PaymentMethodOption,
    onSelected: (PaymentMethodOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SectionTitle(title = "Pago simulado", detail = selected.label)
        PaymentMethodOption.entries.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { option ->
                    PaymentChoice(
                        option = option,
                        selected = selected == option,
                        onClick = { onSelected(option) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Text(
            text = "Modo demo: no se procesa cobro real ni se emite comprobante fiscal.",
            color = BosqueMuted,
            fontSize = 11.sp,
            lineHeight = 15.sp,
        )
    }
}

@Composable
private fun PaymentChoice(
    option: PaymentMethodOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) StoreSoftMint else BosqueSurfaceSoft)
            .border(1.dp, if (selected) StoreLink else BosqueBorder, RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (selected) StoreActionYellow else Color.White)
                .border(1.dp, if (selected) BosqueWarmth else StoreLine, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = option.badge, color = BosqueInk, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = option.label, color = BosqueInk, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1)
            Text(text = option.detail, color = if (selected) StoreLink else BosqueMuted, fontSize = 10.sp, lineHeight = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CartDeliveryAddressCard(address: AddressRecord?, deliveryMethod: String, modifier: Modifier = Modifier) {
    val isPickup = deliveryMethod == "pickup"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(StoreSoftMint),
            contentAlignment = Alignment.Center,
        ) {
            AmazonLineIcon(AmazonIcon.Pin, tint = StoreLink, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = when {
                    isPickup -> "Retiro simulado en Lima"
                    address == null -> "Falta direccion de entrega"
                    else -> "Enviar a ${address.recipientName.ifBlank { "cliente" }}"
                },
                color = BosqueInk,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = when {
                    isPickup -> "No se pedira direccion. Quedara como recojo para demo."
                    address == null -> "Completa Perfil > Cuenta > Direcciones para continuar con delivery."
                    else -> "${address.line1}, ${address.district}"
                },
                color = BosqueMuted,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        StatusBadge(if (isPickup) "Retiro" else "Delivery", active = true)
    }
}

@Composable
private fun CartCheckoutSummary(
    itemCount: Int,
    subtotal: Int,
    defaultAddress: AddressRecord? = null,
    deliveryMethod: String,
    paymentMethod: PaymentMethodOption,
    hasItems: Boolean,
    checkingOut: Boolean,
    onCheckout: () -> Unit,
    onShop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val delivery = 0
    val total = subtotal + delivery

    Column(
        modifier = modifier
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(title = "Resumen", detail = if (hasItems) "$itemCount items" else "sin items")
        CheckoutLine("Subtotal", if (hasItems) formatSoles(subtotal) else "S/ 0.00")
        CheckoutLine(
            "Entrega simulada",
            if (!hasItems) "-" else if (deliveryMethod == "pickup") "Retiro gratis" else if (defaultAddress == null) "Completar datos" else "Delivery gratis",
        )
        CheckoutLine("Metodo de pago", if (hasItems) paymentMethod.label else "-")
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BosqueBorder.copy(alpha = 0.7f)))
        CheckoutLine("Total", if (hasItems) formatSoles(total) else "S/ 0.00", strong = true)
        Button(
            onClick = { if (hasItems) onCheckout() else onShop() },
            enabled = !checkingOut,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
        ) {
            Text(
                text = if (checkingOut) "Procesando pago demo..." else "Pagar demo ($itemCount items)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(text = "Checkout simulado, sin pagos reales. El pedido queda visible en tu perfil.", color = BosqueMuted, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun CheckoutLine(label: String, value: String, strong: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = if (strong) BosqueInk else BosqueMuted, fontSize = if (strong) 17.sp else 13.sp, fontWeight = if (strong) FontWeight.Black else FontWeight.Bold)
        Text(text = value, color = if (strong) BosqueWarmth else BosqueInk, fontSize = if (strong) 20.sp else 14.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AmazonCartItem(
    product: Product,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onSaveForLater: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(BosqueSurfaceSoft)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White)
                    .border(1.dp, StoreLine, RoundedCornerShape(5.dp))
            )
            ProductImage(product = product, modifier = Modifier.size(126.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${product.name} - ${product.format}",
                color = BosqueInk,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = "by ZETA DORADA", color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = "100+ interesados este mes", color = BosqueInk, fontSize = 13.sp)
            ProductPriceBlock(product = product, priceSize = 26, originalSize = 12)
            Text(
                text = "Entrega gratis simulada en Lima",
                color = BosqueGreenDeep,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(text = "Guarda mas con combos y talleres >", color = StoreLink, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(3.dp, StoreActionYellow, RoundedCornerShape(999.dp))
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable(role = Role.Button, onClick = onRemove),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = if (quantity <= 1) "x" else "-", color = BosqueInk, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    }
                    Text(text = quantity.toString(), color = BosqueInk, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable(role = Role.Button, onClick = onAdd),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "+", color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CartActionChip(text = "Eliminar", onClick = onRemove)
                CartActionChip(text = "Guardar", onClick = onSaveForLater)
            }
            CartActionChip(text = "Compartir")
        }
    }
}

@Composable
private fun CartActionChip(text: String, onClick: () -> Unit = {}) {
    Text(
        text = text,
        color = BosqueInk,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White)
            .border(1.dp, StoreLine, RoundedCornerShape(999.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

@Composable
private fun AdminScreen(
    client: SupabaseClient,
    products: List<ProductRecord>,
    promos: List<HomePromoRecord>,
    orders: List<OrderRecord>,
    notifications: List<AdminNotificationRecord>,
    cartCount: Int,
    session: SupabaseSession?,
    profile: ProfileRecord?,
    query: String,
    selectedCategory: String,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSave: (ProductRecord) -> Unit,
    onSavePromo: (HomePromoRecord) -> Unit,
    onDeleteProduct: (ProductRecord) -> Unit,
    onDeletePromo: (HomePromoRecord) -> Unit,
    onUpdateOrderStatus: (OrderRecord, String) -> Unit,
    onRefreshOrders: () -> Unit,
    onMessage: (String?) -> Unit,
) {
    var adminTab by remember { mutableStateOf("products") }
    var productEditorSeed by remember { mutableStateOf<ProductRecord?>(null) }
    var promoEditorSeed by remember { mutableStateOf<HomePromoRecord?>(null) }
    val adminOptions = remember {
        listOf(
            HeaderOption("products", "Productos"),
            HeaderOption("promos", "Anuncios"),
            HeaderOption("orders", "Pedidos"),
            HeaderOption("home", "Home"),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            AmazonStyleHeader(
                cartCount = cartCount,
                email = session?.email,
                isAdmin = profile?.isAdmin == true,
                query = query,
                onQueryChange = onQueryChange,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                onCartClick = onCartClick,
                onAdminClick = onAdminClick,
                headerOptions = adminOptions,
                selectedHeaderKey = adminTab,
                onHeaderOptionSelected = { key ->
                    if (key == "home") {
                        onCategorySelected(selectedCategory)
                    } else {
                        adminTab = key
                    }
                },
            )
        }
        item {
            AdminHero(
                activeTab = adminTab,
                productCount = products.size,
                promoCount = promos.size,
                orderCount = orders.size,
                unreadCount = notifications.count { !it.isRead },
                onNewProduct = { productEditorSeed = emptyProductRecord(displayOrder = products.size + 1) },
                onNewPromo = { promoEditorSeed = emptyHomePromoRecord(displayOrder = promos.size + 1) },
                onRefreshOrders = onRefreshOrders,
            )
        }
        if (adminTab == "orders") {
            item {
                SectionTitle(
                    title = "Pedidos",
                    detail = "${notifications.count { !it.isRead }} nuevos",
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
            if (notifications.isNotEmpty()) {
                item {
                    AdminNotificationsPanel(
                        notifications = notifications.take(4),
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                }
            }
            if (orders.isEmpty()) {
                item {
                    Text(
                        text = "Todavia no hay pedidos registrados.",
                        color = BosqueMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    AdminOrderCard(
                        order = order,
                        onStatus = { status -> onUpdateOrderStatus(order, status) },
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                }
            }
        } else if (adminTab == "promos") {
            item {
                SectionTitle(
                    title = "Anuncios home",
                    detail = "${promos.size} banners",
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
            items(promos, key = { it.id }) { promo ->
                AdminPromoCard(
                    promo = promo,
                    onEdit = { promoEditorSeed = promo },
                    onDelete = { onDeletePromo(promo) },
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
        } else {
            item {
                SectionTitle(
                    title = "Productos",
                    detail = "${products.size} items",
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
            items(products, key = { it.id }) { product ->
                AdminProductCard(
                    product = product,
                    onEdit = { productEditorSeed = product },
                    onDelete = { onDeleteProduct(product) },
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }
        }
    }

    productEditorSeed?.let { seed ->
        AdminPanelDialog(
            client = client,
            session = session,
            products = products,
            initialProduct = seed,
            onDismiss = { productEditorSeed = null },
            onMessage = onMessage,
            onSave = onSave,
            onDelete = {
                onDeleteProduct(it)
                productEditorSeed = null
            },
        )
    }
    promoEditorSeed?.let { seed ->
        HomePromoAdminDialog(
            client = client,
            session = session,
            promos = promos,
            initialPromo = seed,
            onDismiss = { promoEditorSeed = null },
            onMessage = onMessage,
            onSave = onSavePromo,
            onDelete = {
                onDeletePromo(it)
                promoEditorSeed = null
            },
        )
    }
}

@Composable
private fun AdminHero(
    activeTab: String,
    productCount: Int,
    promoCount: Int,
    orderCount: Int,
    unreadCount: Int,
    onNewProduct: () -> Unit,
    onNewPromo: () -> Unit,
    onRefreshOrders: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF173B25),
                        Color(0xFF285C38),
                        Color(0xFFE3C044),
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.38f), RoundedCornerShape(28.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.Menu, tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Panel admin", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                Text(
                    text = when (activeTab) {
                        "orders" -> "Pedidos, pagos simulados y estados"
                        "promos" -> "Banners y anuncios del home"
                        else -> "Catalogo, stock e imagenes"
                    },
                    color = Color(0xFFF6EFC8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (unreadCount > 0) {
                Text(
                    text = "$unreadCount nuevos",
                    color = BosqueInk,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(StoreActionYellow)
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetric("Productos", productCount.toString(), AmazonIcon.Cart)
                AdminMetric("Pedidos", orderCount.toString(), AmazonIcon.User)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetric("Anuncios", promoCount.toString(), AmazonIcon.Home)
                AdminMetric("Nuevos", unreadCount.toString(), AmazonIcon.Menu)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onNewProduct,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BosqueGreenDeep),
            ) {
                Text(text = "Agregar producto", fontWeight = FontWeight.Black)
            }
            Button(
                onClick = onNewPromo,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = "Agregar anuncio", fontWeight = FontWeight.Black)
            }
        }
        if (activeTab == "orders") {
            Button(
                onClick = onRefreshOrders,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.92f), contentColor = BosqueGreenDeep),
            ) {
                Text(text = "Actualizar pedidos", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun AdminMetric(label: String, value: String, icon: AmazonIcon, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.17f))
            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            AmazonLineIcon(icon = icon, tint = StoreActionYellow, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(text = label, color = Color(0xFFF6EFC8), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun AdminOrderCard(order: OrderRecord, onStatus: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.82f), RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(StoreSoftMint),
                    contentAlignment = Alignment.Center,
                ) {
                    AmazonLineIcon(AmazonIcon.Cart, tint = StoreLink, modifier = Modifier.size(25.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Pedido ${order.id.take(8)}", color = BosqueInk, fontWeight = FontWeight.Black, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "${order.items.sumOf { it.quantity }} items - ${formatSoles(order.total)}", color = BosqueWarmth, fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
            StatusBadge(formatOrderStatus(order.status), order.status != "cancelled")
        }
        Text(
            text = order.customerEmail.ifBlank { "cliente" },
            color = BosqueMuted,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        order.items.take(3).forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BosqueSurfaceSoft)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RemoteImage(
                    url = item.imageUrl,
                    contentDescription = item.productName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(13.dp)),
                    background = BosqueSurfaceStrong,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.productName, color = BosqueInk, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = "${item.quantity} x ${formatSoles(item.unitPrice)}", color = BosqueMuted, fontSize = 12.sp)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminSmallButton("Preparando", { onStatus("preparing") }, Modifier.weight(1f))
            AdminSmallButton("Listo", { onStatus("ready") }, Modifier.weight(1f))
            AdminSmallButton("Completado", { onStatus("completed") }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AdminNotificationsPanel(notifications: List<AdminNotificationRecord>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(StoreSoftMint)
            .border(1.dp, BosqueBorder, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(StoreActionYellow),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.Menu, tint = BosqueInk, modifier = Modifier.size(17.dp))
            }
            Text(text = "Avisos recientes", color = BosqueInk, fontWeight = FontWeight.Black, fontSize = 17.sp)
        }
        notifications.forEach { notification ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.72f))
                    .padding(9.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (notification.isRead) BosqueMuted else StoreActionYellow)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = notification.title, color = BosqueInk, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Text(text = notification.body, color = BosqueMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun AdminProductCard(product: ProductRecord, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.82f), RoundedCornerShape(22.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RemoteImage(
                url = product.imageUrls.firstOrNull().orEmpty(),
                contentDescription = product.name,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(20.dp)),
                background = product.accentHex.toComposeColor(BosqueSurfaceStrong).copy(alpha = 0.75f),
                placeholderText = product.name.take(2).uppercase(),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminMiniChip(product.category, StoreSoftMint, StoreLink)
                    AdminMiniChip("${product.imageUrls.size} img", BosqueSurfaceSoft, BosqueMuted)
                }
                Text(text = product.name, color = BosqueInk, fontWeight = FontWeight.Black, fontSize = 19.sp, lineHeight = 21.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = "${formatSoles(product.offerPrice ?: product.price)} - stock ${product.stock}", color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                if (product.offerPrice != null && product.offerPrice < product.price) {
                    Text(text = "Oferta: antes ${formatSoles(product.price)}", color = BosqueWarmth, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
            StatusBadge(if (product.isActive) "Activo" else "Oculto", product.isActive)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminSmallButton("Modificar", onEdit, Modifier.weight(1f))
            AdminSmallButton("Eliminar", onDelete, Modifier.weight(1f), danger = true)
        }
    }
}

@Composable
private fun AdminPromoCard(promo: HomePromoRecord, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.82f), RoundedCornerShape(22.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RemoteImage(
                url = promo.imageUrl,
                contentDescription = promo.title,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(20.dp)),
                background = promo.colorHexes.firstOrNull()?.toComposeColor(StoreSoftMint) ?: StoreSoftMint,
                placeholderText = "AD",
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    AdminMiniChip("Orden ${promo.displayOrder}", StoreSoftMint, StoreLink)
                    AdminMiniChip(if (promo.imageUrl.isBlank()) "sin imagen" else "imagen", BosqueSurfaceSoft, BosqueMuted)
                }
                Text(text = promo.eyebrow, color = BosqueMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = promo.title, color = BosqueInk, fontWeight = FontWeight.Black, fontSize = 19.sp, lineHeight = 21.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            StatusBadge(if (promo.isActive) "Activo" else "Oculto", promo.isActive)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdminSmallButton("Modificar", onEdit, Modifier.weight(1f))
            AdminSmallButton("Eliminar", onDelete, Modifier.weight(1f), danger = true)
        }
    }
}

@Composable
private fun AdminMiniChip(text: String, background: Color, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    )
}

@Composable
private fun StatusBadge(text: String, active: Boolean) {
    Text(
        text = text,
        color = if (active) StoreLink else BosqueWarmth,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) StoreSoftMint else Color(0xFFFFEEE7))
            .border(1.dp, if (active) Color(0xFFD3E9D5) else Color(0xFFF4CDBE), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun AdminSmallButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, danger: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (danger) Color(0xFFFFEEE7) else Color(0xFFEFF6DF),
            contentColor = if (danger) BosqueWarmth else BosqueGreenDeep,
        ),
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        Text(text = text, fontWeight = FontWeight.Black, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun AdminLockedScreen(onProfileClick: () -> Unit, onRefreshProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                start = 14.dp,
                end = 14.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(text = "Admin", color = BosqueInk, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Necesitas iniciar sesion con una cuenta admin para entrar.", color = BosqueMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onRefreshProfile,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = "Refrescar rol", fontWeight = FontWeight.Black)
            }
            Button(
                onClick = onProfileClick,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BosqueGreenDeep),
            ) {
                Text(text = "Ir a perfil", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    selected: AppTab,
    count: Int,
    subtotal: Int,
    isAdmin: Boolean,
    onSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp)
            .background(Color.White)
            .border(1.dp, StoreLine)
            .padding(top = 5.dp, bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 5.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        NavItem("Inicio", AmazonIcon.Home, selected == AppTab.Home, Modifier.weight(1f)) { onSelected(AppTab.Home) }
        NavItem("Perfil", AmazonIcon.User, selected == AppTab.Profile, Modifier.weight(1f)) { onSelected(AppTab.Profile) }
        NavItem("Carrito", AmazonIcon.Cart, selected == AppTab.Cart, Modifier.weight(1f), badge = count) { onSelected(AppTab.Cart) }
        if (isAdmin) {
            NavItem("Admin", AmazonIcon.Menu, selected == AppTab.Admin, Modifier.weight(1f)) { onSelected(AppTab.Admin) }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: AmazonIcon,
    active: Boolean,
    modifier: Modifier = Modifier,
    badge: Int = 0,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(54.dp)
            .clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (active) StoreSoftMint else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(
                    icon = icon,
                    tint = if (active) StoreLink else BosqueInk,
                    modifier = Modifier.size(28.dp),
                )
            }
            if (badge > 0) {
                CartBadge(count = badge, modifier = Modifier.align(Alignment.TopEnd).padding(top = 1.dp, end = 1.dp))
            }
        }
    }
}

@Composable
private fun CartBadge(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(19.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(StoreActionYellow)
            .border(1.dp, Color.White.copy(alpha = 0.82f), RoundedCornerShape(999.dp))
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = BosqueInk,
            fontSize = 10.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

private enum class AmazonIcon {
    Home,
    User,
    Cart,
    Menu,
    Search,
    Pin,
}

@Composable
private fun AccountHeader(signedIn: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(Brush.verticalGradient(listOf(StoreHeaderTop, StoreHeaderBottom)))
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "Cuenta", color = BosqueInk, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(
                text = if (signedIn) "Tu espacio Zeta Dorada" else "Pedidos, direcciones y preferencias",
                color = BosqueMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun AccountTabRow(
    options: List<HeaderOption>,
    selectedKey: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val selected = option.key == selectedKey
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) BosqueGreenDeep else Color.White.copy(alpha = 0.9f))
                    .border(1.dp, if (selected) BosqueGreenDeep else BosqueBorder, RoundedCornerShape(14.dp))
                    .clickable(role = Role.Button) { onSelected(option.key) }
                    .padding(horizontal = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                option.leading?.let { icon ->
                    AmazonLineIcon(icon = icon, tint = if (selected) Color.White else StoreLink, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = option.text,
                    color = if (selected) Color.White else BosqueInk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun AmazonStyleHeader(
    cartCount: Int,
    email: String?,
    isAdmin: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    selectedSortKey: String = DEFAULT_PRODUCT_SORT,
    onSortSelected: (String) -> Unit = {},
    showFilters: Boolean = true,
    headerOptions: List<HeaderOption>? = null,
    selectedHeaderKey: String = selectedCategory,
    onHeaderOptionSelected: (String) -> Unit = {},
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val selectedSort = productSortOptions.firstOrNull { it.key == selectedSortKey } ?: productSortOptions.first()
    val options = headerOptions ?: buildList {
        add(HeaderOption("sort", if (selectedSort.key == DEFAULT_PRODUCT_SORT) "Ordenar" else selectedSort.label, AmazonIcon.Menu))
        categories.forEach { category -> add(HeaderOption(category, category)) }
        add(HeaderOption("account", if (email == null) "Ingresar" else "Cuenta", AmazonIcon.User))
        if (isAdmin) add(HeaderOption("admin", "Admin"))
    }
    val filterHeight by animateDpAsState(
        targetValue = if (showFilters) 42.dp else 0.dp,
        animationSpec = tween(durationMillis = 240),
        label = "header_filter_height",
    )
    val filterSpacing by animateDpAsState(
        targetValue = if (showFilters) 12.dp else 0.dp,
        animationSpec = tween(durationMillis = 240),
        label = "header_filter_spacing",
    )
    val filterOffset by animateDpAsState(
        targetValue = if (showFilters) 0.dp else (-18).dp,
        animationSpec = tween(durationMillis = 240),
        label = "header_filter_offset",
    )
    val filterAlpha by animateFloatAsState(
        targetValue = if (showFilters) 1f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "header_filter_alpha",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.verticalGradient(listOf(StoreHeaderTop, StoreHeaderBottom)))
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SearchPill(query = query, onQueryChange = onQueryChange, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(filterSpacing))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(filterHeight)
                .clipToBounds()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = filterOffset)
                    .alpha(filterAlpha)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                options.forEach { option ->
                    if (headerOptions == null && option.key == "sort") {
                        Box {
                            HeaderChip(
                                text = option.text,
                                leading = option.leading,
                                selected = selectedSortKey != DEFAULT_PRODUCT_SORT,
                                onClick = { sortMenuExpanded = true },
                            )
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                                offset = DpOffset(x = (-12).dp, y = 4.dp),
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                                shadowElevation = 0.dp,
                                modifier = Modifier
                                    .width(220.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Brush.verticalGradient(listOf(StoreHeaderTop, Color(0xFFF9FFF5), StoreHeaderBottom)))
                                    .border(1.dp, Color.White.copy(alpha = 0.78f), RoundedCornerShape(22.dp))
                                    .padding(8.dp),
                            ) {
                                Text(
                                    text = "Ordenar productos",
                                    color = BosqueMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                                productSortOptions.forEach { sortOption ->
                                    val selected = sortOption.key == selectedSortKey
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(if (selected) Color.White else Color.White.copy(alpha = 0.48f))
                                            .border(
                                                1.dp,
                                                if (selected) StoreLink.copy(alpha = 0.45f) else Color.Transparent,
                                                RoundedCornerShape(15.dp),
                                            )
                                            .clickable(role = Role.Button) {
                                                sortMenuExpanded = false
                                                onSortSelected(sortOption.key)
                                            }
                                            .padding(horizontal = 11.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(if (selected) StoreLink else Color.White.copy(alpha = 0.72f))
                                                .border(1.dp, if (selected) StoreLink else BosqueBorder, CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (selected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(StoreActionYellow)
                                                )
                                            }
                                        }
                                        Text(
                                            text = sortOption.label,
                                            color = if (selected) BosqueGreenDeep else BosqueInk,
                                            fontSize = 14.sp,
                                            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    } else {
                        HeaderChip(
                            text = option.text,
                            leading = option.leading,
                            selected = option.key == selectedHeaderKey,
                            onClick = {
                                if (headerOptions != null) {
                                    onHeaderOptionSelected(option.key)
                                } else {
                                    when (option.key) {
                                        "account" -> Unit
                                        "admin" -> onAdminClick()
                                        else -> onCategorySelected(option.key)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchPill(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White)
            .border(1.dp, StoreLine, RoundedCornerShape(999.dp))
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AmazonLineIcon(AmazonIcon.Search, tint = BosqueInk, modifier = Modifier.size(27.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(color = BosqueInk, fontSize = 17.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (query.isBlank()) {
                    Text(
                        text = "Buscar o hacer una pregunta",
                        color = Color(0xFF5F6368),
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun HeaderChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: AmazonIcon? = null,
) {
    Row(
        modifier = Modifier
            .height(42.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Color.White else Color.White.copy(alpha = 0.62f))
            .border(1.dp, if (selected) StoreLink else Color.Transparent, RoundedCornerShape(999.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (leading != null) {
            AmazonLineIcon(icon = leading, tint = BosqueInk, modifier = Modifier.size(20.dp))
        }
        Text(text = text, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AmazonLineIcon(icon: AmazonIcon, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.minDimension * 0.1f, cap = StrokeCap.Round)
        when (icon) {
            AmazonIcon.Search -> {
                drawCircle(tint, radius = size.minDimension * 0.28f, center = Offset(size.width * 0.43f, size.height * 0.42f), style = stroke)
                drawLine(tint, Offset(size.width * 0.64f, size.height * 0.64f), Offset(size.width * 0.86f, size.height * 0.86f), strokeWidth = size.minDimension * 0.1f, cap = StrokeCap.Round)
            }
            AmazonIcon.Home -> {
                val roof = Path().apply {
                    moveTo(size.width * 0.12f, size.height * 0.48f)
                    lineTo(size.width * 0.5f, size.height * 0.16f)
                    lineTo(size.width * 0.88f, size.height * 0.48f)
                }
                drawPath(roof, tint, style = stroke)
                drawRoundRect(tint, Offset(size.width * 0.24f, size.height * 0.47f), Size(size.width * 0.52f, size.height * 0.38f), CornerRadius(size.width * 0.07f), style = stroke)
            }
            AmazonIcon.User -> {
                drawCircle(tint, radius = size.minDimension * 0.17f, center = Offset(size.width * 0.5f, size.height * 0.3f), style = stroke)
                drawArc(tint, 205f, 130f, false, Offset(size.width * 0.18f, size.height * 0.45f), Size(size.width * 0.64f, size.height * 0.48f), style = stroke)
            }
            AmazonIcon.Cart -> {
                drawLine(tint, Offset(size.width * 0.18f, size.height * 0.28f), Offset(size.width * 0.28f, size.height * 0.28f), strokeWidth = size.minDimension * 0.1f, cap = StrokeCap.Round)
                drawPath(Path().apply {
                    moveTo(size.width * 0.28f, size.height * 0.28f)
                    lineTo(size.width * 0.38f, size.height * 0.66f)
                    lineTo(size.width * 0.76f, size.height * 0.66f)
                    lineTo(size.width * 0.84f, size.height * 0.38f)
                    lineTo(size.width * 0.34f, size.height * 0.38f)
                }, tint, style = stroke)
                drawCircle(tint, radius = size.minDimension * 0.055f, center = Offset(size.width * 0.42f, size.height * 0.8f))
                drawCircle(tint, radius = size.minDimension * 0.055f, center = Offset(size.width * 0.72f, size.height * 0.8f))
            }
            AmazonIcon.Menu -> {
                drawLine(tint, Offset(size.width * 0.18f, size.height * 0.28f), Offset(size.width * 0.82f, size.height * 0.28f), strokeWidth = size.minDimension * 0.11f, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.18f, size.height * 0.5f), Offset(size.width * 0.82f, size.height * 0.5f), strokeWidth = size.minDimension * 0.11f, cap = StrokeCap.Round)
                drawLine(tint, Offset(size.width * 0.18f, size.height * 0.72f), Offset(size.width * 0.82f, size.height * 0.72f), strokeWidth = size.minDimension * 0.11f, cap = StrokeCap.Round)
            }
            AmazonIcon.Pin -> {
                drawCircle(tint, radius = size.minDimension * 0.24f, center = Offset(size.width * 0.5f, size.height * 0.38f), style = stroke)
                drawCircle(tint, radius = size.minDimension * 0.07f, center = Offset(size.width * 0.5f, size.height * 0.38f))
                drawLine(tint, Offset(size.width * 0.5f, size.height * 0.64f), Offset(size.width * 0.5f, size.height * 0.88f), strokeWidth = size.minDimension * 0.1f, cap = StrokeCap.Round)
            }
        }
    }
}

@Composable
private fun MarketplaceTopBar(
    cartCount: Int,
    email: String?,
    isAdmin: Boolean,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = "Zeta Dorada", color = BosqueInk, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = email ?: "Tienda demo conectada a Supabase",
                color = BosqueMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (isAdmin) {
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BosqueWarmth)
                        .clickable(role = Role.Button, onClick = onAdminClick)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Admin", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
            Box(
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onCartClick),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(BosqueGreenDeep),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Bolsa", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                if (cartCount > 0) {
                    CartBadge(count = cartCount, modifier = Modifier.align(Alignment.TopEnd).padding(top = 1.dp))
                }
            }
        }
    }
}

@Composable
private fun SearchBox(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Buscar", color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (query.isBlank()) {
                    Text(text = "reishi, foco, capsulas, kit...", color = BosqueMuted.copy(alpha = 0.65f), fontSize = 15.sp)
                }
                innerTextField()
            },
        )
    }
}

private data class Promo(
    val eyebrow: String,
    val title: String,
    val detail: String,
    val colors: List<Color>,
    val imageUrl: String,
)

@Composable
private fun PromoCarousel(promos: List<Promo>) {
    var index by remember { mutableStateOf(0) }
    if (promos.isEmpty()) {
        EmptyPromoCarousel()
        return
    }
    val promo = promos[index.coerceAtMost(promos.lastIndex)]

    LaunchedEffect(index, promos.size) {
        delay(3600)
        index = (index + 1) % promos.size
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(promo.colors))
            .clickable(role = Role.Button) { index = (index + 1) % promos.size }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = promo.eyebrow, color = Color(0xFFFFF8E8), fontSize = 12.sp, fontWeight = FontWeight.Black)
            Text(
                text = promo.title,
                color = Color.White,
                fontSize = 27.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = promo.detail, color = Color(0xFFFFF8E8), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(top = 8.dp)) {
                promos.indices.forEach { dot ->
                    Box(
                        modifier = Modifier
                            .size(width = if (dot == index) 18.dp else 7.dp, height = 7.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = if (dot == index) 0.95f else 0.42f))
                    )
                }
            }
        }
        RemoteImage(
            url = promo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            background = Color.White.copy(alpha = 0.16f),
            placeholderText = "IMG",
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.36f), RoundedCornerShape(20.dp))
        )
    }
}

@Composable
private fun EmptyPromoCarousel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(BosqueGreenDeep, StoreLink)))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Sin anuncios activos", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(text = "Crea anuncios desde Admin para llenar este carrusel.", color = Color(0xFFFFF8E8), fontSize = 13.sp)
    }
}

@Composable
private fun AuthStrip(
    client: SupabaseClient,
    session: SupabaseSession?,
    onSessionChanged: (SupabaseSession?) -> Unit,
    onMessage: (String?) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder.copy(alpha = 0.78f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (session == null) {
            Text(text = "Entrar a mi cuenta", color = BosqueInk, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(
                text = "Continua con Google para guardar tus pedidos y preferencias.",
                color = BosqueMuted,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
            Button(
                onClick = {
                    val url = client.googleSignInUrl("com.zeta.store://login-callback")
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                },
                enabled = client.isConfigured,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BosqueGreenDeep, contentColor = Color.White),
            ) {
                Text(text = "G", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Continuar con Google", fontWeight = FontWeight.Black)
            }
            Text(
                text = "Demo conectada a Supabase. Google es el unico inicio de sesion en esta pantalla.",
                color = BosqueMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(StoreSoftMint),
                    contentAlignment = Alignment.Center,
                ) {
                    AmazonLineIcon(AmazonIcon.User, tint = StoreLink, modifier = Modifier.size(30.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Hola", color = BosqueMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = session.email, color = BosqueInk, fontSize = 17.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Button(
                    onClick = { onSessionChanged(null) },
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BosqueWarmth),
                ) {
                    Text(text = "Salir", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun AuthButton(text: String, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BosqueGreenDeep),
    ) {
        Text(text = text, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun StatusPill(message: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(BosqueSurfaceStrong)
            .border(1.dp, BosqueBorder, RoundedCornerShape(999.dp))
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            color = BosqueMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "x", color = BosqueMuted, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun CenterStatusToast(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 32.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(BosqueGreenDeep),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "OK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = message, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = "Cambios sincronizados", color = BosqueMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CenterStatusToastDialog(message: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CenterStatusToast(message = message)
        }
    }
}

@Composable
private fun CategoryChips(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            val active = category == selected
            Text(
                text = category,
                color = if (active) Color.White else BosqueInk,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) BosqueGreenDeep else Color.White)
                    .border(1.dp, if (active) BosqueGreenDeep else BosqueBorder, RoundedCornerShape(999.dp))
                    .clickable(role = Role.Button) { onSelected(category) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun ToggleChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (selected) Color.White else BosqueInk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) BosqueGreenDeep else BosqueSurfaceSoft)
            .border(1.dp, if (selected) BosqueGreenDeep else StoreLine, RoundedCornerShape(999.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProductListSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .border(1.dp, BosqueBorder.copy(alpha = 0.65f), RoundedCornerShape(22.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonBlock(modifier = Modifier.width(120.dp).height(146.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SkeletonBlock(modifier = Modifier.fillMaxWidth(0.55f).height(18.dp))
                    SkeletonBlock(modifier = Modifier.fillMaxWidth().height(22.dp))
                    SkeletonBlock(modifier = Modifier.fillMaxWidth(0.76f).height(14.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonBlock(modifier = Modifier.fillMaxWidth(0.42f).height(28.dp))
                    SkeletonBlock(modifier = Modifier.fillMaxWidth(0.68f).height(36.dp))
                }
            }
        }
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(BosqueSurfaceStrong, BosqueSurfaceSoft, BosqueSurfaceStrong)))
    )
}

@Composable
private fun EmptyStateCard(
    title: String,
    detail: String,
    action: String?,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(24.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(StoreSoftMint),
            contentAlignment = Alignment.Center,
        ) {
            AmazonLineIcon(AmazonIcon.Search, tint = StoreLink, modifier = Modifier.size(27.dp))
        }
        Text(text = title, color = BosqueInk, fontSize = 21.sp, fontWeight = FontWeight.Black)
        Text(text = detail, color = BosqueMuted, fontSize = 13.sp, lineHeight = 18.sp)
        if (action != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = action, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, detail: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(text = title, color = BosqueInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text(text = detail, color = BosqueMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProductPriceBlock(
    product: Product,
    priceSize: Int,
    originalSize: Int,
    modifier: Modifier = Modifier,
) {
    if (product.hasValidOffer) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatSoles(product.price),
                    color = BosqueMuted,
                    fontSize = originalSize.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.LineThrough,
                    maxLines = 1,
                )
                Text(
                    text = "-${product.discountPercent}%",
                    color = BosqueWarmth,
                    fontSize = originalSize.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFFFF2D2))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            Text(
                text = formatSoles(product.currentPrice),
                color = BosqueGreenDeep,
                fontSize = priceSize.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }
    } else {
        Text(
            text = formatSoles(product.price),
            color = BosqueInk,
            fontSize = priceSize.sp,
            fontWeight = FontWeight.Black,
            modifier = modifier,
            maxLines = 1,
        )
    }
}

@Composable
private fun DealTile(product: Product, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(152.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(10.dp),
    ) {
        ProductImage(product = product, modifier = Modifier.fillMaxWidth().height(118.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.name, color = BosqueInk, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
        ProductPriceBlock(product = product, priceSize = 18, originalSize = 11)
    }
}

@Composable
private fun ProductRow(product: Product, quantity: Int, onClick: () -> Unit, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(22.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProductImage(product = product, modifier = Modifier.width(120.dp).height(146.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Badge(text = product.tag, color = product.accent, textColor = product.color)
                Badge(text = "Stock ${product.stock}", color = BosqueSurfaceStrong, textColor = BosqueGreenDeep)
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(text = product.name, color = BosqueInk, fontSize = 18.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(text = product.format, color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = "***** ${product.rating}  |  ${product.benefits.joinToString(" - ")}", color = Color(0xFF8C6A18), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ProductPriceBlock(product = product, priceSize = 25, originalSize = 12)
            Text(text = "Entrega gratis simulada - Retiro disponible", color = BosqueGreenDeep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(42.dp),
            ) {
                Text(text = if (quantity > 0) "Agregar otro ($quantity)" else "Agregar al carrito", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ProductImage(product: Product, modifier: Modifier = Modifier, imageUrl: String = product.imageUrls.firstOrNull().orEmpty()) {
    RemoteImage(
        url = imageUrl,
        contentDescription = product.name,
        contentScale = ContentScale.Crop,
        background = product.accent.copy(alpha = 0.6f),
        placeholderText = product.name.take(2).uppercase(),
        modifier = modifier.clip(RoundedCornerShape(18.dp)),
    )
}

@Composable
private fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    background: Color = BosqueSurfaceStrong,
    placeholderText: String = "IMG",
) {
    val cleanUrl = url.trim()
    var bitmap by remember(cleanUrl) { mutableStateOf(imageMemoryCache[cleanUrl]) }

    LaunchedEffect(cleanUrl) {
        bitmap = imageMemoryCache[cleanUrl]
        if (cleanUrl.isBlank() || bitmap != null) return@LaunchedEffect

        loadRemoteImageBitmap(cleanUrl)?.let { loaded ->
            imageMemoryCache[cleanUrl] = loaded
            bitmap = loaded
        }
    }

    Box(
        modifier = modifier.background(background),
        contentAlignment = Alignment.Center,
    ) {
        val loaded = bitmap
        if (loaded != null) {
            Image(
                bitmap = loaded,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(text = placeholderText, color = BosqueMuted, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun Badge(text: String, color: Color, textColor: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    )
}

@Composable
private fun DetailInfoStrip(product: Product) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailMiniMetric("Formato", product.format.ifBlank { product.category }, Modifier.weight(1f))
        DetailMiniMetric("Categoria", product.category, Modifier.weight(1f))
        DetailMiniMetric("Entrega", "Lima demo", Modifier.weight(1f))
    }
}

@Composable
private fun DetailMiniMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, BosqueBorder, RoundedCornerShape(16.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = label, color = BosqueMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(text = value, color = BosqueInk, fontSize = 12.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ProductDetailSection(title: String, items: List<String>, warning: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (warning) Color(0xFFFFF6E8) else Color.White)
            .border(1.dp, if (warning) Color(0xFFE6C88D) else BosqueBorder, RoundedCornerShape(18.dp))
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Text(text = title, color = BosqueInk, fontSize = 16.sp, fontWeight = FontWeight.Black)
        items.forEach { item ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Text(text = if (warning) "!" else "+", color = if (warning) BosqueWarmth else StoreLink, fontWeight = FontWeight.Black)
                Text(text = item, color = BosqueInk, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProductDetailDialog(
    product: Product,
    relatedProducts: List<Product>,
    quantity: Int,
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
    onRelatedSelected: (Product) -> Unit,
) {
    var selectedImage by remember(product.id) { mutableStateOf(product.imageUrls.firstOrNull().orEmpty()) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box {
                ProductImage(product = product, imageUrl = selectedImage, modifier = Modifier.fillMaxWidth().height(292.dp))
                Badge(
                    text = product.tag,
                    color = product.accent,
                    textColor = product.color,
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(product.imageUrls.ifEmpty { listOf("") }) { image ->
                    RemoteImage(
                        url = image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        background = product.accent.copy(alpha = 0.55f),
                        placeholderText = product.name.take(2).uppercase(),
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(2.dp, if (image == selectedImage) BosqueWarmth else BosqueBorder, RoundedCornerShape(14.dp))
                            .clickable { selectedImage = image },
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(text = product.name, color = BosqueInk, fontSize = 24.sp, lineHeight = 27.sp, fontWeight = FontWeight.Black)
                Text(text = product.latin.ifBlank { product.format }, color = BosqueMuted, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "***** ${product.rating}  |  Stock ${product.stock}", color = Color(0xFF8C6A18), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                ProductPriceBlock(product = product, priceSize = 32, originalSize = 14)
                Text(text = productDescription(product), color = BosqueInk, fontSize = 14.sp, lineHeight = 20.sp)
            }
            DetailInfoStrip(product = product)
            ProductDetailSection(title = "Beneficios destacados", items = productBenefitCopy(product))
            ProductDetailSection(title = "Modo de uso sugerido", items = productUsageCopy(product))
            ProductDetailSection(title = "Advertencias", items = productWarningCopy(product), warning = true)
            if (relatedProducts.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle(title = "Relacionados", detail = "${relatedProducts.size} opciones")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(relatedProducts, key = { it.id }) { related ->
                            DealTile(product = related, onClick = { onRelatedSelected(related) })
                        }
                    }
                }
            }
            Text(text = "Compra simulada para demo. No se procesa pago ni envio real.", color = BosqueMuted, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = BosqueSurfaceStrong, contentColor = BosqueGreenDeep)) {
                    Text(text = "Cerrar", fontWeight = FontWeight.Black)
                }
                Button(onClick = onAdd, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk)) {
                    Text(text = if (quantity > 0) "Agregar otro ($quantity)" else "Agregar", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun CheckoutConfirmationDialog(
    itemCount: Int,
    subtotal: Int,
    deliveryMethod: String,
    paymentMethod: PaymentMethodOption,
    checkingOut: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = { if (!checkingOut) onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(StoreSoftMint),
                contentAlignment = Alignment.Center,
            ) {
                AmazonLineIcon(AmazonIcon.Cart, tint = StoreLink, modifier = Modifier.size(30.dp))
            }
            Text(text = "Confirmar pago demo", color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(text = "Se simulara la aprobacion del pago, se reservara stock y el pedido aparecera en Perfil > Pedidos.", color = BosqueMuted, fontSize = 13.sp, lineHeight = 18.sp)
            CheckoutLine("Items", itemCount.toString())
            CheckoutLine("Entrega", if (deliveryMethod == "pickup") "Retiro simulado" else "Delivery gratis")
            CheckoutLine("Pago", paymentMethod.label)
            CheckoutLine("Total", formatSoles(subtotal), strong = true)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(1.dp, BosqueBorder, RoundedCornerShape(18.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(text = "Operacion demo", color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text(text = paymentMethod.detail, color = BosqueMuted, fontSize = 12.sp, lineHeight = 16.sp)
                Text(text = "No se cobrara dinero real.", color = BosqueWarmth, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDismiss,
                    enabled = !checkingOut,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BosqueSurfaceStrong, contentColor = BosqueGreenDeep),
                ) {
                    Text(text = "Cancelar", fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = onConfirm,
                    enabled = !checkingOut,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                ) {
                    Text(text = if (checkingOut) "Procesando" else "Pagar demo", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun CheckoutSuccessDialog(success: CheckoutSuccess, onDismiss: () -> Unit, onViewOrders: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(StoreActionYellow, BosqueSurfaceStrong))),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "OK", color = BosqueInk, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Text(text = "Pedido registrado", color = BosqueInk, fontSize = 25.sp, fontWeight = FontWeight.Black)
            Text(
                text = "Tu compra demo quedo lista para presentacion.",
                color = BosqueMuted,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            AccountLine("Codigo", success.orderCode)
            AccountLine("Items", success.itemCount.toString())
            AccountLine("Pago", success.paymentMethod)
            AccountLine("Total", formatSoles(success.total))
            Text(
                text = "Comprobante visual para demo. No reemplaza boleta, factura ni transaccion real.",
                color = BosqueMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Button(
                onClick = onViewOrders,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = "Ver mis pedidos", fontWeight = FontWeight.Black)
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BosqueSurfaceStrong, contentColor = BosqueGreenDeep),
            ) {
                Text(text = "Cerrar", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun OrderDetailDialog(order: OrderRecord, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "Pedido ${order.id.take(8)}", color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(text = formatOrderStatus(order.status), color = BosqueGreenDeep, fontSize = 14.sp, fontWeight = FontWeight.Black)
            AccountLine("Pago", if (order.paymentStatus == "simulated_paid") "Pago simulado aprobado" else order.paymentStatus)
            AccountLine("Items", order.items.sumOf { it.quantity }.toString())
            AccountLine("Total", formatSoles(order.total))
            Text(text = "Productos", color = BosqueInk, fontSize = 17.sp, fontWeight = FontWeight.Black)
            order.items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RemoteImage(
                        url = item.imageUrl,
                        contentDescription = item.productName,
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        background = BosqueSurfaceStrong,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.productName, color = BosqueInk, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(text = "${item.quantity} x ${formatSoles(item.unitPrice)}", color = BosqueMuted, fontSize = 12.sp)
                    }
                    Text(text = formatSoles(item.lineTotal), color = BosqueWarmth, fontWeight = FontWeight.Black)
                }
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
            ) {
                Text(text = "Cerrar", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun CartBottomBar(count: Int, subtotal: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(BosqueGreenDeep)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = "$count items en carrito", color = Color.White, fontWeight = FontWeight.Black)
            Text(text = "Subtotal ${formatSoles(subtotal)}", color = Color(0xFFE8F2D1), fontSize = 12.sp)
        }
        Text(text = "Ver carrito", color = Color.White, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun CartDialog(
    products: List<Product>,
    cart: Map<String, Int>,
    subtotal: Int,
    onDismiss: () -> Unit,
    onAdd: (Product) -> Unit,
    onRemove: (Product) -> Unit,
) {
    val cartProducts = products.filter { (cart[it.id] ?: 0) > 0 }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "Carrito simulado", color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
            if (cartProducts.isEmpty()) {
                Text(text = "Todavia no agregaste productos.", color = BosqueMuted)
            } else {
                cartProducts.forEach { product ->
                    val qty = cart[product.id] ?: 0
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProductImage(product = product, modifier = Modifier.size(62.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.name, color = BosqueInk, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "${formatSoles(product.currentPrice)} x $qty", color = BosqueMuted, fontSize = 12.sp)
                        }
                        QuantityButton(text = "-") { onRemove(product) }
                        Text(text = qty.toString(), fontWeight = FontWeight.Black)
                        QuantityButton(text = "+") { onAdd(product) }
                    }
                }
            }
            Text(text = "Subtotal ${formatSoles(subtotal)}", color = BosqueInk, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Text(text = "Checkout desactivado: esta version es solo para presentacion.", color = BosqueMuted, fontSize = 12.sp)
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = BosqueGreenDeep)) {
                Text(text = "Seguir viendo productos", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun AdminPanelDialog(
    client: SupabaseClient,
    session: SupabaseSession?,
    products: List<ProductRecord>,
    initialProduct: ProductRecord,
    onDismiss: () -> Unit,
    onMessage: (String?) -> Unit,
    onSave: (ProductRecord) -> Unit,
    onDelete: (ProductRecord) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editing by remember(initialProduct, products) { mutableStateOf(initialProduct) }
    var id by remember { mutableStateOf(editing.id) }
    var name by remember { mutableStateOf(editing.name) }
    var latin by remember { mutableStateOf(editing.latin) }
    var format by remember { mutableStateOf(editing.format) }
    var category by remember { mutableStateOf(editing.category) }
    var price by remember { mutableStateOf(editing.price.toString()) }
    var offerPrice by remember { mutableStateOf(editing.offerPrice?.toString().orEmpty()) }
    var stock by remember { mutableStateOf(editing.stock.toString()) }
    var displayOrder by remember { mutableStateOf(editing.displayOrder.toString()) }
    var tag by remember { mutableStateOf(editing.tag) }
    var featuredOffer by remember { mutableStateOf(editing.isFeaturedOffer) }
    var rating by remember { mutableStateOf(editing.rating) }
    var colorHex by remember { mutableStateOf(editing.colorHex) }
    var accentHex by remember { mutableStateOf(editing.accentHex) }
    var benefits by remember { mutableStateOf(editing.benefits.ifEmpty { listOf("Ritual") }) }
    var imageUrls by remember { mutableStateOf(editing.imageUrls.ifEmpty { listOf("") }) }
    var isActive by remember { mutableStateOf(editing.isActive) }
    var uploadingImageIndex by remember { mutableStateOf<Int?>(null) }

    fun uploadImage(uri: Uri, index: Int) {
        val activeSession = session
        if (activeSession == null) {
            onMessage("Inicia sesion como admin para subir imagenes.")
            return
        }
        scope.launch {
            uploadingImageIndex = index
            onMessage("Subiendo imagen...")
            runCatching {
                val upload = context.prepareStoreImageUpload(
                    uri = uri,
                    folder = "products/${slugFrom(id.ifBlank { name })}",
                )
                client.uploadStoreImage(
                    session = activeSession,
                    objectPath = upload.path,
                    contentType = upload.contentType,
                    bytes = upload.bytes,
                )
            }.onSuccess { uploadedUrl ->
                val next = imageUrls.ifEmpty { listOf("") }.toMutableList()
                while (next.size <= index) next.add("")
                next[index] = uploadedUrl
                imageUrls = next
                onMessage("Imagen subida.")
            }.onFailure { error ->
                onMessage(error.message ?: "No se pudo subir la imagen.")
            }
            uploadingImageIndex = null
        }
    }

    LaunchedEffect(editing.id, editing.displayOrder) {
        id = editing.id
        name = editing.name
        latin = editing.latin
        format = editing.format
        category = editing.category
        price = editing.price.toString()
        offerPrice = editing.offerPrice?.toString().orEmpty()
        stock = editing.stock.toString()
        displayOrder = editing.displayOrder.toString()
        tag = editing.tag
        featuredOffer = editing.isFeaturedOffer
        rating = editing.rating
        colorHex = editing.colorHex
        accentHex = editing.accentHex
        benefits = editing.benefits.ifEmpty { listOf("Ritual") }
        imageUrls = editing.imageUrls.ifEmpty { listOf("") }
        isActive = editing.isActive
    }

    Dialog(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(720.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Panel admin", color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text(text = "Solo visible para usuarios admin", color = BosqueMuted, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { editing = emptyProductRecord(displayOrder = products.size + 1) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                    ) {
                        Text(text = "Nuevo", fontWeight = FontWeight.Black)
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(products, key = { it.id }) { product ->
                        val active = product.id == editing.id
                        Text(
                            text = product.name.ifBlank { product.id },
                            color = if (active) Color.White else BosqueInk,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .width(132.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) BosqueGreenDeep else BosqueSurfaceStrong)
                                .clickable(role = Role.Button) { editing = product }
                                .padding(horizontal = 10.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            item { AdminFormSectionTitle("Informacion principal", "Lo que vera el cliente en la tienda.") }
            item { AdminTextField("Nombre del producto", name, onValueChange = { name = it }) }
            item { AdminTextField("Nombre botanico o descripcion corta", latin, onValueChange = { latin = it }) }
            item { AdminTextField("Presentacion", format, onValueChange = { format = it }) }
            item {
                AdminSingleChoiceSection(
                    title = "Categoria",
                    options = categories.filterNot { it == "Todos" },
                    selected = category,
                    onSelected = { category = it },
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField("Precio S/", price, modifier = Modifier.weight(1f), onValueChange = { price = it.filter(Char::isDigit) })
                    AdminTextField("Precio oferta S/", offerPrice, modifier = Modifier.weight(1f), onValueChange = { offerPrice = it.filter(Char::isDigit) })
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField("Stock", stock, modifier = Modifier.weight(1f), onValueChange = { stock = it.filter(Char::isDigit) })
                    AdminTextField("Posicion", displayOrder, modifier = Modifier.weight(1f), onValueChange = { displayOrder = it.filter(Char::isDigit) })
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField("Etiqueta visible", tag, modifier = Modifier.weight(1f), onValueChange = { tag = it })
                    AdminTextField("Calificacion", rating, modifier = Modifier.weight(1f), onValueChange = { rating = it })
                }
            }
            item {
                AdminFeaturedOfferToggle(
                    selected = featuredOffer,
                    onToggle = {
                        val next = !featuredOffer
                        featuredOffer = next
                    },
                )
            }
            item {
                AdminColorPicker(
                    title = "Color del producto",
                    selectedColorHex = colorHex,
                    selectedAccentHex = accentHex,
                    onSelected = { option ->
                        colorHex = option.colorHex
                        accentHex = option.accentHex
                    },
                )
            }
            item { AdminBenefitPicker(selected = benefits, onChange = { benefits = it }) }
            item {
                AdminImageUrlEditor(
                    title = "Imagenes del producto",
                    urls = imageUrls,
                    allowMultiple = true,
                    uploadingIndex = uploadingImageIndex,
                    onUpload = ::uploadImage,
                    onChange = { imageUrls = it },
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { isActive = !isActive },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isActive) BosqueGreenDeep else BosqueMuted, contentColor = Color.White),
                    ) {
                        Text(text = if (isActive) "Activo" else "Inactivo", fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = {
                            val basePrice = price.toIntOrNull() ?: 0
                            val discountPrice = offerPrice.trim().takeIf { it.isNotBlank() }?.toIntOrNull()
                            if (offerPrice.isNotBlank() && (discountPrice == null || discountPrice <= 0 || discountPrice >= basePrice)) {
                                onMessage("El precio oferta debe ser menor que el precio original.")
                                return@Button
                            }
                            onSave(
                                editing.copy(
                                    id = id.trim().takeUnless { it == "nuevo-producto" }.orEmpty().ifBlank { slugFrom(name) },
                                    name = name.trim(),
                                    latin = latin.trim(),
                                    format = format.trim(),
                                    category = category.trim().ifBlank { "Kits" },
                                    price = basePrice,
                                    offerPrice = discountPrice,
                                    isFeaturedOffer = featuredOffer,
                                    stock = stock.toIntOrNull() ?: 0,
                                    displayOrder = displayOrder.toIntOrNull() ?: editing.displayOrder,
                                    tag = tag.trim().ifBlank { "Nuevo" },
                                    rating = rating.trim().ifBlank { "4.8" },
                                    colorHex = colorHex.trim().ifBlank { "#426B35" },
                                    accentHex = accentHex.trim().ifBlank { "#EEF4D8" },
                                    benefits = benefits,
                                    imageKeys = emptyList(),
                                    imageUrls = cleanImageUrls(imageUrls),
                                    isActive = isActive,
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                    ) {
                        Text(text = "Guardar", fontWeight = FontWeight.Black)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onDelete(editing) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEE7), contentColor = BosqueWarmth),
                    ) {
                        Text(text = "Eliminar", fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BosqueSurfaceStrong, contentColor = BosqueGreenDeep),
                    ) {
                        Text(text = "Cerrar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePromoAdminDialog(
    client: SupabaseClient,
    session: SupabaseSession?,
    promos: List<HomePromoRecord>,
    initialPromo: HomePromoRecord,
    onDismiss: () -> Unit,
    onMessage: (String?) -> Unit,
    onSave: (HomePromoRecord) -> Unit,
    onDelete: (HomePromoRecord) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editing by remember(initialPromo, promos) { mutableStateOf(initialPromo) }
    var id by remember { mutableStateOf(editing.id) }
    var eyebrow by remember { mutableStateOf(editing.eyebrow) }
    var title by remember { mutableStateOf(editing.title) }
    var detail by remember { mutableStateOf(editing.detail) }
    var colorHexes by remember { mutableStateOf(editing.colorHexes.ifEmpty { adminPromoPalettes.first().colors }) }
    var imageKey by remember { mutableStateOf(editing.imageKey) }
    var imageUrl by remember { mutableStateOf(editing.imageUrl) }
    var displayOrder by remember { mutableStateOf(editing.displayOrder.toString()) }
    var isActive by remember { mutableStateOf(editing.isActive) }
    var uploadingImageIndex by remember { mutableStateOf<Int?>(null) }

    fun uploadImage(uri: Uri, index: Int) {
        val activeSession = session
        if (activeSession == null) {
            onMessage("Inicia sesion como admin para subir imagenes.")
            return
        }
        scope.launch {
            uploadingImageIndex = index
            onMessage("Subiendo imagen...")
            runCatching {
                val upload = context.prepareStoreImageUpload(
                    uri = uri,
                    folder = "promos/${slugFrom(id.ifBlank { title })}",
                )
                client.uploadStoreImage(
                    session = activeSession,
                    objectPath = upload.path,
                    contentType = upload.contentType,
                    bytes = upload.bytes,
                )
            }.onSuccess { uploadedUrl ->
                imageUrl = uploadedUrl
                onMessage("Imagen subida.")
            }.onFailure { error ->
                onMessage(error.message ?: "No se pudo subir la imagen.")
            }
            uploadingImageIndex = null
        }
    }

    LaunchedEffect(editing.id, editing.displayOrder) {
        id = editing.id
        eyebrow = editing.eyebrow
        title = editing.title
        detail = editing.detail
        colorHexes = editing.colorHexes.ifEmpty { adminPromoPalettes.first().colors }
        imageKey = editing.imageKey
        imageUrl = editing.imageUrl
        displayOrder = editing.displayOrder.toString()
        isActive = editing.isActive
    }

    Dialog(onDismissRequest = onDismiss) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(720.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(BosqueSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Editar home", color = BosqueInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text(text = "Anuncios del carousel principal", color = BosqueMuted, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { editing = emptyHomePromoRecord(displayOrder = promos.size + 1) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                    ) {
                        Text(text = "Nuevo", fontWeight = FontWeight.Black)
                    }
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(promos, key = { it.id }) { promo ->
                        val active = promo.id == editing.id
                        Text(
                            text = promo.eyebrow.ifBlank { promo.id },
                            color = if (active) Color.White else BosqueInk,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .width(132.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (active) BosqueGreenDeep else BosqueSurfaceStrong)
                                .clickable(role = Role.Button) { editing = promo }
                                .padding(horizontal = 10.dp, vertical = 9.dp)
                        )
                    }
                }
            }
            item { AdminFormSectionTitle("Contenido del anuncio", "Texto y estilo del carrusel del home.") }
            item { AdminTextField("Etiqueta pequena", eyebrow, onValueChange = { eyebrow = it }) }
            item { AdminTextField("Titulo", title, onValueChange = { title = it }) }
            item { AdminTextField("Detalle", detail, onValueChange = { detail = it }) }
            item {
                AdminPromoPalettePicker(
                    selectedColors = colorHexes,
                    onSelected = { colorHexes = it.colors },
                )
            }
            item {
                AdminImageUrlEditor(
                    title = "Imagen del anuncio",
                    urls = listOf(imageUrl),
                    allowMultiple = false,
                    uploadingIndex = uploadingImageIndex,
                    onUpload = ::uploadImage,
                    onChange = { imageUrl = it.firstOrNull().orEmpty() },
                )
            }
            item { AdminTextField("Posicion en el carrusel", displayOrder, onValueChange = { displayOrder = it.filter(Char::isDigit) }) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { isActive = !isActive },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isActive) BosqueGreenDeep else BosqueMuted, contentColor = Color.White),
                    ) {
                        Text(text = if (isActive) "Activo" else "Oculto", fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = {
                            onSave(
                                editing.copy(
                                    id = id.trim().takeUnless { it == "nuevo-anuncio" }.orEmpty().ifBlank { slugFrom(title) },
                                    eyebrow = eyebrow.trim(),
                                    title = title.trim(),
                                    detail = detail.trim(),
                                    colorHexes = colorHexes,
                                    imageKey = "",
                                    imageUrl = imageUrl.trim(),
                                    displayOrder = displayOrder.toIntOrNull() ?: 99,
                                    isActive = isActive,
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StoreActionYellow, contentColor = BosqueInk),
                    ) {
                        Text(text = "Guardar", fontWeight = FontWeight.Black)
                    }
                }
            }
            item {
                Button(
                    onClick = { onDelete(editing) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEE7), contentColor = BosqueWarmth),
                ) {
                    Text(text = "Eliminar anuncio", fontWeight = FontWeight.Black)
                }
            }
            item {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BosqueSurfaceStrong, contentColor = BosqueGreenDeep),
                ) {
                    Text(text = "Cerrar editor", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun AdminFormSectionTitle(title: String, detail: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = title, color = BosqueInk, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(text = detail, color = BosqueMuted, fontSize = 12.sp)
    }
}

@Composable
private fun AdminSingleChoiceSection(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(options) { option ->
                AdminChoiceChip(
                    text = option,
                    selected = option == selected,
                    onClick = { onSelected(option) },
                )
            }
        }
    }
}

@Composable
private fun AdminChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (selected) Color.White else BosqueInk,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) BosqueGreenDeep else Color.White)
            .border(1.dp, if (selected) BosqueGreenDeep else StoreLine, RoundedCornerShape(999.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp)
    )
}

@Composable
private fun AdminColorPicker(
    title: String,
    selectedColorHex: String,
    selectedAccentHex: String,
    onSelected: (AdminColorOption) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(adminColorOptions) { option ->
                val selected = option.colorHex.equals(selectedColorHex, ignoreCase = true) &&
                    option.accentHex.equals(selectedAccentHex, ignoreCase = true)
                Column(
                    modifier = Modifier
                        .width(118.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) StoreSoftMint else Color.White)
                        .border(2.dp, if (selected) StoreLink else StoreLine, RoundedCornerShape(16.dp))
                        .clickable(role = Role.Button) { onSelected(option) }
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(option.color))
                        Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(option.accent))
                    }
                    Text(text = option.name, color = BosqueInk, fontSize = 12.sp, fontWeight = FontWeight.Black, maxLines = 2)
                }
            }
        }
    }
}

@Composable
private fun AdminBenefitPicker(selected: List<String>, onChange: (List<String>) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Beneficios", color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(adminBenefitOptions) { benefit ->
                val active = selected.any { it.equals(benefit, ignoreCase = true) }
                AdminChoiceChip(
                    text = benefit,
                    selected = active,
                    onClick = {
                        onChange(
                            if (active) selected.filterNot { it.equals(benefit, ignoreCase = true) }
                            else (selected + benefit).take(4)
                        )
                    },
                )
            }
        }
        Text(text = "Elige hasta 4 para mostrar en las tarjetas.", color = BosqueMuted, fontSize = 11.sp)
    }
}

@Composable
private fun AdminFeaturedOfferToggle(selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) StoreSoftMint else Color.White)
            .border(1.dp, if (selected) StoreLink else StoreLine, RoundedCornerShape(18.dp))
            .clickable(role = Role.Checkbox, onClick = onToggle)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selected) BosqueGreenDeep else BosqueSurfaceSoft)
                .border(1.dp, if (selected) BosqueGreenDeep else StoreLine, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = if (selected) "OK" else "", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = "Mostrar en Ofertas destacadas", color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = if (selected) "Aparecera en el carrusel de promociones del Home." else "Producto normal, solo aparece en resultados/catalogo.", color = BosqueMuted, fontSize = 12.sp, lineHeight = 16.sp)
        }
        StatusBadge(if (selected) "Oferta" else "Normal", active = selected)
    }
}

@Composable
private fun AdminImageUrlEditor(
    title: String,
    urls: List<String>,
    allowMultiple: Boolean,
    uploadingIndex: Int?,
    onUpload: (Uri, Int) -> Unit,
    onChange: (List<String>) -> Unit,
) {
    val visibleUrls = if (urls.isEmpty()) listOf("") else urls
    var pendingUploadIndex by remember { mutableStateOf(0) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) onUpload(uri, pendingUploadIndex)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = title, color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
            if (allowMultiple) {
                AdminSmallButton(
                    text = "Agregar",
                    onClick = { onChange(visibleUrls + "") },
                    modifier = Modifier.width(110.dp),
                )
            }
        }
        visibleUrls.forEachIndexed { index, url ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, StoreLine, RoundedCornerShape(16.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RemoteImage(
                    url = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(124.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    placeholderText = "URL",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminSmallButton(
                        text = if (uploadingIndex == index) "Subiendo..." else "Subir imagen",
                        onClick = {
                            pendingUploadIndex = index
                            imagePicker.launch("image/*")
                        },
                        modifier = Modifier.weight(1f),
                    )
                    AdminSmallButton(
                        text = "Limpiar",
                        onClick = {
                            val next = visibleUrls.toMutableList()
                            next[index] = ""
                            onChange(next)
                        },
                        modifier = Modifier.weight(1f),
                        danger = true,
                    )
                }
                if (url.isNotBlank()) {
                    Text(text = "Imagen guardada", color = StoreLink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                if (allowMultiple && visibleUrls.size > 1) {
                    AdminSmallButton(
                        text = "Quitar",
                        onClick = { onChange(visibleUrls.filterIndexed { itemIndex, _ -> itemIndex != index }) },
                        modifier = Modifier.fillMaxWidth(),
                        danger = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminPromoPalettePicker(
    selectedColors: List<String>,
    onSelected: (AdminPromoPalette) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Estilo del banner", color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Black)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(adminPromoPalettes) { palette ->
                val selected = palette.colors == selectedColors
                Column(
                    modifier = Modifier
                        .width(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) StoreSoftMint else Color.White)
                        .border(2.dp, if (selected) StoreLink else StoreLine, RoundedCornerShape(16.dp))
                        .clickable(role = Role.Button) { onSelected(palette) }
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(34.dp).clip(RoundedCornerShape(12.dp))) {
                        palette.preview.forEach { color ->
                            Box(modifier = Modifier.weight(1f).fillMaxSize().background(color))
                        }
                    }
                    Text(text = palette.name, color = BosqueInk, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun AdminTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = BosqueMuted) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(color = BosqueInk, fontSize = 15.sp, fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = BosqueInk,
            unfocusedTextColor = BosqueInk,
            disabledTextColor = BosqueMuted,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = StoreLink,
            unfocusedBorderColor = StoreLine,
            focusedLabelColor = StoreLink,
            unfocusedLabelColor = BosqueMuted,
            cursorColor = StoreLink,
        ),
    )
}

@Composable
private fun QuantityButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(BosqueSurfaceStrong)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = BosqueGreenDeep, fontWeight = FontWeight.Black)
    }
}

private fun emptyProductRecord(displayOrder: Int = 99): ProductRecord =
    ProductRecord(
        id = "nuevo-producto",
        name = "Nuevo producto",
        latin = "",
        format = "",
        category = "Kits",
        price = 0,
        offerPrice = null,
        isFeaturedOffer = false,
        tag = "Nuevo",
        rating = "4.8",
        colorHex = "#426B35",
        accentHex = "#EEF4D8",
        benefits = listOf("Demo"),
        stock = 0,
        imageKeys = emptyList(),
        imageUrls = emptyList(),
        isActive = true,
        displayOrder = displayOrder,
    )

private fun emptyHomePromoRecord(displayOrder: Int = 99): HomePromoRecord =
    HomePromoRecord(
        id = "nuevo-anuncio",
        eyebrow = "Nuevo anuncio",
        title = "Titulo del anuncio",
        detail = "Detalle visible en el home.",
        colorHexes = listOf("#234428", "#5F7E3F", "#E7C56B"),
        imageKey = "",
        imageUrl = "",
        displayOrder = displayOrder,
        isActive = true,
    )

private fun slugFrom(value: String): String =
    value.lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifBlank { "nuevo-producto" }

private fun ProductRecord.toProduct(): Product =
    Product(
        id = id,
        name = name,
        latin = latin,
        format = format,
        category = category,
        price = price,
        offerPrice = offerPrice?.takeIf { it in 1 until price },
        isFeaturedOffer = isFeaturedOffer,
        tag = tag,
        rating = rating,
        color = colorHex.toComposeColor(Color(0xFF426B35)),
        accent = accentHex.toComposeColor(Color(0xFFEEF4D8)),
        benefits = benefits.take(3),
        stock = stock,
        imageUrls = cleanImageUrls(imageUrls),
    )

private fun HomePromoRecord.toPromo(): Promo =
    Promo(
        eyebrow = eyebrow,
        title = title,
        detail = detail,
        colors = colorHexes.map { it.toComposeColor(BosqueGreenDeep) }.ifEmpty {
            listOf(Color(0xFF234428), Color(0xFF5F7E3F), Color(0xFFE7C56B))
        },
        imageUrl = imageUrl.trim(),
    )

private fun cleanImageUrls(urls: List<String>): List<String> =
    urls.map { it.trim() }
        .filter { it.startsWith("http://") || it.startsWith("https://") }
        .distinct()

private suspend fun loadRemoteImageBitmap(url: String): ImageBitmap? =
    withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            connection.inputStream.use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }.also {
                connection.disconnect()
            }
        }.getOrNull()
    }

private fun Context.prepareStoreImageUpload(uri: Uri, folder: String): StoreImageUpload {
    val contentType = contentResolver.getType(uri) ?: "image/jpeg"
    val bytes = contentResolver.openInputStream(uri)?.use { stream -> stream.readBytes() }
        ?: throw IllegalStateException("No pude leer la imagen seleccionada.")
    val originalName = displayName(uri)
    val extension = originalName.substringAfterLast('.', missingDelimiterValue = "")
        .ifBlank { contentType.imageExtension() }
    val baseName = originalName.substringBeforeLast('.', missingDelimiterValue = originalName)
        .ifBlank { "imagen" }
    val safeName = "${System.currentTimeMillis()}-${slugFrom(baseName)}.$extension"

    return StoreImageUpload(
        path = "${folder.trim('/')}/$safeName",
        contentType = contentType,
        bytes = bytes,
    )
}

private fun Context.displayName(uri: Uri): String =
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0).orEmpty() else ""
    }.orEmpty().ifBlank { "imagen" }

private fun String.imageExtension(): String =
    when (lowercase(Locale.US)) {
        "image/png" -> "png"
        "image/webp" -> "webp"
        "image/gif" -> "gif"
        else -> "jpg"
    }

private fun String.toComposeColor(fallback: Color): Color =
    runCatching {
        val clean = trim().removePrefix("#")
        val argb = when (clean.length) {
            6 -> "FF$clean"
            8 -> clean
            else -> return fallback
        }
        Color(argb.toLong(16))
    }.getOrDefault(fallback)

private fun Product.popularityScore(): Double =
    rating.replace(",", ".").toDoubleOrNull() ?: 0.0

private fun Product.isOfferProduct(): Boolean = isFeaturedOffer

private val Product.hasValidOffer: Boolean
    get() = offerPrice != null && offerPrice > 0 && offerPrice < price

private val Product.currentPrice: Int
    get() = offerPrice?.takeIf { it > 0 && it < price } ?: price

private val Product.discountPercent: Int
    get() = if (price <= 0 || !hasValidOffer) {
        0
    } else {
        (((price - currentPrice) * 100.0) / price).roundToInt().coerceAtLeast(1)
    }

private fun productDescription(product: Product): String {
    val benefitCopy = product.benefits.joinToString(", ").ifBlank { "bienestar diario" }
    val format = product.format.ifBlank { product.category.lowercase() }
    return "${product.name} acompana rutinas de $benefitCopy en formato $format. Esta ficha resume la experiencia esperada para presentacion: origen naturalista, compra simple y recomendaciones claras para el cliente."
}

private fun productBenefitCopy(product: Product): List<String> {
    val mapped = product.benefits.map { benefit ->
        when (benefit.lowercase()) {
            "relax" -> "Apoya momentos de pausa y descanso dentro de una rutina equilibrada."
            "energia", "energía" -> "Ideal para rutinas activas o mananas que necesitan un impulso natural."
            "foco" -> "Pensado para acompanar estudio, trabajo profundo o tareas de concentracion."
            "noche" -> "Encaja bien en rituales tranquilos al final del dia."
            "ritual" -> "Facil de integrar en cafe, infusiones o pausas personales."
            "balance" -> "Aporta una opcion suave para sostener habitos diarios de bienestar."
            "cafe" -> "Combina con bebidas calientes sin complicar la preparacion."
            "regalo" -> "Buena opcion para armar un detalle naturalista y premium."
            else -> "Beneficio destacado: $benefit."
        }
    }

    return (mapped + listOf(
        "Seleccionado para una experiencia Zeta Dorada premium, simple y confiable.",
        "Ficha visual preparada para explicar valor, formato y uso antes de comprar.",
    )).distinct().take(4)
}

private fun productUsageCopy(product: Product): List<String> {
    val lowerFormat = product.format.lowercase()
    return when {
        "caps" in lowerFormat || "capsula" in lowerFormat || "capsule" in lowerFormat -> listOf(
            "Tomar con agua siguiendo la indicacion del empaque o recomendacion profesional.",
            "Usar de forma constante dentro de una rutina de alimentacion equilibrada.",
            "Evitar duplicar porciones si tambien consumes otros suplementos similares.",
        )
        "tintura" in lowerFormat -> listOf(
            "Agitar antes de usar y diluir las gotas en agua, infusion o bebida tibia.",
            "Empezar con una porcion pequena para observar tolerancia.",
            "Guardar bien cerrado, lejos de calor directo y luz intensa.",
        )
        "polvo" in lowerFormat || "powder" in lowerFormat -> listOf(
            "Mezclar con cafe, cacao, batidos o infusiones hasta integrar bien.",
            "Usar una medida constante para mantener el sabor y la rutina.",
            "Cerrar el envase despues de cada uso para conservar textura y aroma.",
        )
        else -> listOf(
            "Revisar el contenido del kit y elegir un momento fijo del dia para usarlo.",
            "Combinarlo con agua, bebida caliente o la rutina recomendada por el empaque.",
            "Guardar los productos en un lugar fresco y seco.",
        )
    }
}

private fun productWarningCopy(product: Product): List<String> =
    listOf(
        "Producto de bienestar: no reemplaza diagnostico, tratamiento medico ni una dieta variada.",
        "Consulta a un profesional si estas embarazada, lactando, tomando medicacion o tienes una condicion medica.",
        "Suspende su uso si notas molestias y mantenlo fuera del alcance de ninos.",
    )

private fun formatSoles(amount: Int): String = "S/ ${amount}.00"

private fun formatOrderStatus(status: String): String =
    when (status) {
        "paid" -> "Pagado"
        "preparing" -> "Preparando"
        "ready" -> "Listo"
        "completed" -> "Completado"
        "cancelled" -> "Cancelado"
        else -> status.ifBlank { "Pagado" }
    }

@SuppressLint("MissingPermission")
private fun showAdminOrderNotifications(context: Context, notifications: List<AdminNotificationRecord>) {
    if (notifications.isEmpty()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    ensureAdminNotificationChannel(context)

    val prefs = context.getSharedPreferences(ADMIN_NOTIFICATION_PREFS, Context.MODE_PRIVATE)
    val shownIds = prefs.getStringSet("shown_ids", emptySet()).orEmpty().toMutableSet()
    val pendingIntent = PendingIntent.getActivity(
        context,
        2001,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val manager = NotificationManagerCompat.from(context)

    notifications
        .filter { it.id.isNotBlank() && !shownIds.contains(it.id) }
        .forEach { notification ->
            val title = notification.title.ifBlank { "Nuevo pedido" }
            val body = notification.body.ifBlank { "Tienes un pedido nuevo para revisar." }
            val systemNotification = NotificationCompat.Builder(context, ADMIN_ORDER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_zeta)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(0xFF284B2E.toInt())
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .build()

            manager.notify(notification.id.hashCode(), systemNotification)
            shownIds.add(notification.id)
        }

    prefs.edit().putStringSet("shown_ids", shownIds).apply()
}

private fun ensureAdminNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val manager = context.getSystemService(NotificationManager::class.java)
    if (manager.getNotificationChannel(ADMIN_ORDER_NOTIFICATION_CHANNEL_ID) != null) return

    val channel = NotificationChannel(
        ADMIN_ORDER_NOTIFICATION_CHANNEL_ID,
        "Pedidos admin",
        NotificationManager.IMPORTANCE_HIGH,
    ).apply {
        description = "Alertas para pedidos nuevos de Zeta Dorada"
        enableVibration(true)
        setShowBadge(true)
    }

    manager.createNotificationChannel(channel)
}

private const val SESSION_PREFS = "zeta_session"

private fun saveSession(context: Context, session: SupabaseSession) {
    val json = org.json.JSONObject()
        .put("accessToken", session.accessToken)
        .put("refreshToken", session.refreshToken)
        .put("userId", session.userId)
        .put("email", session.email)
        .toString()
    context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString("session", json)
        .apply()
}

private fun loadSavedSession(context: Context): SupabaseSession? =
    runCatching {
        val json = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .getString("session", null)
            ?: return null
        val root = org.json.JSONObject(json)
        SupabaseSession(
            accessToken = root.getString("accessToken"),
            refreshToken = root.optString("refreshToken"),
            userId = root.getString("userId"),
            email = root.optString("email"),
        )
    }.getOrNull()

private fun clearSavedSession(context: Context) {
    context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
        .edit()
        .remove("session")
        .apply()
}

private fun String?.asRealId(): String? =
    this
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?.takeUnless { it.equals("null", ignoreCase = true) }

private fun Throwable.isJwtExpired(): Boolean =
    message.orEmpty().contains("jwt expired", ignoreCase = true)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun BosqueVivoStorePreview() {
    ZetaTheme {
        Surface {
            BosqueVivoStore()
        }
    }
}
