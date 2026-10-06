package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@Composable
fun KasirPosScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val storeName by viewModel.storeName.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var showQuickManualModal by remember { mutableStateOf(false) }
    var showKasbonDialog by remember { mutableStateOf(false) }
    var showBarcodeScannerSheet by remember { mutableStateOf(false) }

    val totalBelanja = cartItems.sumOf { it.subtotal }
    val totalQty = cartItems.sumOf { Math.ceil(it.qty).toInt() }
    val jenisCount = cartItems.size

    val filteredCatalog = remember(searchQuery, selectedCategory, viewModel.posCatalog) {
        viewModel.posCatalog.filter { item ->
            val matchesCat = selectedCategory == "Semua" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.barcode.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header (Fixed)
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(68.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Storefront,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = storeName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainer)
                            )
                            Text(
                                text = "Kasir Buka (Offline Siap)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CartHeaderBadgeButton(
                            itemCount = jenisCount,
                            totalAmount = totalBelanja,
                            onClick = { viewModel.navigateTo(ScreenRoute.CartDetail) }
                        )
                        ProfileAvatarButton(
                            onClick = { viewModel.navigateTo(ScreenRoute.Settings) }
                        )
                    }
                }
            }

            // Main Scrollable Content + Sticky Bottom Checkout
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 186.dp)
                ) {
                    // 0. Top Quick Notification / Jam Operasional Info
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SurfaceContainerLow)
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Verified,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Harga Pasar Sembako Terupdate",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Wifi,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Sinkron Cloud",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 1. Search & Barcode Quick Field
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceContainerLowest,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = OutlineColor,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .size(22.dp)
                                )
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Ketik barang / barcode (cth: Indomie)...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = OutlineColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("kasir_search_input")
                                    )
                                }
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchQuery = "" },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Hapus Pencarian",
                                            tint = OutlineColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        showBarcodeScannerSheet = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                    modifier = Modifier
                                        .height(44.dp)
                                        .testTag("btn_scan_barcode")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.QrCodeScanner,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Scan",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Filter Kategori Cepat (Horizontal Scrollable Large Pills)
                    item {
                        val categories = listOf(
                            Pair("Semua", Icons.Outlined.Dashboard),
                            Pair("Sembako & Eceran", Icons.Outlined.Grain),
                            Pair("Mie & Makanan", Icons.Outlined.RamenDining),
                            Pair("Minuman Dingin", Icons.Outlined.AcUnit),
                            Pair("Rokok", Icons.Outlined.SmokingRooms),
                            Pair("Gas & Galon", Icons.Outlined.PropaneTank),
                            Pair("Sabun & Bumbu", Icons.Outlined.CleaningServices)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { (catName, icon) ->
                                val isSelected = selectedCategory == catName
                                Surface(
                                    onClick = {
                                        selectedCategory = catName
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = if (isSelected) Primary else SurfaceContainerLowest,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.height(42.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else OnSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = catName,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) Color.White else OnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Action Card Banner Prominen (Kelontong Fast-Track)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Manual Cash / Timbangan Input Banner
                            Surface(
                                onClick = { viewModel.navigateTo(ScreenRoute.ManualInput(initialTab = 0)) },
                                shape = RoundedCornerShape(14.dp),
                                color = SecondaryContainer,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("banner_manual_input")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(SurfaceContainerLowest.copy(alpha = 0.85f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Scale,
                                                contentDescription = null,
                                                tint = Secondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "+ Input Manual / Eceran Kilat",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = OnSecondaryContainer
                                            )
                                            Text(
                                                text = "Ketik nominal rupiah langsung / beras timbangan bebas",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = OnSecondaryContainer.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Buka Input Manual",
                                        tint = OnSecondaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Quick Gas, Galon & Hitung Modal Shortcut Row
                            QuickShortcutThreeColumns(
                                onGasClick = {
                                    viewModel.addToCart("Tukar Tabung Gas LPG 3kg", 21000L, 1.0, "tbg", "Gas LPG", WarungImages.GAS_LPG_2)
                                },
                                onGalonClick = {
                                    viewModel.addToCart("Isi Ulang Galon Aqua", 20000L, 1.0, "gln", "Galon Air", WarungImages.GALON_AQUA_2)
                                },
                                onHitungModalClick = {
                                    viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = false))
                                }
                            )
                        }
                    }

                    // 4. Menu Grid Barang Terlaris (Selectable Product Cards with Images, Prices & Instant Cart Badge)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (selectedCategory == "Semua") "Pilih Barang Cepat" else "Katalog: $selectedCategory",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Surface(
                                    onClick = { showBarcodeScannerSheet = true },
                                    shape = RoundedCornerShape(50),
                                    color = PrimaryFixed
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.QrCodeScanner,
                                            contentDescription = null,
                                            tint = OnPrimaryFixed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Scan Cepat",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = OnPrimaryFixed
                                        )
                                    }
                                }
                            }

                            if (filteredCatalog.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceContainerLow,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = OutlineColor)
                                        Text(
                                            text = "Barang \"$searchQuery\" tidak ditemukan di katalog",
                                            style = MaterialTheme.typography.labelLarge,
                                            textAlign = TextAlign.Center
                                        )
                                        TextButton(onClick = { showQuickManualModal = true }) {
                                            Text("+ Tambah Manual \"$searchQuery\" ke Keranjang")
                                        }
                                    }
                                }
                            } else {
                                // 2-Column Grid of Selectable Product Cards
                                val rows = filteredCatalog.chunked(2)
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    rows.forEach { rowItems ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            rowItems.forEach { catalogItem ->
                                                val inCartQty = cartItems
                                                    .filter { it.name.equals(catalogItem.name, ignoreCase = true) }
                                                    .sumOf { it.qty }
                                                SelectableProductGridCard(
                                                    item = catalogItem,
                                                    qtyInCart = inCartQty,
                                                    onSelect = {
                                                        viewModel.scanOrSelectCatalogItem(catalogItem, 1.0)
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            if (rowItems.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. Keranjang Belanja Aktif (Persistent & Detailed Desk)
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceContainerLowest,
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.navigateTo(ScreenRoute.CartDetail) },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ShoppingCart,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Keranjang Belanja ($jenisCount Jenis, $totalQty Qty)",
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Ketuk untuk buka rincian diskon & catatan ➔",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = Primary
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = { viewModel.clearCart() },
                                        shape = RoundedCornerShape(8.dp),
                                        color = ErrorContainer,
                                        modifier = Modifier.testTag("btn_clear_cart")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.DeleteSweep,
                                                contentDescription = null,
                                                tint = OnErrorContainer,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "Kosongkan",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = OnErrorContainer
                                            )
                                        }
                                    }
                                }

                                if (cartItems.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceContainerLow),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ShoppingBasket,
                                                contentDescription = null,
                                                tint = OutlineColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Keranjang Kosong",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Scan barcode atau pilih produk di atas untuk menghitung total",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OutlineColor,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        cartItems.forEach { item ->
                                            CartDeskRow(
                                                item = item,
                                                onDecrement = { viewModel.updateCartItemQty(item.id, -1.0) },
                                                onIncrement = { viewModel.updateCartItemQty(item.id, 1.0) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Sticky Bottom Checkout Control & Cash Denominations
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = SurfaceContainerLowest.copy(alpha = 0.98f),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Fast Denomination Pills
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Preset Tunai:",
                                style = MaterialTheme.typography.labelSmall,
                                color = OutlineColor
                            )
                            Surface(
                                onClick = {
                                    val exactAmount = if (totalBelanja > 0L) totalBelanja else 64000L
                                    viewModel.showToast("Dibayar Uang Pas: ${formatRupiah(exactAmount)}")
                                    viewModel.openPaymentWithPreset(exactAmount)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerHigh
                            ) {
                                Text(
                                    text = "Uang Pas (${formatNumberOnly(if (totalBelanja > 0L) totalBelanja else 64000L)})",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                            Surface(
                                onClick = {
                                    viewModel.showToast("Uang diterima Rp 70.000")
                                    viewModel.openPaymentWithPreset(70000L)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerLow
                            ) {
                                Text(
                                    text = "Rp 70.000",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                            Surface(
                                onClick = {
                                    viewModel.showToast("Uang diterima Rp 100.000")
                                    viewModel.openPaymentWithPreset(100000L)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerLow
                            ) {
                                Text(
                                    text = "Rp 100.000",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }

                        // Total Display & Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.clickable { viewModel.navigateTo(ScreenRoute.CartDetail) }
                            ) {
                                Text(
                                    text = "TOTAL BELANJA ($jenisCount JENIS • $totalQty ITEM)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    ),
                                    color = OutlineColor
                                )
                                Text(
                                    text = formatRupiah(totalBelanja),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = Primary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerLow)
                                    .clickable { showQuickManualModal = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddCircleOutline,
                                    contentDescription = null,
                                    tint = Secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "+ Ketik Rp",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        // Giant Action Buttons Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Catat Kasbon Button
                            Surface(
                                onClick = { showKasbonDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                color = Secondary,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("btn_catat_kasbon")
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Catat Kasbon",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            // Giant Primary Pay CTA
                            Button(
                                onClick = { viewModel.navigateTo(ScreenRoute.PaymentCheckout) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(2f)
                                    .height(54.dp)
                                    .testTag("btn_bayar_sekarang")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PointOfSale,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "BAYAR SEKARANG ➔",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Interactive POS Barcode Scanner Sheet for Checkout
        if (showBarcodeScannerSheet) {
            PosCheckoutBarcodeScannerSheet(
                catalog = viewModel.posCatalog,
                cartItems = cartItems,
                totalBelanja = totalBelanja,
                onScanAdd = { item ->
                    viewModel.scanOrSelectCatalogItem(item, 1.0)
                },
                onOpenWholesaleScanner = {
                    showBarcodeScannerSheet = false
                    viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = true))
                },
                onProceedCheckout = {
                    showBarcodeScannerSheet = false
                    viewModel.navigateTo(ScreenRoute.PaymentCheckout)
                },
                onDismiss = { showBarcodeScannerSheet = false }
            )
        }
    }

    // Modal Manual / Eceran Kilat
    if (showQuickManualModal) {
        QuickManualModalDialog(
            initialName = searchQuery,
            onDismiss = { showQuickManualModal = false },
            onSubmit = { name, price ->
                viewModel.addToCart(name, price, 1.0, "pcs", "Eceran")
                searchQuery = ""
                showQuickManualModal = false
            }
        )
    }

    // Quick Kasbon Dialog
    if (showKasbonDialog) {
        var customerName by remember { mutableStateOf("Bu RT Siti") }
        AlertDialog(
            onDismissRequest = { showKasbonDialog = false },
            title = {
                Text("Catat Kasbon Pelanggan", style = MaterialTheme.typography.headlineSmall)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Masukkan nama tetangga untuk mencatat bon sebesar ${formatRupiah(totalBelanja)}:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Nama Pelanggan / Catatan Bon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.completeCheckout(PaymentMethod.KASBON, 0L, customerName)
                        showKasbonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Simpan Bon")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKasbonDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SelectableProductGridCard(
    item: PosCatalogItem,
    qtyInCart: Double,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInCart = qtyInCart > 0.0
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLowest,
        shadowElevation = if (isInCart) 3.dp else 1.dp,
        modifier = modifier
            .border(
                width = if (isInCart) 1.5.dp else 1.dp,
                color = if (isInCart) Primary else OutlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("product_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLow)
                ) {
                    NetworkProductImage(
                        url = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (isInCart) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(3.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = qtyInCart.toInt().toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.stockLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = OnSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatRupiah(item.price),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Primary
                    )
                    Text(
                        text = "/ ${item.unit}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = OutlineColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isInCart) Primary else PrimaryFixed
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = if (isInCart) Color.White else OnPrimaryFixed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isInCart) "Tambah" else "Pilih",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isInCart) Color.White else OnPrimaryFixed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PosCheckoutBarcodeScannerSheet(
    catalog: List<PosCatalogItem>,
    cartItems: List<CartItem>,
    totalBelanja: Long,
    onScanAdd: (PosCatalogItem) -> Unit,
    onOpenWholesaleScanner: () -> Unit,
    onProceedCheckout: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedScannedItem by remember { mutableStateOf(catalog.first()) }
    var manualBarcodeQuery by remember { mutableStateOf("") }
    var flashOn by remember { mutableStateOf(false) }

    val matchingBarcodeItems = remember(manualBarcodeQuery, catalog) {
        if (manualBarcodeQuery.isBlank()) catalog
        else catalog.filter {
            it.name.contains(manualBarcodeQuery, ignoreCase = true) ||
                it.barcode.contains(manualBarcodeQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InverseSurface.copy(alpha = 0.72f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = SurfaceContainerLowest,
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .clickable(enabled = false) {}
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(OutlineVariant)
                    )
                }

                // Modal Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = OnPrimaryFixed)
                        }
                        Column {
                            Text("Scan Barcode Kasir", style = MaterialTheme.typography.headlineSmall)
                            Text("Arahkan kamera atau pilih barang untuk hitung total", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLow)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup Scanner")
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Camera Viewfinder Simulation
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(185.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(InverseSurface)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.Black.copy(alpha = 0.45f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryFixed)
                                        )
                                        Text(
                                            text = "PEMINDAI BARCODE KASIR AKTIF",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnPrimaryContainer
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        onClick = onOpenWholesaleScanner,
                                        shape = RoundedCornerShape(50),
                                        color = Color.Black.copy(alpha = 0.45f)
                                    ) {
                                        Text(
                                            text = "Mode Kulakan",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { flashOn = !flashOn },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (flashOn) SecondaryContainer else Color.Black.copy(alpha = 0.4f))
                                    ) {
                                        Icon(
                                            Icons.Outlined.FlashOn,
                                            contentDescription = "Senter",
                                            tint = if (flashOn) OnSecondaryContainer else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Center Reticle
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(width = 210.dp, height = 78.dp)
                                    .border(2.dp, PrimaryFixed, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .height(2.dp)
                                            .background(PrimaryFixed)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = selectedScannedItem.barcode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }

                            // Scanned Item Instant Add Bar
                            Surface(
                                onClick = { onScanAdd(selectedScannedItem) },
                                shape = RoundedCornerShape(12.dp),
                                color = Primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .testTag("btn_confirm_scanned_item")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = PrimaryFixed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "TERDETEKSI • ${formatRupiah(selectedScannedItem.price)}/${selectedScannedItem.unit}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PrimaryFixed
                                            )
                                            Text(
                                                text = selectedScannedItem.name,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    Surface(shape = RoundedCornerShape(8.dp), color = SecondaryContainer) {
                                        Text(
                                            text = "+1 Scan Masuk",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = OnSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Search / Manual Barcode Input
                    item {
                        OutlinedTextField(
                            value = manualBarcodeQuery,
                            onValueChange = { manualBarcodeQuery = it },
                            label = { Text("Cari nama produk atau ketik kode barcode...") },
                            leadingIcon = { Icon(Icons.Outlined.QrCode, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Quick Scannable Items List
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Simulasi Scan / Pilih Cepat Barang:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "Ketuk untuk tambah",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    items(matchingBarcodeItems.size) { index ->
                        val item = matchingBarcodeItems[index]
                        val inCartCount = cartItems
                            .filter { it.name.equals(item.name, ignoreCase = true) }
                            .sumOf { it.qty.toInt() }
                        Surface(
                            onClick = {
                                selectedScannedItem = item
                                onScanAdd(item)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedScannedItem.id == item.id) PrimaryFixed.copy(alpha = 0.35f) else SurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerLowest)
                                    ) {
                                        NetworkProductImage(
                                            url = item.imageUrl,
                                            contentDescription = item.name,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.headlineSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "EAN: ${item.barcode} • ${formatRupiah(item.price)}/${item.unit}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (inCartCount > 0) Primary else SurfaceContainerLowest
                                ) {
                                    Text(
                                        text = if (inCartCount > 0) "${inCartCount}x di Keranjang (+1)" else "+ Scan Masuk",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (inCartCount > 0) Color.White else Primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Live Total & Checkout Action inside Scanner Sheet
                Surface(
                    color = SurfaceContainerLowest,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL SEMENTARA (${cartItems.sumOf { it.qty.toInt() }} PCS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = formatRupiah(totalBelanja),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Primary
                            )
                        }
                        Button(
                            onClick = onProceedCheckout,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Lanjut Bayar ➔",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickShortcutThreeColumns(
    onGasClick: () -> Unit,
    onGalonClick: () -> Unit,
    onHitungModalClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Tile 1: + Gas LPG 3Kg
        Surface(
            onClick = onGasClick,
            shape = RoundedCornerShape(12.dp),
            color = SurfaceContainerLow,
            shadowElevation = 1.dp,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ErrorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PropaneTank,
                        contentDescription = null,
                        tint = OnErrorContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "+ Gas LPG...",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Rp 21.000",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary
                    )
                }
            }
        }

        // Tile 2: + Galon Aqua
        Surface(
            onClick = onGalonClick,
            shape = RoundedCornerShape(12.dp),
            color = SurfaceContainerLow,
            shadowElevation = 1.dp,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TertiaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        tint = OnTertiaryFixed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "+ Galon ...",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Rp 20.000",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary
                    )
                }
            }
        }

        // Tile 3: Hitung Modal / Kalkulator Margin
        Surface(
            onClick = onHitungModalClick,
            shape = RoundedCornerShape(12.dp),
            color = SurfaceContainerHigh,
            shadowElevation = 1.dp,
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Calculate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hitung M...",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Kalkulator Margin",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 12.sp
                        ),
                        color = OnSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun CartDeskRow(
    item: CartItem,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val qtyStr = if (item.qty % 1.0 == 0.0) item.qty.toInt().toString() else item.qty.toString()
            Text(
                text = "$qtyStr ${item.unit} × ${formatRupiah(item.price)}",
                style = MaterialTheme.typography.bodySmall,
                color = OutlineColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatRupiah(item.subtotal),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Primary
            )
        }

        // Stepper
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerLowest)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh)
                    .clickable(onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            val qtyDisplay = if (item.qty % 1.0 == 0.0) item.qty.toInt().toString() else item.qty.toString()
            Text(
                text = qtyDisplay,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.widthIn(min = 28.dp),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Primary)
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuickManualModalDialog(
    initialName: String = "",
    onDismiss: () -> Unit,
    onSubmit: (String, Long) -> Unit
) {
    var itemName by remember { mutableStateOf(initialName) }
    var itemPriceText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerLowest,
            shadowElevation = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Balance,
                            contentDescription = null,
                            tint = Secondary
                        )
                        Text(
                            text = "Input Eceran / Ketik Nominal",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Nama Barang / Transaksi") },
                    placeholder = { Text("Cth: Cabai Rawit / Beras 1.5kg") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = itemPriceText,
                    onValueChange = { itemPriceText = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Harga (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(2000L, 5000L, 10000L, 25000L).forEach { preset ->
                        Surface(
                            onClick = { itemPriceText = preset.toString() },
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceContainerHigh
                        ) {
                            Text(
                                text = formatRupiah(preset),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val price = itemPriceText.toLongOrNull() ?: 0L
                        if (price > 0) {
                            onSubmit(itemName.ifBlank { "Barang Eceran" }, price)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Outlined.AddCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Masukkan ke Keranjang", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}
