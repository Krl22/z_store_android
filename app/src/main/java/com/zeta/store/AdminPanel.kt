package com.zeta.store

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zeta.store.data.AdminCustomerRecord
import com.zeta.store.data.AdminNotificationPreferences
import com.zeta.store.data.AdminNotificationRecord
import com.zeta.store.data.HomePromoRecord
import com.zeta.store.data.OrderRecord
import com.zeta.store.data.ProductRecord
import com.zeta.store.data.StoreSettingsRecord
import com.zeta.store.data.SupabaseClient
import com.zeta.store.data.SupabaseSession
import com.zeta.store.ui.theme.BosqueBackground
import com.zeta.store.ui.theme.BosqueBorder
import com.zeta.store.ui.theme.BosqueGreen
import com.zeta.store.ui.theme.BosqueGreenDeep
import com.zeta.store.ui.theme.BosqueInk
import com.zeta.store.ui.theme.BosqueLime
import com.zeta.store.ui.theme.BosqueMuted
import com.zeta.store.ui.theme.BosqueSurfaceSoft
import com.zeta.store.ui.theme.BosqueSurfaceStrong
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Panel admin con la estructura del de Intu (encabezado, secciones en chips, buscador, filtros,
// tarjetas con detalle desplegable y confirmación de acciones) y la paleta de Zeta Dorada.

private val AdminGold = Color(0xFF8A5D0B)
private val AdminGoldSoft = Color(0xFFFFF4D6)
private val AdminRed = Color(0xFFB42318)
private val AdminRedSoft = Color(0xFFFFEDE9)
private val AdminOk = Color(0xFF067647)
private val AdminOkSoft = Color(0xFFE8F6EE)

private val AdminColors = lightColorScheme(
    primary = BosqueGreen, onPrimary = Color.White,
    primaryContainer = BosqueLime, onPrimaryContainer = BosqueInk,
    secondary = BosqueMuted, onSecondary = Color.White,
    secondaryContainer = BosqueSurfaceStrong, onSecondaryContainer = BosqueInk,
    tertiary = AdminGold, tertiaryContainer = AdminGoldSoft,
    background = BosqueBackground, onBackground = BosqueInk,
    surface = Color.White, onSurface = BosqueInk,
    surfaceVariant = BosqueSurfaceSoft, onSurfaceVariant = BosqueMuted,
    outline = Color(0xFF9AA389), outlineVariant = BosqueBorder,
    error = AdminRed, errorContainer = AdminRedSoft, onErrorContainer = AdminRed,
    surfaceContainer = Color.White, surfaceContainerLow = Color.White,
    surfaceContainerHigh = BosqueSurfaceSoft,
)

private enum class AdminSection(val label: String, val icon: ImageVector) {
    Orders("Pedidos", Icons.AutoMirrored.Outlined.ReceiptLong),
    Products("Productos", Icons.Outlined.Inventory2),
    Promos("Anuncios", Icons.Outlined.Campaign),
    Customers("Clientes", Icons.Outlined.People),
    Notifications("Notificaciones", Icons.Outlined.Notifications),
    Store("Tienda", Icons.Outlined.Storefront),
}

private val OrderFilters = listOf(
    "pending_payment" to "Pendientes de pago",
    "paid" to "Pagados",
    "preparing" to "Preparando",
    "ready" to "Listos",
    "completed" to "Completados",
    "cancelled" to "Cancelados",
    null to "Todos",
)

private val ProductFilters = listOf(
    null to "Todos",
    "active" to "Activos",
    "hidden" to "Ocultos",
    "low" to "Stock bajo",
    "offer" to "En oferta",
)

private val CustomerFilters = listOf(
    null to "Todos",
    "orders" to "Con pedidos",
    "pending" to "Pago pendiente",
    "admin" to "Admins",
)

/**
 * Panel de administración de Zeta Dorada.
 * Los pedidos, productos y anuncios llegan del estado de la tienda (se comparten con el catálogo);
 * clientes, avisos y ajustes los carga cada sección con [client].
 */
@Composable
internal fun ZetaAdminPanel(
    client: SupabaseClient,
    session: SupabaseSession,
    products: List<ProductRecord>,
    promos: List<HomePromoRecord>,
    orders: List<OrderRecord>,
    notifications: List<AdminNotificationRecord>,
    storeSettings: StoreSettingsRecord?,
    onRefresh: suspend () -> Unit,
    onSaveProduct: (ProductRecord) -> Unit,
    onDeleteProduct: (ProductRecord) -> Unit,
    onSavePromo: (HomePromoRecord) -> Unit,
    onDeletePromo: (HomePromoRecord) -> Unit,
    onUpdateOrderStatus: suspend (OrderRecord, String) -> Unit,
    onMarkNotificationsRead: suspend () -> Unit,
    onStoreSettingsSaved: (StoreSettingsRecord) -> Unit,
    onOwnRoleRemoved: () -> Unit,
    onOwnAccountDeleted: () -> Unit,
    onMessage: (String?) -> Unit,
) {
    var section by rememberSaveable { mutableStateOf(AdminSection.Orders) }
    var orderFilter by rememberSaveable { mutableStateOf<String?>("pending_payment") }
    var productFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableIntStateOf(0) }
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 104.dp
    val lowStockThreshold = storeSettings?.lowStockThreshold ?: 3
    val pendingPayment = orders.count { it.status == "pending_payment" }
    val lowStock = products.count { it.isActive && it.stock <= lowStockThreshold }

    fun refresh() {
        if (refreshing) return
        refreshing = true
        reloadKey++
        scope.launch {
            runCatching { onRefresh() }
                .onFailure { if (it is CancellationException) throw it; Toast.makeText(context, it.message ?: "No se pudo actualizar.", Toast.LENGTH_LONG).show() }
            refreshing = false
        }
    }

    AdminPanelTheme {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.statusBarsPadding()) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("ZETA DORADA · ADMIN", color = AdminGold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("Administración", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalIconButton(onClick = ::refresh, enabled = !refreshing) {
                            if (refreshing) {
                                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Outlined.Refresh, "Actualizar")
                            }
                        }
                    }
                    if (pendingPayment > 0 || lowStock > 0) {
                        AdminAttentionCard(
                            pendingPayment = pendingPayment,
                            lowStock = lowStock,
                            onClick = {
                                if (pendingPayment > 0) {
                                    section = AdminSection.Orders
                                    orderFilter = "pending_payment"
                                } else {
                                    section = AdminSection.Products
                                    productFilter = "low"
                                }
                            },
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AdminSection.entries.forEach { entry ->
                            val unread = if (entry == AdminSection.Notifications) notifications.count { !it.isRead } else 0
                            FilterChip(
                                selected = section == entry,
                                onClick = { section = entry },
                                label = { Text(if (unread > 0) "${entry.label} ($unread)" else entry.label) },
                                leadingIcon = { Icon(entry.icon, null, Modifier.size(18.dp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BosqueGreenDeep,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                ),
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (section) {
                    AdminSection.Orders -> OrdersSection(orders, orderFilter, { orderFilter = it }, bottomPadding, onUpdateOrderStatus)
                    AdminSection.Products -> ProductsSection(
                        client, session, products, productFilter, { productFilter = it }, lowStockThreshold, bottomPadding,
                        onRefresh, onSaveProduct, onDeleteProduct, onMessage,
                    )
                    AdminSection.Promos -> PromosSection(
                        client, session, promos, storeSettings?.adRotationSeconds, bottomPadding,
                        onSavePromo, onDeletePromo, onMessage,
                    )
                    AdminSection.Customers -> CustomersSection(client, session, reloadKey, bottomPadding, onOwnRoleRemoved, onOwnAccountDeleted)
                    AdminSection.Notifications -> NotificationsSection(client, session, reloadKey, notifications, bottomPadding, onMarkNotificationsRead)
                    AdminSection.Store -> StoreSection(client, session, reloadKey, bottomPadding, onStoreSettingsSaved)
                }
            }
        }
    }
}

@Composable
private fun AdminPanelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AdminColors,
        typography = MaterialTheme.typography,
        shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(18.dp), large = RoundedCornerShape(24.dp)),
        content = content,
    )
}

