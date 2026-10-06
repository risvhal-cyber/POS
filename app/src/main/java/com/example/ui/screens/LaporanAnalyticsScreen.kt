package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@Composable
fun LaporanAnalyticsScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    var activePeriod by remember { mutableStateOf("Harian (Hari Ini)") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
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
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryContainer.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Storefront, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text("TOKO KELONTONG", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text(
                            text = "Laporan Omzet Keuangan",
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                        onClick = { viewModel.navigateTo(ScreenRoute.CartDetail) }
                    )
                    ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Time Period Segmented Scroll
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Harian (Hari Ini)", "Mingguan (7 Hari)", "Bulanan (Mei 2025)", "Kustom").forEach { period ->
                        val isSel = activePeriod == period
                        Surface(
                            onClick = {
                                activePeriod = period
                                viewModel.showToast("Menampilkan laporan: $period")
                            },
                            shape = RoundedCornerShape(50),
                            color = if (isSel) PrimaryContainer else SurfaceContainerLowest,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (period.startsWith("Harian")) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarToday,
                                        contentDescription = null,
                                        tint = if (isSel) Color.White else OnSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = period,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) Color.White else OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Hero Omzet & Profit Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("LAUFI WARUNG SEMBAKO", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Text("Total Penjualan Kotor (Omzet)", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Surface(shape = RoundedCornerShape(50), color = PrimaryFixed) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = OnPrimaryFixedVariant, modifier = Modifier.size(14.dp))
                                    Text("+14% vs kemarin", style = MaterialTheme.typography.labelSmall, color = OnPrimaryFixedVariant)
                                }
                            }
                        }

                        Text(
                            text = "Rp 2.840.000",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Primary
                        )

                        // Micro Stats Bento Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Outlined.Savings, contentDescription = null, tint = Primary, modifier = Modifier.size(15.dp))
                                        Text("Untung Bersih", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Text("Rp 415.000", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 4.dp))
                                    Text("Margin 14.6%", style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Secondary, modifier = Modifier.size(15.dp))
                                        Text("Transaksi", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Text("84 Nota", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 4.dp))
                                    Text("Sukses cetak", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                }
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Outlined.ShoppingCartCheckout, contentDescription = null, tint = Tertiary, modifier = Modifier.size(15.dp))
                                        Text("Rata-rata", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Text("Rp 33.800", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 4.dp))
                                    Text("Per pembeli", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Hourly Rush Chart
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.QueryStats, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    Text("Omzet Jam Sibuk Warung", style = MaterialTheme.typography.headlineSmall)
                                }
                                Text("Rentang operasional 06:00 - 21:00", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = SurfaceContainer) {
                                Text("Hari Ini", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        // Custom Canvas Bar Chart
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow.copy(alpha = 0.6f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(125.dp)
                            ) {
                                val w = size.width
                                val h = size.height
                                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                                // Grid lines
                                listOf(0.2f, 0.55f, 0.9f).forEach { yRatio ->
                                    drawLine(
                                        color = OutlineVariant.copy(alpha = 0.6f),
                                        start = Offset(0f, h * yRatio),
                                        end = Offset(w, h * yRatio),
                                        strokeWidth = 2f,
                                        pathEffect = dashEffect
                                    )
                                }

                                val barWidth = w * 0.12f
                                val barHeights = listOf(0.42f, 0.64f, 0.88f, 0.74f)
                                val barColors = listOf(PrimaryFixedDim, PrimaryContainer, Primary, PrimaryContainer)

                                barHeights.forEachIndexed { idx, ratio ->
                                    val centerX = w * (0.125f + idx * 0.25f)
                                    val barH = h * ratio
                                    val topY = h * 0.92f - barH
                                    drawRoundRect(
                                        color = barColors[idx],
                                        topLeft = Offset(centerX - barWidth / 2f, topY),
                                        size = Size(barWidth, barH),
                                        cornerRadius = CornerRadius(12f, 12f)
                                    )
                                    if (idx == 2) {
                                        drawCircle(
                                            color = ErrorColor,
                                            radius = 10f,
                                            center = Offset(centerX, topY - 8f)
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Pagi 06-08", style = MaterialTheme.typography.labelSmall)
                                    Text("420rb", style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Siang 11-13", style = MaterialTheme.typography.labelSmall)
                                    Text("680rb", style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Sore 16-19 🔥", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("950rb", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Malam 19-21", style = MaterialTheme.typography.labelSmall)
                                    Text("790rb", style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerHigh.copy(alpha = 0.45f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.WbTwilight, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Puncak Omzet Sore: Ibu-ibu & buruh pabrik borong jajan, sembako, dan kopi sachet setelah jam kantor (16.00-18.30).",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Cash Flow / Payment Breakdown (Arus Kas Masuk)
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                Text("Arus Kas Masuk", style = MaterialTheme.typography.headlineSmall)
                            }
                            Text("3 Metode", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }

                        // Stacked Progress Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(50))
                        ) {
                            Box(modifier = Modifier.weight(0.757f).fillMaxHeight().background(PrimaryContainer))
                            Box(modifier = Modifier.weight(0.157f).fillMaxHeight().background(TertiaryContainer))
                            Box(modifier = Modifier.weight(0.086f).fillMaxHeight().background(SecondaryContainer))
                        }

                        val breakdownRows = listOf(
                            Triple("Tunai di Laci Kasir", "Siap setor atau belanja kulakan", Pair("Rp 2.150.000", "75.7%")),
                            Triple("QRIS Toko (Bank/E-Wallet)", "Langsung masuk saldo rekening", Pair("Rp 445.000", "15.7%")),
                            Triple("Kasbon / Piutang Baru", "Tercatat di buku utang warga", Pair("Rp 245.000", "8.6%"))
                        )
                        breakdownRows.forEachIndexed { idx, (title, sub, rightPair) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(10.dp),
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
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (idx) {
                                                    0 -> PrimaryContainer.copy(alpha = 0.15f)
                                                    1 -> TertiaryContainer.copy(alpha = 0.15f)
                                                    else -> SecondaryContainer.copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (idx) {
                                                0 -> Icons.Outlined.Payments
                                                1 -> Icons.Outlined.QrCodeScanner
                                                else -> Icons.Outlined.MenuBook
                                            },
                                            contentDescription = null,
                                            tint = when (idx) {
                                                0 -> Primary
                                                1 -> Tertiary
                                                else -> Secondary
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(sub, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(rightPair.first, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(
                                        text = rightPair.second,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when (idx) {
                                            0 -> Primary
                                            1 -> Tertiary
                                            else -> Secondary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Top 5 Best Sellers (Komoditas Terlaris Hari Ini)
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Text("Komoditas Terlaris Hari Ini", style = MaterialTheme.typography.headlineSmall)
                            }
                            Text("Top 5 SKU", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }

                        val topSellers = listOf(
                            listOf("#1", "Gas LPG 3kg Melon", "Terjual: 18 Tabung", "Rp 378.000", "+Untung 45rb", WarungImages.GAS_LPG_1),
                            listOf("#2", "Beras Ramos Eceran", "Terjual: 32 Kilogram", "Rp 480.000", "+Untung 64rb", WarungImages.BERAS_SCOOP),
                            listOf("#3", "Minyak Goreng Kita 1L", "Terjual: 22 Pouch", "Rp 363.000", "+Untung 44rb", WarungImages.MINYAK_KITA_1),
                            listOf("#4", "Galon Aqua 19L Refill", "Terjual: 14 Galon", "Rp 280.000", "+Untung 42rb", WarungImages.GALON_AQUA_1),
                            listOf("#5", "Telur Ayam Curah", "Terjual: 8.5 Kilogram", "Rp 238.000", "+Untung 29.7rb", WarungImages.TELUR_1)
                        )
                        topSellers.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(10.dp),
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
                                            .clip(RoundedCornerShape(10.dp))
                                    ) {
                                        NetworkProductImage(
                                            url = row[5],
                                            contentDescription = row[1],
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(topEnd = 6.dp),
                                            color = if (row[0] == "#1") Primary else SurfaceVariant,
                                            modifier = Modifier.align(Alignment.BottomStart)
                                        ) {
                                            Text(
                                                text = row[0],
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = if (row[0] == "#1") Color.White else OnSurface,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(row[1], style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(row[2], style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(row[3], style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(row[4], style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Buttons (Thermal Print & Share WhatsApp & Download)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.showToast("Mencetak rekap laporan omzet ke Printer Thermal...") },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLowest,
                            shadowElevation = 1.dp,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Print, contentDescription = null, tint = Tertiary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cetak Thermal", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Surface(
                            onClick = { viewModel.showToast("Mengirim laporan omzet ke WhatsApp Pemilik...") },
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryContainer,
                            shadowElevation = 1.dp,
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kirim WA Pemilik", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            }
                        }
                    }

                    Surface(
                        onClick = { viewModel.showToast("Laporan lengkap (.Excel / .PDF Rekap) berhasil diunduh!") },
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLow,
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download Laporan Lengkap (.Excel / .PDF Rekap)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
