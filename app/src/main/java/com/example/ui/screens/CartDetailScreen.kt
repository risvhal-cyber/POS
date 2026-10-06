package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@Composable
fun CartDetailScreen(
    viewModel: WarungViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val cartItems by viewModel.cartItems.collectAsState()
    val deliveryNote by viewModel.deliveryNote.collectAsState()
    val discountAmount by viewModel.discountAmount.collectAsState()
    var showPosScannerSheet by remember { mutableStateOf(false) }

    val displayItems = remember(cartItems) { cartItems }

    val rawTotal = displayItems.sumOf { it.subtotal }
    val activeDiscount = if (rawTotal > 0L) discountAmount else 0L
    val cleanTotal = (rawTotal - activeDiscount).coerceAtLeast(0L)
    val totalItemTypes = displayItems.size
    val totalPcs = displayItems.sumOf { Math.ceil(it.qty).toInt() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                        AsyncImage(
                            model = WarungImages.LOGO,
                            contentDescription = "Warung POS Logo",
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = "Keranjang\nBelanja",
                            style = MaterialTheme.typography.headlineSmall.copy(lineHeight = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PrimaryContainer
                        ) {
                            Text(
                                text = "$totalItemTypes\nItem",
                                style = MaterialTheme.typography.labelSmall.copy(lineHeight = 11.sp),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = "Kosongkan Keranjang",
                                tint = ErrorColor
                            )
                        }
                        ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 170.dp)
            ) {
                // Status Transaksi & Quick Action Chips
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "#TRX-20250524-0042",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryContainer)
                                        )
                                        Text(
                                            text = "Langganan: Ibu RT Rohanah",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SurfaceContainerHighest
                                ) {
                                    Text(
                                        text = "Kasir Utama",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { showPosScannerSheet = true },
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryFixed
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = OnPrimaryFixed, modifier = Modifier.size(18.dp))
                                    Text("+ Scan / Pilih Barang", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnPrimaryFixed)
                                }
                            }
                            Surface(
                                onClick = { viewModel.navigateTo(ScreenRoute.ManualInput(initialTab = 1)) },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerHigh
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = OnSurface, modifier = Modifier.size(18.dp))
                                    Text("+ Manual Rp", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                                }
                            }
                            Surface(
                                onClick = { viewModel.toggleDiscount() },
                                shape = RoundedCornerShape(10.dp),
                                color = if (activeDiscount > 0L) PrimaryFixed else SurfaceContainerHigh
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Outlined.Loyalty, contentDescription = null, tint = if (activeDiscount > 0L) OnPrimaryFixed else OnSurface, modifier = Modifier.size(18.dp))
                                    Text("Kupon Diskon", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = if (activeDiscount > 0L) OnPrimaryFixed else OnSurface)
                                }
                            }
                        }
                    }
                }

                // Section Title
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Barang Belanja",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SurfaceContainerHighest
                            ) {
                                Text(
                                    text = "$totalItemTypes Item ($totalPcs Pcs)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = "Geser untuk hapus",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Detailed Item Cards
                items(displayItems.size) { index ->
                    val item = displayItems[index]
                    DetailedCartItemCard(
                        item = item,
                        onDecrement = { viewModel.updateCartItemQty(item.id, -1.0) },
                        onIncrement = { viewModel.updateCartItemQty(item.id, 1.0) },
                        onRemove = { viewModel.removeCartItem(item.id) },
                        onNoteClick = { viewModel.showToast("Catatan item: ${item.note ?: "Standar toko"}") },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Catatan Pengantaran / Toko
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                    Text("Catatan Pengantaran / Toko", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Text("Opsional", style = MaterialTheme.typography.labelSmall, color = Primary)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.EditNote, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                                BasicTextField(
                                    value = deliveryNote,
                                    onValueChange = { viewModel.updateDeliveryNote(it) },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Rincian Pembayaran
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Rincian Pembayaran", style = MaterialTheme.typography.headlineSmall)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Harga Normal ($totalPcs Pcs)", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                                Text(formatRupiah(rawTotal), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Outlined.Stars, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                    Text("Diskon Pelanggan Tetap RT", style = MaterialTheme.typography.bodyMedium, color = Primary)
                                }
                                Text("-${formatRupiah(discountAmount)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pembulatan Nominal Warung", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                                Text("Rp 0", style = MaterialTheme.typography.bodyMedium)
                            }
                            HorizontalDivider(color = SurfaceContainerHighest)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Tagihan Kasir Bersih", style = MaterialTheme.typography.labelLarge)
                                    Text("Sudah termasuk PPN & diskon", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                                Text(
                                    text = formatRupiah(cleanTotal.let { if (it > 0) it else 64000L }),
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Primary
                                )
                            }
                        }
                    }
                }

                // Pilihan Cepat Uang Diterima
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Pilihan Cepat Uang Diterima:",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Pair("Uang Pas", cleanTotal),
                                    Pair("Rp 65rb", 65000L),
                                    Pair("Rp 70rb", 70000L),
                                    Pair("Rp 100rb", 100000L)
                                ).forEach { (label, amountVal) ->
                                    Surface(
                                        onClick = { viewModel.openPaymentWithPreset(amountVal) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceContainerLowest,
                                        shadowElevation = 1.dp,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                            }
                    }
                }
            }
        }

        // Sticky Bottom Checkout Dock (Emerald Green Container)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = PrimaryContainer,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL BELANJA ($totalItemTypes ITEM • $totalPcs PCS)",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnPrimaryContainer.copy(alpha = 0.9f)
                        )
                        Text(
                            text = formatRupiah(cleanTotal.let { if (it > 0) it else 64000L }),
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.Savings, contentDescription = null, tint = PrimaryFixed, modifier = Modifier.size(14.dp))
                                Text("Hemat Rp 5.000", style = MaterialTheme.typography.labelSmall, color = PrimaryFixed)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Metode: Tunai / Kasbon / QRIS",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        onClick = {
                            viewModel.completeCheckout(PaymentMethod.KASBON, 0L, "Ibu RT Rohanah")
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("Catat Kasbon", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }

                        Surface(
                            onClick = { viewModel.openPaymentWithPreset(if (cleanTotal > 0L) cleanTotal else 64000L) },
                            shape = RoundedCornerShape(14.dp),
                            color = SecondaryContainer,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .weight(2f)
                                .height(54.dp)
                                .testTag("cart_detail_pay_now")
                        ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.Payments, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(24.dp))
                                Text("BAYAR\nSEKARANG", style = MaterialTheme.typography.headlineSmall.copy(lineHeight = 18.sp), color = OnSecondaryContainer)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }

        if (showPosScannerSheet) {
            AlertDialog(
                onDismissRequest = { showPosScannerSheet = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = Primary)
                        Text("Scan / Pilih Barang Cepat", style = MaterialTheme.typography.headlineSmall)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ketuk barang di bawah untuk memindai barcode & menambahkannya ke daftar belanja:",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                        viewModel.posCatalog.take(6).forEach { catItem ->
                            Surface(
                                onClick = {
                                    viewModel.scanOrSelectCatalogItem(catItem, 1.0)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(catItem.name, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                                        Text("${catItem.barcode} • ${formatRupiah(catItem.price)}/${catItem.unit}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                    Surface(shape = RoundedCornerShape(6.dp), color = Primary) {
                                        Text(
                                            text = "+ Tambah",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showPosScannerSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Selesai Pilih")
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailedCartItemCard(
    item: CartItem,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onRemove: () -> Unit,
    onNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    NetworkProductImage(
                        url = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                if (item.category.contains("Minyak")) Secondary.copy(alpha = 0.9f)
                                else Primary.copy(alpha = 0.9f)
                            )
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus Item",
                            tint = OnSurfaceVariant,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable(onClick = onRemove)
                        )
                    }
                    Text(
                        text = "${formatRupiah(item.price)} / ${item.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    if (!item.badgeText.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.badgeText.contains("Tukar")) OnPrimaryContainer else TertiaryFixed
                        ) {
                            Text(
                                text = item.badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (item.badgeText.contains("Tukar")) Primary else OnTertiaryFixedVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHigh)
                            .clickable(onClick = onDecrement),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Remove, contentDescription = "Kurangi", tint = OnSurface)
                    }
                    Text(
                        text = item.qty.toInt().toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.widthIn(min = 32.dp),
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryContainer)
                            .clickable(onClick = onIncrement),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Tambah", tint = Color.White)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (item.qty > 1) "Subtotal (${item.qty.toInt()}x)" else "Subtotal",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = formatRupiah(item.subtotal),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Primary
                    )
                }
            }

            if (!item.note.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .clickable(onClick = onNoteClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Outlined.EditNote, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                        Text(
                            text = item.note,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = if (item.note.contains("HET")) "+ Diskon Rp" else "Ubah Catatan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (item.note.contains("HET")) Secondary else Primary
                    )
                }
            }
        }
    }
}