@Composable
private fun AdminAttentionCard(pendingPayment: Int, lowStock: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Outlined.NotificationsActive, null, tint = AdminGold, modifier = Modifier.size(20.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    listOfNotNull(
                        pendingPayment.takeIf { it > 0 }?.let { if (it == 1) "1 pedido espera pago" else "$it pedidos esperan pago" },
                        lowStock.takeIf { it > 0 }?.let { if (it == 1) "1 producto con stock bajo" else "$it productos con stock bajo" },
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text("Tocar para revisar", style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = BosqueMuted)
        }
    }
}

// ---------- Pedidos ----------

@Composable
private fun OrdersSection(
    orders: List<OrderRecord>,
    filter: String?,
    onFilter: (String?) -> Unit,
    bottomPadding: Dp,
    onUpdateStatus: suspend (OrderRecord, String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var busyOrderId by remember { mutableStateOf<String?>(null) }
    var pendingConfirmation by remember { mutableStateOf<Pair<OrderRecord, String>?>(null) }

    fun changeStatus(order: OrderRecord, status: String) {
        busyOrderId = order.id
        scope.launch {
            runCatching { onUpdateStatus(order, status) }
                .onSuccess {
                    Toast.makeText(context, "Pedido #${order.code}: ${formatOrderStatus(status).lowercase()}", Toast.LENGTH_SHORT).show()
                }
                .onFailure {
                    if (it is CancellationException) throw it
                    Toast.makeText(context, it.message ?: "No se pudo actualizar el pedido.", Toast.LENGTH_LONG).show()
                }
            busyOrderId = null
        }
    }

    val needle = adminNormalize(query)
    val visible = orders
        .filter { filter == null || it.status == filter }
        .filter { needle in adminNormalize("${it.code} ${it.customerName} ${it.customerPhone} ${it.customerEmail}") }

    AdminList(bottomPadding) {
        item { AdminSectionHeading("Pedidos", "Confirma pagos y avanza cada pedido hasta la entrega. Abre el detalle para ver productos y dirección.", orders.size) }
        item { AdminSearchField(query, { query = it }, "Código, cliente o teléfono") }
        item {
            AdminFilters(
                OrderFilters.map { (code, label) -> code to if (code == null) label else "$label (${orders.count { it.status == code }})" },
                filter,
                onFilter,
            )
        }
        if (visible.isEmpty()) {
            item {
                AdminMessage(
                    when {
                        query.isNotBlank() -> "No hay pedidos que coincidan con tu búsqueda."
                        filter == "pending_payment" -> "No hay pedidos esperando pago."
                        else -> "No hay pedidos en esta lista."
                    }
                )
            }
        } else {
            items(visible, key = { it.id }) { order ->
                AdminOrderCard(
                    order = order,
                    busy = busyOrderId == order.id,
                    onStatus = { status ->
                        if (status == "cancelled") pendingConfirmation = order to status else changeStatus(order, status)
                    },
                )
            }
        }
    }

    pendingConfirmation?.let { (order, status) ->
        AdminConfirmDialog(
            title = "¿Cancelar el pedido #${order.code}?",
            message = "El stock reservado vuelve a la tienda y el pedido no se puede reabrir." +
                if (order.paymentStatus == "paid") " Si el cliente ya pagó, devuélvele el dinero por Yape." else "",
            confirmLabel = "Cancelar pedido",
            destructive = true,
            onConfirm = {
                pendingConfirmation = null
                changeStatus(order, status)
            },
            onDismiss = { pendingConfirmation = null },
        )
    }
}

@Composable
private fun AdminOrderCard(order: OrderRecord, busy: Boolean, onStatus: (String) -> Unit) {
    val context = LocalContext.current
    var expanded by rememberSaveable(order.id) { mutableStateOf(false) }
    val itemCount = order.items.sumOf { it.quantity }

    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Pedido #${order.code}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    listOf(adminDate(order.createdAt), if (itemCount == 1) "1 producto" else "$itemCount productos").filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = BosqueMuted,
                )
            }
            AdminOrderStatusChip(order.status)
        }
        Text(order.customerName.ifBlank { order.customerEmail.ifBlank { "Cliente sin nombre" } }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        if (order.customerPhone.isNotBlank()) Text(order.customerPhone, style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminDetail("Total", formatSoles(order.total), Modifier.weight(1f))
            AdminDetail("Entrega", if (order.deliveryMethod == "pickup") "Recojo" else "Delivery", Modifier.weight(1f))
            AdminDetail("Pago", formatPaymentStatus(order.paymentStatus), Modifier.weight(1.4f))
        }
        TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text(if (expanded) "Ocultar detalle" else "Ver detalle")
            Icon(if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null)
        }
        AnimatedVisibility(expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                order.items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        RemoteImage(
                            url = item.imageUrl,
                            contentDescription = item.productName,
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)),
                        )
                        Column(Modifier.weight(1f)) {
                            Text(item.productName, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.quantity} × ${formatSoles(item.unitPrice)}", style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
                        }
                        Text(formatSoles(item.lineTotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                if (order.deliveryMethod != "pickup") AdminDetail("Dirección", formatAddressSnapshot(order.deliveryAddressSnapshot))
                AdminDetail("Correo", order.customerEmail)
                if (order.userId.isBlank()) AdminDetail("Cuenta", "Eliminada")
            }
        }
        adminWhatsappUrl(order.customerPhone, "Hola${order.customerName.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()}, te escribimos de Zeta Dorada por tu pedido #${order.code}.")?.let { url ->
            OutlinedButton(onClick = { context.openWhatsapp(url) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Outlined.Chat, null, Modifier.padding(end = 8.dp).size(18.dp))
                Text("Escribir por WhatsApp")
            }
        }
        val next = when (order.status) {
            "pending_payment" -> "paid" to "Pago recibido"
            "paid" -> "preparing" to "Marcar preparando"
            "preparing" -> "ready" to "Marcar listo"
            "ready" -> "completed" to "Marcar entregado"
            else -> null
        }
        when {
            busy -> AdminLoading()
            next != null -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onStatus("cancelled") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminRed),
                ) { Text("Cancelar") }
                Button(onClick = { onStatus(next.first) }, modifier = Modifier.weight(1.4f)) { Text(next.second) }
            }
        }
    }
}

