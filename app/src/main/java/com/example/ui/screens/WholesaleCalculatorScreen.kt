package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel
import java.util.Locale

@Composable
fun WholesaleCalculatorScreen(
    openScanSheetInitially: Boolean,
    initialSkuName: String,
    viewModel: WarungViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val cartItems by viewModel.cartItems.collectAsState()
    var showScanModal by remember { mutableStateOf(openScanSheetInitially) }

    var productName by remember { mutableStateOf(initialSkuName) }
    var wholesalePriceText by remember { mutableStateOf("36000") }
    var selectedUnitType by remember { mutableStateOf("lusin") }
    var unitCount by remember { mutableIntStateOf(12) }
    var extraCostPerPcs by remember { mutableLongStateOf(0L) }
    var showExtraCosts by remember { mutableStateOf(false) }
    var targetMarginPercent by remember { mutableDoubleStateOf(10.0) }
    var currentSellingPrice by remember { mutableLongStateOf(3500L) }

    val wholesalePrice = wholesalePriceText.toLongOrNull() ?: 36000L
    val baseHpp = if (unitCount > 0) Math.round(wholesalePrice.toDouble() / unitCount.toDouble()) else 0L
    val totalHpp = baseHpp + extraCostPerPcs
    val rawMathSelling = Math.round(totalHpp * (1.0 + targetMarginPercent / 100.0))

    val roundUp1000 = ((rawMathSelling + 999L) / 1000L) * 1000L
    val roundUp500 = ((rawMathSelling + 499L) / 500L) * 500L

    val profitPerPcs = currentSellingPrice - totalHpp
    val totalPackProfit = profitPerPcs * unitCount
    val actualMarginPct = if (totalHpp > 0) (profitPerPcs.toDouble() / totalHpp.toDouble()) * 100.0 else 0.0

    fun loadPresetSample(name: String, price: Long, qty: Int, type: String, defaultSell: Long) {
        productName = name
        wholesalePriceText = price.toString()
        unitCount = qty
        selectedUnitType = type
        currentSellingPrice = defaultSell
    }

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
                        .height(68.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kalkulator Kulakan & Margin",
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Konversi Lusin/Kodi ke Eceran",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
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
                            onClick = { viewModel.navigateTo(ScreenRoute.CartDetail) },
                            compact = true
                        )
                        ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Intro Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryContainer.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Calculate, contentDescription = null, tint = Primary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Kalkulator Modal & Harga Jual", style = MaterialTheme.typography.headlineSmall)
                                    Surface(shape = RoundedCornerShape(50), color = SecondaryFixed) {
                                        Text(
                                            text = "Eceran",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSecondaryFixed,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Bagi harga kulakan grosir jadi modal per biji & bulatkan harga kasir tanpa pusing uang kembalian.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Search + Scan Button + Fast Select Restock Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLowest,
                                shadowElevation = 1.dp,
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Search, contentDescription = null, tint = OutlineColor, modifier = Modifier.size(20.dp))
                                    BasicTextField(
                                        value = productName,
                                        onValueChange = { productName = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Button(
                                onClick = { showScanModal = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("btn_open_scan_kulakan")
                            ) {
                                Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        // Quick Source Chips
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val samples = listOf(
                                Triple("Korek Tokai (Lusin)", { loadPresetSample("Korek Api Gas Tokai", 36000L, 12, "lusin", 3500L) }, "lusin"),
                                Triple("Minyak Kita 1L (Dus)", { loadPresetSample("Minyak Goreng Kita 1L", 174000L, 12, "dus", 16500L) }, "dus"),
                                Triple("Gas LPG 3kg (5 tbg)", { loadPresetSample("Gas LPG 3kg Melon", 92500L, 5, "custom", 21000L) }, "custom"),
                                Triple("Buku Kiky (Pak 10)", { loadPresetSample("Buku Tulis Kiky 38lbr", 52000L, 10, "pak", 6000L) }, "pak")
                            )
                            samples.forEach { (label, action, typeKey) ->
                                val isSel = selectedUnitType == typeKey
                                Surface(
                                    onClick = action,
                                    shape = RoundedCornerShape(50),
                                    color = if (isSel) Primary else SurfaceContainerHighest
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSel) Color.White else OnSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 1: Data Pembelian Kulakan & Satuan Kemasan
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape).background(PrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("1", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                    }
                                    Text("Data Pembelian Kulakan", style = MaterialTheme.typography.headlineSmall)
                                }
                                Surface(
                                    onClick = { showScanModal = true },
                                    shape = RoundedCornerShape(50),
                                    color = PrimaryContainer.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Dari Nota Grosir",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = wholesalePriceText,
                                onValueChange = { wholesalePriceText = it.filter { c -> c.isDigit() } },
                                label = { Text("Total Harga Beli Grosir (Rp)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text("Satuan Kemasan Kulakan", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)

                            val units = listOf(
                                Triple("lusin", "Lusin", "12 pcs" to 12),
                                Triple("kodi", "Kodi", "20 pcs" to 20),
                                Triple("gros", "Gros", "144 pcs" to 144),
                                Triple("dus", "Dus/Karton", "Custom" to 24),
                                Triple("pak", "Pak/Renceng", "Custom" to 10),
                                Triple("custom", "Manual", "Bebas" to unitCount)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                units.chunked(3).forEach { rowUnits ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        rowUnits.forEach { (key, title, subPair) ->
                                            val isSel = selectedUnitType == key
                                            Surface(
                                                onClick = {
                                                    selectedUnitType = key
                                                    unitCount = subPair.second
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isSel) PrimaryContainer else SurfaceContainerHigh,
                                                modifier = Modifier.weight(1f).height(52.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = title,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        color = if (isSel) Color.White else OnSurface
                                                    )
                                                    Text(
                                                        text = subPair.first,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isSel) Color.White.copy(alpha = 0.9f) else OnSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Isi Satuan Stepper
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Isi Satuan (Pcs)", style = MaterialTheme.typography.labelLarge)
                                    Text("Jumlah biji per pack kulak", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = { if (unitCount > 1) unitCount-- },
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerLowest,
                                        shadowElevation = 1.dp,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.Remove, contentDescription = "Kurangi")
                                        }
                                    }
                                    Text(
                                        text = unitCount.toString(),
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Surface(
                                        onClick = { unitCount++ },
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerLowest,
                                        shadowElevation = 1.dp,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.Add, contentDescription = "Tambah")
                                        }
                                    }
                                }
                            }

                            Surface(
                                onClick = { showScanModal = true },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Outlined.Checklist, contentDescription = null, tint = Primary)
                                        Text(
                                            text = "Lihat Daftar Belanjaan / Nota Kulakan (4 Darurat)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Primary
                                        )
                                    }
                                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = OutlineColor)
                                }
                            }
                        }
                    }
                }

                // Section 2: Modal Pokok (HPP) Bersih
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimaryContainer,
                        shadowElevation = 2.dp,
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("2", style = MaterialTheme.typography.labelSmall, color = Primary)
                                    }
                                    Text("Modal Pokok (HPP) Bersih", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                }
                                Surface(shape = RoundedCornerShape(50), color = PrimaryFixedDim) {
                                    Text(
                                        text = "Otomatis Terbagi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnPrimaryFixed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text("Modal Kulak Eceran:", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = formatRupiah(totalHpp),
                                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Color.White
                                        )
                                        Text("/ pcs", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.85f))
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Rumus:", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
                                    Text(
                                        text = "${formatRupiah(wholesalePrice)} ÷ $unitCount",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                onClick = { showExtraCosts = !showExtraCosts },
                                shape = RoundedCornerShape(10.dp),
                                color = Primary,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Text(
                                            text = if (extraCostPerPcs > 0) "Biaya Ekstra Aktif: +Rp $extraCostPerPcs/pcs" else "+ Tambah Biaya Ongkir / Parkir / Kresek",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showExtraCosts) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }

                            if (showExtraCosts) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(0L, 50L, 100L, 200L).forEach { fee ->
                                        Surface(
                                            onClick = { extraCostPerPcs = fee },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (extraCostPerPcs == fee) SecondaryContainer else SurfaceContainerLowest,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = if (fee == 0L) "Nol" else "+Rp $fee",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 3: Target Margin & Harga Jual
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape).background(PrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("3", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                    }
                                    Text("Target Margin & Harga Jual", style = MaterialTheme.typography.headlineSmall)
                                }
                                Text("Bebas Receh", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Secondary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pilih Persentase Untung Bersih", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                                Text("+${targetMarginPercent.toInt()}% Terpilih", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(10.0, 15.0, 20.0, 25.0, 33.3).forEach { pct ->
                                    val isSel = targetMarginPercent == pct
                                    Surface(
                                        onClick = { targetMarginPercent = pct },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSel) PrimaryContainer else SurfaceContainerHigh,
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${pct.toInt()}%",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSel) Color.White else OnSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // Hitungan Matematis Murni
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("∑ Hitungan Matematis Murni (+${targetMarginPercent.toInt()}%):", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                Text(formatRupiah(rawMathSelling), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            }

                            Text("Rekomendasi Pembulatan Kasir Warung: Cepat Transaksi", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)

                            // Option A: Round Up 1000
                            val isOptASel = currentSellingPrice == roundUp1000
                            Surface(
                                onClick = { currentSellingPrice = roundUp1000 },
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Icon(
                                            imageVector = if (isOptASel) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isOptASel) PrimaryContainer else OutlineColor
                                        )
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(formatRupiah(roundUp1000), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                                                Surface(shape = RoundedCornerShape(4.dp), color = Primary) {
                                                    Text("Disarankan", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                            val labaA = roundUp1000 - totalHpp
                                            Text("Laba: ${formatRupiah(labaA)} / pcs", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Uang Pas", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Icon(Icons.Outlined.Payments, contentDescription = null, tint = Secondary)
                                    }
                                }
                            }

                            // Option B: Round Up 500
                            val optBPrice = if (roundUp500 == roundUp1000) (roundUp1000 - 500L).coerceAtLeast(totalHpp) else roundUp500
                            val isOptBSel = currentSellingPrice == optBPrice
                            Surface(
                                onClick = { currentSellingPrice = optBPrice },
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Icon(
                                            imageVector = if (isOptBSel) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isOptBSel) PrimaryContainer else OutlineColor
                                        )
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(formatRupiah(optBPrice), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                                                Surface(shape = RoundedCornerShape(4.dp), color = SurfaceContainerHighest) {
                                                    Text("Harga Murah", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                            val labaB = optBPrice - totalHpp
                                            Text("Laba: ${formatRupiah(labaB)} / pcs", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Pecahan 500", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Icon(Icons.Outlined.Toll, contentDescription = null, tint = OutlineColor)
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 4: Proyeksi Laba Bersih
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape).background(PrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("4", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                    }
                                    Text("Proyeksi Laba Bersih", style = MaterialTheme.typography.headlineSmall)
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = OnPrimaryContainer) {
                                    Text("Sehat & Untung", style = MaterialTheme.typography.labelSmall, color = Primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(shape = RoundedCornerShape(10.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Untung / Pcs", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text("+${formatRupiah(profitPerPcs)}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                                        Text("per barang", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(10.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Laba 1 ${selectedUnitType.uppercase()}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text(formatRupiah(totalPackProfit), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                                        Text("$unitCount pcs terjual", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(10.dp), color = SurfaceContainerLow, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("% Margin", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text("${String.format(Locale.US, "%.1f", actualMarginPct)}%", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Secondary)
                                        Text("Sangat Baik", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("✓ Evaluasi Daya Saing & Margin Warung", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Standar Grosir (10-22%)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Secondary)
                                    }
                                    LinearProgressIndicator(
                                        progress = { (actualMarginPct.toFloat() / 35f).coerceIn(0.15f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                                        color = SecondaryContainer,
                                        trackColor = SurfaceContainerLowest
                                    )
                                    Text(
                                        text = "Cocok untuk sembako putaran cepat (beras, minyak, gula, rokok) yang laku setiap hari.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Action Bar
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = SurfaceContainerLowest,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$productName • HPP ${formatRupiah(totalHpp)}", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Text("+${formatRupiah(profitPerPcs)} (${String.format(Locale.US, "%.1f", actualMarginPct)}%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            loadPresetSample("Korek Api Gas Tokai", 36000L, 12, "lusin", 3500L)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerHigh,
                        modifier = Modifier.weight(4f).height(52.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ulang", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Surface(
                        onClick = {
                            viewModel.saveWholesaleCalcToCatalog(productName, totalHpp, currentSellingPrice, unitCount)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryContainer,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .weight(8f)
                            .height(52.dp)
                            .testTag("btn_save_wholesale_calc")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.AddTask, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan: ${formatRupiah(currentSellingPrice)}", style = MaterialTheme.typography.labelLarge, color = Color.White)
                        }
                    }
                }
            }
        }

        // Scan Barcode & Kulakan Bottom Sheet Overlay (Matches Image 3)
        if (showScanModal) {
            ScanBarcodeKulakanOverlay(
                onDismiss = { showScanModal = false },
                onSelectSku = { name, price, qty, type, sell ->
                    loadPresetSample(name, price, qty, type, sell)
                    showScanModal = false
                    viewModel.showToast("Deteksi $name dimuat ke kalkulator!")
                }
            )
        }
    }
}

@Composable
private fun ScanBarcodeKulakanOverlay(
    onDismiss: () -> Unit,
    onSelectSku: (String, Long, Int, String, Long) -> Unit
) {
    var activeScanTab by remember { mutableIntStateOf(0) } // 0: Kamera Barcode, 1: Ketik / Stok Kritis
    var flashOn by remember { mutableStateOf(false) }
    var barcodeInput by remember { mutableStateOf("8992775211025") }

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
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(width = 48.dp, height = 6.dp).clip(RoundedCornerShape(50)).background(OutlineVariant))
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
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = OnPrimaryFixed)
                        }
                        Column {
                            Text("Scan Barcode & Kulakan", style = MaterialTheme.typography.headlineSmall)
                            Text("Konversi modal dus & margin eceran", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(38.dp).clip(CircleShape).background(SurfaceContainerLow)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup Dialog")
                    }
                }

                // Switch Tabs: Kamera Barcode vs Ketik / Stok Kritis
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        onClick = { activeScanTab = 0 },
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeScanTab == 0) SurfaceContainerLowest else Color.Transparent,
                        shadowElevation = if (activeScanTab == 0) 1.dp else 0.dp,
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = if (activeScanTab == 0) Primary else OnSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kamera Barcode", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = if (activeScanTab == 0) Primary else OnSurfaceVariant)
                        }
                    }
                    Surface(
                        onClick = { activeScanTab = 1 },
                        shape = RoundedCornerShape(8.dp),
                        color = if (activeScanTab == 1) SurfaceContainerLowest else Color.Transparent,
                        shadowElevation = if (activeScanTab == 1) 1.dp else 0.dp,
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ManageSearch, contentDescription = null, tint = if (activeScanTab == 1) Primary else OnSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ketik / Stok Kritis", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = if (activeScanTab == 1) Primary else OnSurfaceVariant)
                        }
                    }
                }

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Viewfinder Box
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(195.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(InverseSurface)
                                .padding(10.dp)
                        ) {
                            // Top status + Flash
                            Row(
                                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
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
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryFixed))
                                        Text("SENSOR AKTIF (AUTO-FOCUS)", style = MaterialTheme.typography.labelSmall, color = OnPrimaryContainer)
                                    }
                                }

                                IconButton(
                                    onClick = { flashOn = !flashOn },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (flashOn) SecondaryContainer else Color.Black.copy(alpha = 0.4f))
                                ) {
                                    Icon(Icons.Outlined.FlashOn, contentDescription = "Senter", tint = if (flashOn) OnSecondaryContainer else Color.White, modifier = Modifier.size(18.dp))
                                }
                            }

                            // Center Reticle
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(width = 210.dp, height = 90.dp)
                                    .border(2.dp, PrimaryFixed, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.fillMaxWidth(0.85f).height(2.dp).background(PrimaryFixed))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("8992775211025", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }

                            // Bottom Detection Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Primary,
                                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
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
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = PrimaryFixed, modifier = Modifier.size(20.dp))
                                        Column {
                                            Text("TERDETEKSI CEPAT", style = MaterialTheme.typography.labelSmall, color = PrimaryFixed)
                                            Text("Minyak Goreng Kita 1L Dus (Isi 12)", style = MaterialTheme.typography.headlineSmall, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    Surface(shape = RoundedCornerShape(6.dp), color = PrimaryContainer) {
                                        Text("12 Pouch", style = MaterialTheme.typography.labelSmall, color = OnPrimaryContainer, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = barcodeInput,
                            onValueChange = { barcodeInput = it },
                            label = { Text("Ketik nama sembako / no. barcode...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Dari Stok Kritis (3 Perlu Restock)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Outlined.NotificationImportant, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                Text("Dari Stok Kritis (Kulak Segera)", style = MaterialTheme.typography.labelLarge)
                            }
                            Surface(shape = RoundedCornerShape(50), color = SecondaryFixed.copy(alpha = 0.6f)) {
                                Text("3 PERLU RESTOCK", style = MaterialTheme.typography.labelSmall, color = Secondary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                    }

                    item {
                        val criticalShortcuts = listOf(
                            Triple("Minyak Goreng Kita 1L", "Sisa 2 Pouch • Dus isi 12 pouch") {
                                onSelectSku("Minyak Goreng Kita 1L", 174000L, 12, "dus", 16500L)
                            },
                            Triple("Gas LPG 3kg Melon", "Sisa 3 Tabung • Dropping 20 Pcs") {
                                onSelectSku("Gas LPG 3kg Melon", 370000L, 20, "custom", 21000L)
                            },
                            Triple("Korek Api Gas Tokai", "Sisa 4 pcs • Kotak Grosir 50 pcs") {
                                onSelectSku("Korek Api Gas Tokai", 140000L, 50, "custom", 3500L)
                            }
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            criticalShortcuts.forEachIndexed { idx, (name, sub, act) ->
                                Surface(
                                    onClick = act,
                                    shape = RoundedCornerShape(12.dp),
                                    color = SurfaceContainerLow,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(name, style = MaterialTheme.typography.headlineSmall)
                                            Text(sub, style = MaterialTheme.typography.bodySmall, color = ErrorColor)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (idx == 0) Primary else SurfaceContainerLowest
                                        ) {
                                            Text(
                                                text = if (idx == 0) "Hitung Dus" else "Pilih & Hitung",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (idx == 0) Color.White else OnSurface,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Sticky Action Button
                Surface(
                    color = SurfaceContainerLowest,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = {
                                onSelectSku("Minyak Goreng Kita 1L", 174000L, 12, "dus", 16500L)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Primary,
                            modifier = Modifier.fillMaxWidth().height(54.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Gunakan Minyak Goreng Kita", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                    Text("1 Karton Dus (12 Pouch) • 8992775211025", style = MaterialTheme.typography.labelSmall, color = OnPrimaryContainer)
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
