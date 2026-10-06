package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
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

    val totalBelanja = cartItems.sumOf { it.subtotal }
    val totalQty = cartItems.sumOf { Math.ceil(it.qty).toInt() }
    val jenisCount = cartItems.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
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
                contentPadding = PaddingValues(bottom = 176.dp)
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
                            Button(
                                onClick = {
                                    viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = true))
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
                                    text = "Barcode",
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
                                    if (catName == "Gas & Galon") {
                                        viewModel.navigateTo(ScreenRoute.ManualInput(initialTab = 2))
                                    } else {
                                        viewModel.showToast("Kategori: $catName")
                                    }
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

                // 4. Menu Grid Barang Terlaris
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
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
                                    text = "Paling Sering Dibeli",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Sentuh untuk tambah",
                                style = MaterialTheme.typography.labelSmall,
                                color = OutlineColor
                            )
                        }

                        QuickShortcutThreeColumns(
                            onGasClick = {
                                viewModel.navigateTo(ScreenRoute.ManualInput(initialTab = 2))
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
                                    Text(
                                        text = "Keranjang Belanja ($jenisCount Jenis, $totalQty Qty)",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
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

                            val filteredCart = if (searchQuery.isBlank()) {
                                cartItems
                            } else {
                                cartItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            }

                            if (filteredCart.isEmpty()) {
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
                                        text = "Pilih produk di atas atau ketuk + Input Manual",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OutlineColor
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    filteredCart.forEach { item ->
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
                                viewModel.showToast("Dibayar Uang Pas: ${formatRupiah(totalBelanja)}")
                                viewModel.navigateTo(ScreenRoute.PaymentCheckout)
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceContainerHigh
                        ) {
                            Text(
                                text = "Uang Pas (64.000)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        Surface(
                            onClick = {
                                viewModel.showToast("Uang diterima Rp 70.000")
                                viewModel.navigateTo(ScreenRoute.PaymentCheckout)
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
                                viewModel.navigateTo(ScreenRoute.PaymentCheckout)
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
                        Column {
                            Text(
                                text = "TOTAL BELANJA ($totalQty ITEM)",
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
                                imageVector = Icons.Outlined.Payments,
                                contentDescription = null,
                                tint = Secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Tunai / QRIS",
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

    // Modal Manual / Eceran Kilat
    if (showQuickManualModal) {
        QuickManualModalDialog(
            onDismiss = { showQuickManualModal = false },
            onSubmit = { name, price ->
                viewModel.addToCart(name, price, 1.0, "pcs", "Eceran")
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
    onDismiss: () -> Unit,
    onSubmit: (String, Long) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
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