@Composable
private fun AdminOrderStatusChip(status: String) {
    val (fg, bg) = when (status) {
        "pending_payment" -> AdminGold to AdminGoldSoft
        "cancelled" -> AdminRed to AdminRedSoft
        "completed" -> BosqueMuted to BosqueSurfaceSoft
        else -> AdminOk to AdminOkSoft
    }
    AdminBadge(formatOrderStatus(status), fg, bg)
}

// ---------- Productos ----------

@Composable
private fun ProductsSection(
    client: SupabaseClient,
    session: SupabaseSession,
    products: List<ProductRecord>,
    filter: String?,
    onFilter: (String?) -> Unit,
    lowStockThreshold: Int,
    bottomPadding: Dp,
    onRefresh: suspend () -> Unit,
    onSave: (ProductRecord) -> Unit,
    onDelete: (ProductRecord) -> Unit,
    onMessage: (String?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var editorSeed by remember { mutableStateOf<ProductRecord?>(null) }
    var pendingDelete by remember { mutableStateOf<ProductRecord?>(null) }
    var busyProductId by remember { mutableStateOf<String?>(null) }

    fun matches(product: ProductRecord, code: String?): Boolean = when (code) {
        "active" -> product.isActive
        "hidden" -> !product.isActive
        "low" -> product.isActive && product.stock <= lowStockThreshold
        "offer" -> product.hasAdminOffer
        else -> true
    }

    fun saveStock(product: ProductRecord, stock: Int) {
        busyProductId = product.id
        scope.launch {
            runCatching {
                client.updateProductStock(session, product.id, stock)
                onRefresh()
            }.onSuccess {
                Toast.makeText(context, "${product.name}: stock $stock", Toast.LENGTH_SHORT).show()
            }.onFailure {
                if (it is CancellationException) throw it
                Toast.makeText(context, it.message ?: "No se pudo guardar el stock.", Toast.LENGTH_LONG).show()
            }
            busyProductId = null
        }
    }

    val needle = adminNormalize(query)
    val visible = products
        .filter { matches(it, filter) }
        .filter { needle in adminNormalize("${it.name} ${it.category} ${it.format} ${it.id}") }

    AdminList(bottomPadding) {
        item { AdminSectionHeading("Productos", "Edita precios, stock e imágenes. Los productos ocultos no aparecen en la tienda.", products.size) }
        item {
            Button(onClick = { editorSeed = emptyProductRecord(displayOrder = products.size + 1) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Add, null, Modifier.padding(end = 8.dp))
                Text("Nuevo producto")
            }
        }
        item { AdminSearchField(query, { query = it }, "Nombre o categoría") }
        item {
            AdminFilters(
                ProductFilters.map { (code, label) -> code to if (code == null) label else "$label (${products.count { matches(it, code) }})" },
                filter,
                onFilter,
            )
        }
        if (visible.isEmpty()) {
            item { AdminMessage(if (query.isNotBlank()) "No hay productos que coincidan con tu búsqueda." else "No hay productos en esta lista.") }
        } else {
            items(visible, key = { it.id }) { product ->
                AdminProductCard(
                    product = product,
                    lowStockThreshold = lowStockThreshold,
                    busy = busyProductId == product.id,
                    onSaveStock = { saveStock(product, it) },
                    onEdit = { editorSeed = product },
                    onDelete = { pendingDelete = product },
                )
            }
        }
    }

    editorSeed?.let { seed ->
        AdminPanelDialog(
            client = client,
            session = session,
            products = products,
            initialProduct = seed,
            onDismiss = { editorSeed = null },
            onMessage = onMessage,
            onSave = onSave,
            onDelete = {
                editorSeed = null
                pendingDelete = it
            },
        )
    }
    pendingDelete?.let { product ->
        AdminConfirmDialog(
            title = "¿Eliminar ${product.name}?",
            message = "Desaparece de la tienda y de los carritos. Los pedidos anteriores conservan el nombre y el precio. Si solo quieres sacarlo un tiempo, edítalo y desactívalo.",
            confirmLabel = "Eliminar",
            destructive = true,
            onConfirm = {
                pendingDelete = null
                onDelete(product)
            },
            onDismiss = { pendingDelete = null },
        )
    }
}

@Composable
private fun AdminProductCard(
    product: ProductRecord,
    lowStockThreshold: Int,
    busy: Boolean,
    onSaveStock: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var stock by remember(product.id, product.stock) { mutableIntStateOf(product.stock) }

    AdminCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RemoteImage(
                url = product.imageUrls.firstOrNull().orEmpty().ifBlank { product.imageKeys.firstOrNull().orEmpty() },
                contentDescription = product.name,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(listOf(product.category, product.format).filter { it.isNotBlank() }.joinToString(" · "), style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (product.hasAdminOffer) {
                        Text(formatSoles(product.offerPrice ?: product.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(formatSoles(product.price), style = MaterialTheme.typography.bodySmall, color = BosqueMuted, textDecoration = TextDecoration.LineThrough)
                    } else {
                        Text(formatSoles(product.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!product.isActive) AdminBadge("Oculto", BosqueMuted, BosqueSurfaceSoft)
            when {
                product.stock == 0 -> AdminBadge("Agotado", AdminRed, AdminRedSoft)
                product.stock <= lowStockThreshold -> AdminBadge("Stock bajo", AdminGold, AdminGoldSoft)
            }
            if (product.hasAdminOffer) AdminBadge("Oferta", AdminOk, AdminOkSoft)
            if (product.isFeaturedOffer) AdminBadge("Destacado")
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Stock", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            if (busy) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                AdminStepper(stock, { stock = it.coerceIn(0, 9999) })
                if (stock != product.stock) {
                    TextButton(onClick = { onSaveStock(stock) }) { Text("Guardar") }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.Edit, null, Modifier.padding(end = 6.dp).size(18.dp))
                Text("Editar")
            }
            OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminRed)) {
                Text("Eliminar")
            }
        }
    }
}

private val ProductRecord.hasAdminOffer: Boolean
    get() = offerPrice != null && offerPrice > 0 && offerPrice < price

// ---------- Anuncios ----------

@Composable
private fun PromosSection(
    client: SupabaseClient,
    session: SupabaseSession,
    promos: List<HomePromoRecord>,
    rotationSeconds: Int?,
    bottomPadding: Dp,
    onSave: (HomePromoRecord) -> Unit,
    onDelete: (HomePromoRecord) -> Unit,
    onMessage: (String?) -> Unit,
) {
    var editorSeed by remember { mutableStateOf<HomePromoRecord?>(null) }
    var pendingDelete by remember { mutableStateOf<HomePromoRecord?>(null) }
    val sorted = promos.sortedBy { it.displayOrder }

    AdminList(bottomPadding) {
        item {
            AdminSectionHeading(
                "Anuncios del inicio",
                "Banners del carrusel de la tienda. Cambian cada ${rotationSeconds ?: 4} s; puedes ajustarlo en Tienda.",
                promos.size,
            )
        }
        item {
            Button(onClick = { editorSeed = emptyHomePromoRecord(displayOrder = promos.size + 1) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Add, null, Modifier.padding(end = 8.dp))
                Text("Nuevo anuncio")
            }
        }
        if (sorted.isEmpty()) {
            item { AdminMessage("No hay anuncios. La tienda muestra un banner por defecto.") }
        } else {
            items(sorted, key = { it.id }) { promo ->
                AdminCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RemoteImage(
                            url = promo.imageUrl,
                            contentDescription = promo.title,
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)),
                        )
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            if (promo.eyebrow.isNotBlank()) Text(promo.eyebrow.uppercase(), style = MaterialTheme.typography.labelSmall, color = AdminGold, fontWeight = FontWeight.Bold)
                            Text(promo.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            if (promo.detail.isNotBlank()) Text(promo.detail, style = MaterialTheme.typography.bodySmall, color = BosqueMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (promo.isActive) AdminBadge("Visible", AdminOk, AdminOkSoft) else AdminBadge("Oculto", BosqueMuted, BosqueSurfaceSoft)
                        AdminBadge("Posición ${promo.displayOrder}", BosqueMuted, BosqueSurfaceSoft)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { editorSeed = promo }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Outlined.Edit, null, Modifier.padding(end = 6.dp).size(18.dp))
                            Text("Editar")
                        }
                        OutlinedButton(onClick = { pendingDelete = promo }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = AdminRed)) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }

    editorSeed?.let { seed ->
        HomePromoAdminDialog(
            client = client,
            session = session,
            promos = promos,
            initialPromo = seed,
            onDismiss = { editorSeed = null },
            onMessage = onMessage,
            onSave = onSave,
            onDelete = {
                editorSeed = null
                pendingDelete = it
            },
        )
    }
    pendingDelete?.let { promo ->
        AdminConfirmDialog(
            title = "¿Eliminar el anuncio?",
            message = "\"${promo.title}\" deja de aparecer en el carrusel. Si solo quieres pausarlo, edítalo y ocúltalo.",
            confirmLabel = "Eliminar",
            destructive = true,
            onConfirm = {
                pendingDelete = null
                onDelete(promo)
            },
            onDismiss = { pendingDelete = null },
        )
    }
}

// ---------- Clientes ----------

private enum class CustomerAction { ResetCart, ResetAccount, MakeAdmin, RemoveAdmin, Delete }

@Composable
private fun CustomersSection(
    client: SupabaseClient,
    session: SupabaseSession,
    reloadKey: Int,
    bottomPadding: Dp,
    onOwnRoleRemoved: () -> Unit,
    onOwnAccountDeleted: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val myId = session.userId
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf<String?>(null) }
    var customers by remember { mutableStateOf<List<AdminCustomerRecord>?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var localReload by remember { mutableIntStateOf(0) }
    var busyId by remember { mutableStateOf<String?>(null) }
    var pendingAction by remember { mutableStateOf<Pair<AdminCustomerRecord, CustomerAction>?>(null) }

    LaunchedEffect(reloadKey, localReload) {
        customers = null
        loadError = null
        try {
            customers = client.fetchCustomers(session)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            loadError = e.message ?: "No se pudo cargar la lista."
            customers = emptyList()
        }
    }

    fun run(customer: AdminCustomerRecord, action: CustomerAction) {
        busyId = customer.id
        scope.launch {
            runCatching {
                when (action) {
                    CustomerAction.ResetCart -> client.resetCustomer(session, customer.id, "cart")
                    CustomerAction.ResetAccount -> client.resetCustomer(session, customer.id, "account")
                    CustomerAction.MakeAdmin -> client.setCustomerAdmin(session, customer.id, true)
                    CustomerAction.RemoveAdmin -> client.setCustomerAdmin(session, customer.id, false)
                    CustomerAction.Delete -> client.deleteCustomer(session, customer.id)
                }
            }.onSuccess {
                val name = customer.displayName
                val done = when (action) {
                    CustomerAction.ResetCart -> "Carrito de $name vaciado"
                    CustomerAction.ResetAccount -> "Cuenta de $name reiniciada"
                    CustomerAction.MakeAdmin -> "$name ahora es administrador"
                    CustomerAction.RemoveAdmin -> "$name ya no es administrador"
                    CustomerAction.Delete -> "Cuenta eliminada"
                }
                Toast.makeText(context, done, Toast.LENGTH_SHORT).show()
                when {
                    action == CustomerAction.Delete && customer.id == myId -> onOwnAccountDeleted()
                    action == CustomerAction.RemoveAdmin && customer.id == myId -> onOwnRoleRemoved()
                    else -> localReload++
                }
            }.onFailure {
                if (it is CancellationException) throw it
                Toast.makeText(context, it.message ?: "No se pudo completar la acción.", Toast.LENGTH_LONG).show()
            }
            busyId = null
        }
    }

    fun matches(customer: AdminCustomerRecord, code: String?): Boolean = when (code) {
        "orders" -> customer.ordersCount > 0
        "pending" -> customer.pendingOrdersCount > 0
        "admin" -> customer.isAdmin
        else -> true
    }

    val list = customers
    val needle = adminNormalize(query)
    val visible = list.orEmpty()
        .filter { matches(it, filter) }
        .filter { needle in adminNormalize("${it.fullName} ${it.phone} ${it.email} ${it.id}") }

    AdminList(bottomPadding) {
        item { AdminSectionHeading("Clientes", "Consulta compras, acceso y contacto. Abre Gestionar cuenta para permisos o para repetir pruebas.", list?.size) }
        item { AdminSearchField(query, { query = it }, "Nombre, teléfono o correo") }
        if (list != null && loadError == null) {
            item {
                AdminFilters(
                    CustomerFilters.map { (code, label) -> code to if (code == null) label else "$label (${list.count { matches(it, code) }})" },
                    filter,
                ) { filter = it }
            }
        }
        when {
            list == null -> item { AdminLoading() }
            loadError != null -> item { AdminMessage(loadError.orEmpty(), isError = true) { localReload++ } }
            visible.isEmpty() -> item { AdminMessage(if (query.isBlank()) "No hay clientes en esta lista." else "No hay clientes que coincidan con tu búsqueda.") }
            else -> items(visible, key = { it.id }) { customer ->
                AdminCustomerCard(customer, customer.id == myId, busyId == customer.id) { action -> pendingAction = customer to action }
            }
        }
        item {
            Text(
                "Los pedidos se conservan al reiniciar o eliminar una cuenta. Para ver un reinicio, la persona debe cerrar y volver a abrir la app.",
                style = MaterialTheme.typography.bodySmall,
                color = BosqueMuted,
            )
        }
    }

    pendingAction?.let { (customer, action) ->
        val name = customer.displayName
        val isMe = customer.id == myId
        val (title, message, confirm) = when (action) {
            CustomerAction.ResetCart -> Triple(
                "¿Vaciar carrito y guardados?",
                "Se quitarán los productos del carrito y de guardados de $name. Sus pedidos no cambian.",
                "Vaciar",
            )
            CustomerAction.ResetAccount -> Triple(
                "¿Reiniciar cuenta?",
                "Se borrarán las direcciones, el nombre y el teléfono de $name, además de su carrito y guardados. " +
                    "Sus pedidos pendientes de pago se cancelan y el stock vuelve a la tienda. El historial de pedidos se conserva.",
                "Reiniciar",
            )
            CustomerAction.MakeAdmin -> Triple(
                "¿Hacer administrador?",
                "$name podrá entrar a este panel: editar productos y anuncios, cambiar pedidos, ver clientes y eliminar cuentas.",
                "Hacer admin",
            )
            CustomerAction.RemoveAdmin -> Triple(
                "¿Quitar administrador?",
                if (isMe) "Dejarás de ver este panel. Otro administrador tendrá que devolverte el permiso." else "$name ya no podrá entrar a este panel.",
                "Quitar",
            )
            CustomerAction.Delete -> Triple(
                if (isMe) "¿Eliminar tu propia cuenta?" else "¿Eliminar cuenta?",
                (if (isMe) "Es la cuenta con la que estás usando la app: se cerrará tu sesión. " else "") +
                    "Se borrarán el inicio de sesión, el perfil, las direcciones, el carrito y los guardados de $name. " +
                    "Sus pedidos pendientes de pago se cancelan; los demás pedidos se conservan sin usuario. " +
                    "Si vuelve a entrar con Google, será una cuenta nueva.",
                "Eliminar",
            )
        }
        AdminConfirmDialog(
            title = title,
            message = message,
            confirmLabel = confirm,
            destructive = action != CustomerAction.MakeAdmin,
            requireAcknowledgement = if (action == CustomerAction.Delete) "Entiendo que esto no se puede deshacer" else null,
            onConfirm = {
                pendingAction = null
                run(customer, action)
            },
            onDismiss = { pendingAction = null },
        )
    }
}

@Composable
private fun AdminCustomerCard(customer: AdminCustomerRecord, isMe: Boolean, busy: Boolean, onAction: (CustomerAction) -> Unit) {
    val context = LocalContext.current
    var expanded by rememberSaveable(customer.id) { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }

    AdminCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(BosqueSurfaceStrong),
                contentAlignment = Alignment.Center,
            ) {
                Text(customer.displayName.take(1).uppercase(), style = MaterialTheme.typography.titleMedium, color = BosqueGreenDeep, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    customer.fullName.ifBlank { "Perfil sin nombre" } + if (isMe) " (tú)" else "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (customer.isAdmin) AdminBadge("Administrador")
            }
        }
        if (customer.email.isNotBlank()) Text(customer.email, style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
        if (customer.phone.isNotBlank()) Text(customer.phone, style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AdminStat("Pedidos", customer.ordersCount.toString(), Modifier.weight(1f))
            AdminStat("Pagado", formatSoles(customer.totalPaid), Modifier.weight(1.3f))
            AdminStat("En carrito", customer.cartUnits.toString(), Modifier.weight(1f))
        }
        if (customer.pendingOrdersCount > 0) {
            AdminBadge(
                if (customer.pendingOrdersCount == 1) "1 pedido pendiente de pago" else "${customer.pendingOrdersCount} pedidos pendientes de pago",
                AdminGold,
                AdminGoldSoft,
            )
        }
        adminWhatsappUrl(customer.phone, "Hola${customer.fullName.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()}, te escribimos de Zeta Dorada.")?.let { url ->
            OutlinedButton(onClick = { context.openWhatsapp(url) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Outlined.Chat, null, Modifier.padding(end = 8.dp).size(18.dp))
                Text("Escribir por WhatsApp")
            }
        }
        TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text(if (expanded) "Ocultar acceso y actividad" else "Ver acceso y actividad")
            Icon(if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null)
        }
        AnimatedVisibility(expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                AdminDetail("Inicia sesión con", customer.providers.joinToString { formatProvider(it) }.ifBlank { "Sin método registrado" })
                AdminDetail("Correo", if (customer.email.isBlank()) "" else customer.email + if (customer.emailConfirmed) " · verificado" else " · sin verificar")
                AdminDetail("Cliente desde", adminDate(customer.createdAt))
                AdminDetail("Último ingreso", adminDate(customer.lastSignInAt))
                AdminDetail("Último pedido", adminDate(customer.lastOrderAt).ifBlank { "Sin pedidos" })
                AdminDetail("Direcciones guardadas", customer.addressesCount.toString())
                AdminDetail("Cuenta", customer.id)
            }
        }
        if (busy) {
            AdminLoading()
        } else {
            Box(Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { menu = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Settings, null, Modifier.padding(end = 8.dp))
                    Text("Gestionar cuenta")
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    listOf(
                        CustomerAction.ResetCart to "Vaciar carrito y guardados",
                        CustomerAction.ResetAccount to "Reiniciar cuenta",
                        (if (customer.isAdmin) CustomerAction.RemoveAdmin else CustomerAction.MakeAdmin) to (if (customer.isAdmin) "Quitar admin" else "Hacer admin"),
                        CustomerAction.Delete to "Eliminar cuenta",
                    ).forEach { (action, label) ->
                        if (action == CustomerAction.Delete) HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text(label, color = if (action == CustomerAction.Delete) AdminRed else Color.Unspecified) },
                            onClick = {
                                menu = false
                                onAction(action)
                            },
                        )
                    }
                }
            }
        }
    }
}

