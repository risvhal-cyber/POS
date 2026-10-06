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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@Composable
fun RiwayatHistoryScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("Semua (48)") }
    var showScanNotaDialog by remember { mutableStateOf(false) }

    val filteredTransactions = remember(transactions, searchQuery, activeFilter) {
        transactions.filter { trx ->
            val matchesQuery = searchQuery.isBlank() ||
                    trx.customerName.contains(searchQuery, ignoreCase = true) ||
                    trx.code.contains(searchQuery, ignoreCase = true) ||
                    trx.itemsSummary.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when {
                activeFilter.startsWith("Semua") -> true
                activeFilter.startsWith("Lunas Tunai") -> trx.method == PaymentMethod.TUNAI
                activeFilter.startsWith("QRIS") -> trx.method == PaymentMethod.QRIS
                activeFilter.startsWith("Kasbon") -> trx.method == PaymentMethod.KASBON || trx.method == PaymentMethod.SPLIT_BON
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    val soreList = filteredTransactions.filter { it.timeGroup.startsWith("Sore") }
    val siangList = filteredTransactions.filter { it.timeGroup.startsWith("Siang") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.95f),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = WarungImages.LOGO,
                        contentDescription = "Logo Warung POS",
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                    Column {
                        Text(
                            text = "Toko Kelontong",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Primary
                        )
                        Text(
                            text = "Riwayat",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CartHeaderBadgeButton(
                        itemCount = cartItems.size,
                        totalAmount = cartItems.sumOf { it.subtotal },
                        onClick = { viewModel.navigateTo(ScreenRoute.CartDetail) },
                        compact = true
                    )
                    ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                // Top Search & Quick Filter Bar
                item {
                    Surface(
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.Search, contentDescription = null, tint = OutlineColor, modifier = Modifier.size(22.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchQuery.isEmpty()) {
                                        Text("Cari nomor nota, nama, barang...", style = MaterialTheme.typography.bodyMedium, color = OutlineColor)
                                    }
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Surface(
                                    onClick = { showScanNotaDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceContainerHigh,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan Nota", tint = Primary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val filters = listOf(
                                    "Semua (48)",
                                    "Lunas Tunai (32)",
                                    "QRIS / Bank (10)",
                                    "Kasbon / Bon (4)",
                                    "Batal / Hold (2)"
                                )
                                filters.forEach { f ->
                                    val isSel = activeFilter == f
                                    Surface(
                                        onClick = {
                                            activeFilter = f
                                            viewModel.showToast("Filter: $f")
                                        },
                                        shape = RoundedCornerShape(50),
                                        color = if (isSel) Primary else SurfaceContainerLow
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (isSel) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                            }
                                            Text(
                                                text = f,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSel) Color.White else OnSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Ledger Financial Summary Card (Kilas Kas & Buku Besar)
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                        Text("KILAS KAS & BUKU BESAR", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                                    }
                                    Text(
                                        text = "Rp 2.840.000",
                                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = OnSurface,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Text(
                                        text = "48 transaksi berhasil tercatat hari ini",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }

                                Surface(
                                    onClick = { viewModel.showToast("Menyiapkan rekap harian PDF...") },
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceContainerLow
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                        Text("Rekap", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(10.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("● Tunai Laci", style = MaterialTheme.typography.labelSmall, color = Primary)
                                        Text("Rp 2.145.000", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 2.dp))
                                        Text("32 Nota", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(10.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("● QRIS/Bank", style = MaterialTheme.typography.labelSmall, color = Tertiary)
                                        Text("Rp 450.000", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 2.dp))
                                        Text("10 Masuk", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(10.dp), color = SecondaryContainer.copy(alpha = 0.2f), modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("● Kasbon Baru", style = MaterialTheme.typography.labelSmall, color = OnSecondaryContainer)
                                        Text("Rp 245.000", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Secondary, modifier = Modifier.padding(top = 2.dp))
                                        Text("6 Pelanggan", style = MaterialTheme.typography.labelSmall, color = Secondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Group 1: Sore Ini (15:00 - 18:00)
                if (soreList.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Outlined.WbTwilight, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                Text("Sore Ini (15:00 - 18:00)", style = MaterialTheme.typography.headlineSmall)
                            }
                            Text("${soreList.size} Transaksi", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }

                    items(soreList.size) { idx ->
                        TransactionLedgerCard(
                            trx = soreList[idx],
                            onPrint = { viewModel.showToast("Mencetak struk ${soreList[idx].code} ke Mini POS Thermal...") },
                            onAction = {
                                if (soreList[idx].isKasbonPending) {
                                    viewModel.selectTab(MainTab.KASBON)
                                } else {
                                    viewModel.showToast("Membuka WhatsApp ${soreList[idx].customerName}...")
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // Group 2: Siang Hari (11:00 - 14:59)
                if (siangList.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(18.dp))
                                Text("Siang Hari (11:00 - 14:59)", style = MaterialTheme.typography.headlineSmall)
                            }
                            Text("${siangList.size} Transaksi", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }

                    items(siangList.size) { idx ->
                        TransactionLedgerCard(
                            trx = siangList[idx],
                            onPrint = { viewModel.showToast("Mencetak struk ${siangList[idx].code}...") },
                            onAction = { viewModel.showToast("Rincian transaksi ${siangList[idx].customerName}") },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                // Kas Kasir Seimbang Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Outlined.Verified, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Kas Kasir Seimbang", style = MaterialTheme.typography.headlineSmall)
                                Text(
                                    text = "Seluruh fisik uang laci cocok dengan catatan sistem tanpa selisih minus.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Sticky Bottom Quick Summary Dock (Ekspor Excel)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = InverseSurface,
                    shadowElevation = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Outlined.Insights, contentDescription = null, tint = PrimaryFixed, modifier = Modifier.size(15.dp))
                                Text("TOKO KELONTONG BERKAH", style = MaterialTheme.typography.labelSmall, color = InverseOnSurface.copy(alpha = 0.8f))
                            }
                            Text(
                                text = "48 Transaksi • Rp 2.840.000",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
                                color = InverseOnSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            onClick = { viewModel.showToast("Laporan 48 transaksi diekspor ke format Excel (.xlsx)") },
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryContainer,
                            modifier = Modifier.height(42.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Outlined.TableView, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("Ekspor Excel", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 14.sp), color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScanNotaDialog) {
        Dialog(onDismissRequest = { showScanNotaDialog = false }) {
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
                        Text("Pindai Barcode / Nota", style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = { showScanNotaDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHighest)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.CenterFocusStrong, contentDescription = null, tint = Primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Arahkan kamera ke nota atau barcode barang", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = {
                            searchQuery = "#TRX-20240524-0042"
                            showScanNotaDialog = false
                            viewModel.showToast("Nota #TRX-20240524-0042 ditemukan (Ibu Anisa)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Simulasi Deteksi Nota #0042")
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionLedgerCard(
    trx: TransactionRecord,
    onPrint: () -> Unit,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (trx.method == PaymentMethod.KASBON) Modifier.border(1.5.dp, SecondaryContainer, RoundedCornerShape(14.dp))
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when (trx.method) {
                                    PaymentMethod.TUNAI -> PrimaryFixed
                                    PaymentMethod.QRIS -> TertiaryFixed
                                    else -> SecondaryFixed
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (trx.method) {
                                PaymentMethod.TUNAI -> Icons.Outlined.ShoppingBag
                                PaymentMethod.QRIS -> Icons.Outlined.QrCode2
                                else -> Icons.Outlined.MenuBook
                            },
                            contentDescription = null,
                            tint = when (trx.method) {
                                PaymentMethod.TUNAI -> Primary
                                PaymentMethod.QRIS -> Tertiary
                                else -> Secondary
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = trx.customerName,
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (trx.method == PaymentMethod.KASBON) SecondaryFixed else PrimaryFixed
                            ) {
                                Text(
                                    text = trx.customerBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = if (trx.method == PaymentMethod.KASBON) OnSecondaryFixed else OnPrimaryFixed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${trx.code} • ${trx.timeWib}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatRupiah(trx.totalAmount),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (trx.method == PaymentMethod.KASBON) Secondary else OnSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when (trx.method) {
                            PaymentMethod.TUNAI -> PrimaryContainer
                            PaymentMethod.QRIS -> Tertiary
                            else -> SecondaryContainer
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "✓ ${trx.method.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (trx.method == PaymentMethod.KASBON) OnSecondaryContainer else Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Item Preview Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (trx.method == PaymentMethod.KASBON) SecondaryFixed.copy(alpha = 0.35f) else SurfaceContainerLow)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (trx.previews.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trx.previews.forEach { prev ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    NetworkProductImage(
                                        url = prev.imageUrl,
                                        contentDescription = prev.label,
                                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp))
                                    )
                                    Column(modifier = Modifier.padding(end = 6.dp)) {
                                        Text(prev.label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = OnSurfaceVariant)
                                        Text(prev.qtyLabel, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = Primary)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = trx.itemsSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = trx.totalItemsCount,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(trx.footerLeft, style = MaterialTheme.typography.labelSmall, color = OutlineColor)
                    Text(trx.footerRight, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = if (trx.isKasbonPending) Secondary else OutlineColor)
                }
            }

            // Action Buttons Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (trx.method == PaymentMethod.KASBON) {
                        Surface(
                            onClick = onAction,
                            shape = RoundedCornerShape(8.dp),
                            color = Primary,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Text("Pelunasan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }
                        Surface(
                            onClick = onPrint,
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.Send, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                                Text("Tagih WA", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                            }
                        }
                    } else {
                        Surface(
                            onClick = onPrint,
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.Print, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Text("Cetak Struk", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                            }
                        }
                        Surface(
                            onClick = onAction,
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Chat, contentDescription = "WhatsApp", tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.clickable(onClick = onAction),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (trx.method == PaymentMethod.KASBON) "Buku Bon" else "Rincian",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurfaceVariant
                    )
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