private val AdminCustomerRecord.displayName: String
    get() = fullName.ifBlank { email.substringBefore("@").ifBlank { "esta cuenta" } }

private fun formatProvider(provider: String): String = when (provider) {
    "google" -> "Google"
    "email" -> "Correo y contraseña"
    "facebook" -> "Facebook"
    "phone" -> "SMS"
    else -> provider
}

// ---------- Notificaciones ----------

@Composable
private fun NotificationsSection(
    client: SupabaseClient,
    session: SupabaseSession,
    reloadKey: Int,
    notifications: List<AdminNotificationRecord>,
    bottomPadding: Dp,
    onMarkAllRead: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var preferences by remember { mutableStateOf<AdminNotificationPreferences?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var localReload by remember { mutableIntStateOf(0) }
    var saving by remember { mutableStateOf(false) }
    var marking by remember { mutableStateOf(false) }

    LaunchedEffect(reloadKey, localReload) {
        loadError = null
        try {
            preferences = client.fetchNotificationPreferences(session)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            loadError = e.message ?: "No se pudieron cargar tus preferencias."
        }
    }

    fun save(next: AdminNotificationPreferences) {
        val previous = preferences
        preferences = next
        saving = true
        scope.launch {
            runCatching { client.saveNotificationPreferences(session, next) }
                .onFailure {
                    if (it is CancellationException) throw it
                    preferences = previous
                    Toast.makeText(context, it.message ?: "No se pudo guardar.", Toast.LENGTH_LONG).show()
                }
            saving = false
        }
    }

    val unread = notifications.count { !it.isRead }
    AdminList(bottomPadding) {
        item { AdminSectionHeading("Notificaciones", "Elige qué avisos recibes. Se aplican a todos los dispositivos con tu cuenta, incluso con la app cerrada.") }
        val prefs = preferences
        when {
            loadError != null -> item { AdminMessage(loadError.orEmpty(), isError = true) { localReload++ } }
            prefs == null -> item { AdminLoading() }
            else -> item {
                AdminCard {
                    AdminToggle("Recibir avisos", "Pausa o reanuda todos los avisos del panel.", prefs.enabled, !saving) { save(prefs.copy(enabled = it)) }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    val enabled = prefs.enabled && !saving
                    AdminToggle("Pedidos nuevos", "Cuando un cliente confirma un pedido.", prefs.newOrders, enabled) { save(prefs.copy(newOrders = it)) }
                    AdminToggle("Clientes nuevos", "Cuando alguien crea su cuenta en Zeta Dorada.", prefs.newCustomers, enabled) { save(prefs.copy(newCustomers = it)) }
                    AdminToggle("Stock bajo", "Cuando un producto activo llega al mínimo que definas en Tienda.", prefs.lowStock, enabled) { save(prefs.copy(lowStock = it)) }
                }
            }
        }
        item { AdminSectionHeading("Actividad reciente", "Los últimos avisos de tu cuenta.", notifications.size) }
        if (unread > 0) {
            item {
                OutlinedButton(
                    onClick = {
                        marking = true
                        scope.launch {
                            runCatching { onMarkAllRead() }
                                .onFailure { if (it is CancellationException) throw it; Toast.makeText(context, it.message ?: "No se pudo actualizar.", Toast.LENGTH_LONG).show() }
                            marking = false
                        }
                    },
                    enabled = !marking,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (marking) "Marcando…" else "Marcar $unread como leídos") }
            }
        }
        if (notifications.isEmpty()) {
            item { AdminMessage("Todavía no hay avisos.") }
        } else {
            items(notifications, key = { it.id }) { notification -> AdminNotificationRow(notification) }
        }
    }
}

@Composable
private fun AdminNotificationRow(notification: AdminNotificationRecord) {
    val icon = when (notification.kind) {
        "customer" -> Icons.Outlined.PersonAdd
        "stock" -> Icons.Outlined.Inventory2
        else -> Icons.AutoMirrored.Outlined.ReceiptLong
    }
    AdminCard {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(36.dp).clip(CircleShape).background(if (notification.isRead) BosqueSurfaceSoft else BosqueSurfaceStrong),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = if (notification.isRead) BosqueMuted else BosqueGreenDeep, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(notification.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    if (!notification.isRead) Box(Modifier.size(8.dp).clip(CircleShape).background(AdminGold))
                }
                Text(notification.body, style = MaterialTheme.typography.bodySmall)
                Text(adminDate(notification.createdAt), style = MaterialTheme.typography.labelSmall, color = BosqueMuted)
            }
        }
    }
}

@Composable
private fun AdminToggle(title: String, description: String, checked: Boolean, enabled: Boolean, onChecked: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = if (enabled) BosqueInk else BosqueMuted)
            Text(description, style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
        }
        Switch(checked = checked, onCheckedChange = onChecked, enabled = enabled)
    }
}

// ---------- Tienda ----------

@Composable
private fun StoreSection(
    client: SupabaseClient,
    session: SupabaseSession,
    reloadKey: Int,
    bottomPadding: Dp,
    onSaved: (StoreSettingsRecord) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var loaded by remember { mutableStateOf<StoreSettingsRecord?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var localReload by remember { mutableIntStateOf(0) }
    var whatsapp by remember { mutableStateOf("") }
    var rotation by remember { mutableIntStateOf(4) }
    var lowStock by remember { mutableIntStateOf(3) }
    var saving by remember { mutableStateOf(false) }

    LaunchedEffect(reloadKey, localReload) {
        loadError = null
        try {
            val settings = client.fetchStoreSettings() ?: throw IllegalStateException("La tienda no tiene ajustes guardados.")
            loaded = settings
            whatsapp = settings.whatsappNumber.removePrefix("51")
            rotation = settings.adRotationSeconds
            lowStock = settings.lowStockThreshold
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            loadError = e.message ?: "No se pudieron cargar los ajustes."
        }
    }

    val whatsappDigits = whatsapp.filter(Char::isDigit)
    val whatsappValid = whatsappDigits.length == 9 && whatsappDigits.startsWith("9")
    val current = StoreSettingsRecord("51$whatsappDigits", rotation, lowStock)
    val changed = loaded != null && current != loaded

    AdminList(bottomPadding) {
        item { AdminSectionHeading("Tienda", "Ajustes compartidos por la app y la web. Los cambios se ven al volver a abrir la tienda.") }
        when {
            loadError != null -> item { AdminMessage(loadError.orEmpty(), isError = true) { localReload++ } }
            loaded == null -> item { AdminLoading() }
            else -> {
                item {
                    AdminCard {
                        Text("WhatsApp de pedidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("A este número llegan los pedidos y las consultas de pago.", style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it.filter(Char::isDigit).take(9) },
                            prefix = { Text("+51 ") },
                            label = { Text("Celular") },
                            singleLine = true,
                            isError = whatsapp.isNotEmpty() && !whatsappValid,
                            supportingText = { if (whatsapp.isNotEmpty() && !whatsappValid) Text("Debe ser un celular de 9 dígitos que empiece con 9.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                item {
                    AdminCard {
                        Text("Carrusel de anuncios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Cada banner se muestra $rotation segundos.", style = MaterialTheme.typography.bodySmall, color = BosqueMuted, modifier = Modifier.weight(1f))
                            AdminStepper(rotation, { rotation = it.coerceIn(2, 60) })
                        }
                    }
                }
                item {
                    AdminCard {
                        Text("Aviso de stock bajo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (lowStock == 0) "Solo avisa cuando un producto se agota." else "Avisa cuando quedan $lowStock unidades o menos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BosqueMuted,
                                modifier = Modifier.weight(1f),
                            )
                            AdminStepper(lowStock, { lowStock = it.coerceIn(0, 1000) })
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            saving = true
                            scope.launch {
                                runCatching { client.updateStoreSettings(session, current) }
                                    .onSuccess {
                                        loaded = current
                                        onSaved(current)
                                        Toast.makeText(context, "Ajustes guardados", Toast.LENGTH_SHORT).show()
                                    }
                                    .onFailure {
                                        if (it is CancellationException) throw it
                                        Toast.makeText(context, it.message ?: "No se pudo guardar.", Toast.LENGTH_LONG).show()
                                    }
                                saving = false
                            }
                        },
                        enabled = changed && whatsappValid && !saving,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(if (saving) "Guardando…" else "Guardar cambios") }
                }
            }
        }
    }
}

// ---------- Componentes comunes (mismo patrón que AdminDesign.kt de Intu) ----------

@Composable
private fun AdminList(bottomPadding: Dp, content: LazyListScope.() -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
private fun AdminSectionHeading(title: String, description: String, count: Int? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (count != null) AdminBadge(count.toString())
        }
        Text(description, style = MaterialTheme.typography.bodySmall, color = BosqueMuted)
    }
}

@Composable
private fun AdminSearchField(query: String, onQuery: (String) -> Unit, label: String) {
    OutlinedTextField(
        query,
        onQuery,
        label = { Text(label) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Search, null) },
        trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { onQuery("") }) { Icon(Icons.Outlined.Close, "Borrar búsqueda") } },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
    )
}

@Composable
private fun AdminFilters(options: List<Pair<String?, String>>, selected: String?, onSelect: (String?) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelect(value) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BosqueGreenDeep, selectedLabelColor = Color.White),
                shape = RoundedCornerShape(10.dp),
            )
        }
    }
}

@Composable
private fun AdminCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun AdminBadge(label: String, color: Color = BosqueGreenDeep, background: Color = BosqueSurfaceStrong) {
    Text(
        label,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(background, RoundedCornerShape(8.dp)).padding(horizontal = 9.dp, vertical = 5.dp),
    )
}

@Composable
private fun AdminDetail(label: String, value: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = BosqueMuted)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AdminStat(label: String, value: String, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(12.dp), color = BosqueSurfaceSoft) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(label, style = MaterialTheme.typography.labelSmall, color = BosqueMuted)
        }
    }
}

@Composable
private fun AdminStepper(value: Int, onValue: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValue(value - 1) }) { Icon(Icons.Outlined.Remove, "Menos") }
        Text(value.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        IconButton(onClick = { onValue(value + 1) }) { Icon(Icons.Outlined.Add, "Más") }
    }
}

@Composable
private fun AdminLoading() {
    Column(
        Modifier.fillMaxWidth().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircularProgressIndicator(Modifier.size(28.dp), color = BosqueGreen, strokeWidth = 3.dp)
        Text("Cargando…", color = BosqueMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AdminMessage(text: String, isError: Boolean = false, onRetry: (() -> Unit)? = null) {
    AdminCard {
        Icon(if (isError) Icons.Outlined.CloudOff else Icons.Outlined.Inbox, null, tint = if (isError) AdminRed else BosqueGreen, modifier = Modifier.size(28.dp))
        Text(if (isError) "No se pudo completar" else "Todo al día", style = MaterialTheme.typography.titleMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = BosqueMuted)
        if (onRetry != null) TextButton(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun AdminConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    destructive: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    requireAcknowledgement: String? = null,
) {
    var acknowledged by remember { mutableStateOf(requireAcknowledgement == null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(message)
                if (requireAcknowledgement != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = acknowledged, onCheckedChange = { acknowledged = it })
                        Text(requireAcknowledgement, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = acknowledged) {
                Text(confirmLabel, color = if (!acknowledged) BosqueMuted else if (destructive) AdminRed else BosqueGreen)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Volver") } },
        containerColor = Color.White,
    )
}

// ---------- Utilidades ----------

private val OrderRecord.code: String
    get() = id.take(8).uppercase()

private fun adminNormalize(text: String): String =
    Normalizer.normalize(text, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "").lowercase(Locale.ROOT)

/** wa.me para un celular peruano guardado como 9XXXXXXXX, +51 9XX XXX XXX o 519XXXXXXXX. */
private fun adminWhatsappUrl(phone: String, text: String): String? {
    val digits = phone.filter(Char::isDigit)
    val international = when {
        digits.length == 9 && digits.startsWith("9") -> "51$digits"
        digits.length == 11 && digits.startsWith("519") -> digits
        else -> return null
    }
    return "https://wa.me/$international?text=${java.net.URLEncoder.encode(text, "UTF-8").replace("+", "%20")}"
}

/** Fechas de Supabase (UTC, "2026-10-09T17:48:55.56+00:00") en hora de Lima. */
private fun adminDate(timestamp: String): String {
    if (timestamp.length < 19) return ""
    return runCatching {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }
        val formatter = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("es", "PE")).apply { timeZone = TimeZone.getTimeZone("America/Lima") }
        formatter.format(parser.parse(timestamp.take(19).replace(' ', 'T'))!!)
    }.getOrDefault("")
}

private fun formatAddressSnapshot(snapshot: String): String = runCatching {
    val json = JSONObject(snapshot)
    listOf("line1", "line2", "district", "city")
        .map { json.optString(it).trim() }
        .filter { it.isNotBlank() && it != "null" }
        .joinToString(", ") +
        json.optString("reference").trim().takeIf { it.isNotBlank() && it != "null" }?.let { "\nReferencia: $it" }.orEmpty()
}.getOrDefault("")
