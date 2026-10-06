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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel
import java.util.Locale

@Composable
fun ManualInputSuiteScreen(
    initialTab: Int,
    viewModel: WarungViewModel
) {
    BackHandler { viewModel.navigateBack() }

    var activeTab by remember { mutableIntStateOf(initialTab) }
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount = cartItems.size
    val cartTotal = cartItems.sumOf { it.subtotal }

    Column(
        modifier = Modifier
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
                    .padding(horizontal = 8.dp),
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
                            text = when (activeTab) {
                                0 -> "Input Manual & Eceran / Timbangan"
                                1 -> "Input Manual / Ketik Bebas Rp"
                                else -> "Transaksi Gas & Galon Cepat"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = when (activeTab) {
                                2 -> "Tukar Tabung, Isi Ulang, & Titip Tabung"
                                else -> "Warung Sembako & Kelontong"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    CartHeaderBadgeButton(
                        itemCount = cartCount,
                        totalAmount = cartTotal,
                        onClick = { viewModel.navigateTo(ScreenRoute.CartDetail) }
                    )
                    ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                }
            }
        }

        // Segmented Mode Switcher (3 Modes)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModeTabButton(
                    title = "Eceran / Curah",
                    icon = Icons.Outlined.Scale,
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                ModeTabButton(
                    title = "Ketik Bebas Rp",
                    icon = Icons.Outlined.Payments,
                    selected = activeTab == 1,
                    badge = "KILAT",
                    onClick = { activeTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                ModeTabButton(
                    title = "Gas & Galon",
                    icon = Icons.Outlined.PropaneTank,
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Sub-Screen Content
        Box(modifier = Modifier.weight(1f)) {
            when (activeTab) {
                0 -> EceranCurahTabContent(viewModel)
                1 -> KetikBebasRpTabContent(viewModel)
                2 -> GasGallonTabContent(viewModel)
            }
        }
    }
}

@Composable
private fun ModeTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Primary else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp,
        modifier = modifier.height(42.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else OnSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (selected) Color.White else OnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (badge != null && selected) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SecondaryContainer
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = OnSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EceranCurahTabContent(viewModel: WarungViewModel) {
    val presets = viewModel.curahPresets
    var selectedPreset by remember { mutableStateOf(presets[4]) } // Tepung Terigu (Rp 12.000) as in Image 13
    var currentWeight by remember { mutableDoubleStateOf(1.50) }
    var selectedUnit by remember { mutableStateOf("Kilogram (Kg)") }
    val selectedPackings = remember { mutableStateListOf<String>() }

    val subtotal = Math.round(currentWeight * selectedPreset.ratePerUnit)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Section 1: Pilih Sembako Curah (2x3 Grid)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                        Text("Pilih Sembako Curah", style = MaterialTheme.typography.headlineSmall)
                    }
                    Text("6 Item Cepat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { item ->
                                val isSel = selectedPreset.id == item.id
                                Surface(
                                    onClick = { selectedPreset = item },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSel) Primary else SurfaceContainerLowest,
                                    shadowElevation = if (isSel) 4.dp else 1.dp,
                                    modifier = Modifier.weight(1f).height(94.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.headlineSmall,
                                                color = if (isSel) Color.White else OnSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (item.badge != null && !isSel) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (item.badge.contains("ons")) ErrorContainer else SurfaceContainer
                                                ) {
                                                    Text(
                                                        text = item.badge,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = if (item.badge.contains("ons")) OnErrorContainer else OnSurfaceVariant,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = if (isSel) Icons.Filled.CheckCircle else Icons.Outlined.AddCircleOutline,
                                                    contentDescription = null,
                                                    tint = if (isSel) PrimaryFixed else OutlineVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = formatRupiah(item.ratePerUnit),
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSel) PrimaryFixed else OnSurface
                                            )
                                            Text(
                                                text = "per Kilogram (${item.unit})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSel) OnPrimaryContainer else OnSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Kalkulator Timbangan & Kuantitas
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Unit Pills
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Kilogram (Kg)", "Gram (gr)", "Ons (100gr)", "Bungkus / Kantong").forEach { u ->
                                val isUnitSel = selectedUnit == u
                                Surface(
                                    onClick = { selectedUnit = u },
                                    shape = RoundedCornerShape(50),
                                    color = if (isUnitSel) Primary else SurfaceContainer
                                ) {
                                    Text(
                                        text = u,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isUnitSel) Color.White else OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        // Scale Display Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainerLow)
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TIMBANGAN TERBACA: ${selectedPreset.name.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OnSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.2f", currentWeight),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = OnSurface
                                )
                                Text(
                                    text = selectedPreset.unit,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Primary
                                )
                            }
                            Text(
                                text = "Tarif Patokan: ${formatRupiah(selectedPreset.ratePerUnit)} / ${selectedPreset.unit}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    onClick = { currentWeight = (currentWeight - 0.25).coerceAtLeast(0.10) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceContainerHighest,
                                    modifier = Modifier.weight(1f).height(50.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("—  - 0.25 Kg", style = MaterialTheme.typography.headlineSmall)
                                    }
                                }
                                Surface(
                                    onClick = { currentWeight += 0.25 },
                                    shape = RoundedCornerShape(10.dp),
                                    color = PrimaryFixed,
                                    modifier = Modifier.weight(1f).height(50.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("+  + 0.25 Kg", style = MaterialTheme.typography.headlineSmall, color = OnPrimaryFixed)
                                    }
                                }
                            }
                        }

                        // Quick Weight Chips
                        Text("TAMBAH BERAT CEPAT", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        val addWeights = listOf(
                            Pair("+ 1/4 Kg", 0.25),
                            Pair("+ 1/2 Kg", 0.50),
                            Pair("+ 1 Kg", 1.0),
                            Pair("+ 2 Kg", 2.0),
                            Pair("+ 5 Kg", 5.0),
                            Pair("+ 10 Kg", 10.0)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            addWeights.chunked(3).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowItems.forEach { (lbl, w) ->
                                        val is10Kg = w == 10.0
                                        Surface(
                                            onClick = { currentWeight += w },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (is10Kg) SecondaryFixed else SurfaceContainer,
                                            modifier = Modifier.weight(1f).height(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = lbl,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = if (is10Kg) OnSecondaryFixed else OnSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Direct Rupiah Request
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("PELANGGAN BELI UANG PAS (RUPIAH)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            Text("Otomatis Hitung Berat", style = MaterialTheme.typography.labelSmall, color = Primary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(10000L, 20000L, 50000L).forEach { rp ->
                                Surface(
                                    onClick = {
                                        currentWeight = rp.toDouble() / selectedPreset.ratePerUnit.toDouble()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceContainerHigh,
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(formatRupiah(rp), style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Instruksi Kemasan & Catatan
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                        Text("Instruksi Kemasan & Catatan", style = MaterialTheme.typography.headlineSmall)
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Plastik 1 Kg-an", "Bawa Karung Sendiri", "Cabe / Gorengan Campur", "Titipan Tetangga / Kasbon").forEach { pack ->
                            val isChecked = selectedPackings.contains(pack)
                            Surface(
                                onClick = {
                                    if (isChecked) selectedPackings.remove(pack) else selectedPackings.add(pack)
                                },
                                shape = RoundedCornerShape(50),
                                color = if (isChecked) SecondaryFixed else SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isChecked) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(pack, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Rincian Perhitungan & Action Buttons
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 32.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainerHighest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Rincian Perhitungan", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", currentWeight)} ${selectedPreset.unit} × ${formatRupiah(selectedPreset.ratePerUnit)}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Tagihan", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                Text(
                                    text = formatRupiah(subtotal),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Primary
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = {
                            viewModel.addToCart(selectedPreset.name, selectedPreset.ratePerUnit, currentWeight, selectedPreset.unit, "Sembako Curah")
                            viewModel.completeCheckout(PaymentMethod.TUNAI, subtotal)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryContainer,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Payments, contentDescription = null, tint = OnSecondaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Langsung Bayar Uang Pas (${formatRupiah(subtotal)})",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSecondaryContainer
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            viewModel.addToCart(selectedPreset.name, selectedPreset.ratePerUnit, currentWeight, selectedPreset.unit, "Sembako Curah")
                            viewModel.navigateBack()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = Primary,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.ShoppingCartCheckout, contentDescription = null, tint = Color.White)
                                Text("+ Masukkan Keranjang", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                            }
                            Text(formatRupiah(subtotal), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = PrimaryFixed)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KetikBebasRpTabContent(viewModel: WarungViewModel) {
    var currentRawAmount by remember { mutableLongStateOf(15000L) }
    var currentQuantity by remember { mutableIntStateOf(1) }
    var isEnteringQty by remember { mutableStateOf(false) }
    var currentCategory by remember { mutableStateOf("Gorengan / Kerupuk") }
    var customNote by remember { mutableStateOf("") }

    val totalAmount = currentRawAmount * currentQuantity

    fun handleKeyPress(key: String) {
        if (isEnteringQty) {
            val q = key.toIntOrNull() ?: 1
            if (q > 0) currentQuantity = q
            isEnteringQty = false
            viewModel.showToast("Kuantitas diatur: ${currentQuantity}x")
        } else {
            val curStr = if (currentRawAmount == 0L) "" else currentRawAmount.toString()
            val next = curStr + key
            if (next.length <= 9) {
                currentRawAmount = next.toLongOrNull() ?: 0L
            }
        }
    }

    fun handleBackspace() {
        if (isEnteringQty) {
            currentQuantity = 1
            isEnteringQty = false
            return
        }
        val str = currentRawAmount.toString()
        currentRawAmount = if (str.length > 1) str.dropLast(1).toLongOrNull() ?: 0L else 0L
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 165.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Giant Display Nominal Rupiah
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryContainer))
                                Text("Input Manual Cepat", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                            }
                            if (currentQuantity > 1) {
                                Surface(shape = RoundedCornerShape(6.dp), color = SurfaceContainerHigh) {
                                    Text(
                                        text = "Qty: ${currentQuantity}x",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.weight(1f)) {
                                Text("Rp ", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = Primary)
                                Text(
                                    text = formatNumberOnly(currentRawAmount),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = OnSurface
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLow)
                                    .clickable { handleBackspace() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Backspace, contentDescription = "Hapus Digit")
                            }
                        }

                        Text(
                            text = "Item belanja bebas tanpa barcode / eceran harian",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // Nominal Kerap Dibeli Presets
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("NOMINAL KERAP DIBELI", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text("Sekali Sentuh", style = MaterialTheme.typography.labelSmall, color = Tertiary)
                    }

                    val presets = listOf(
                        Triple("+1.000", "Permen / Krupuk", 1000L),
                        Triple("+2.000", "Gorengan / Es", 2000L),
                        Triple("+5.000", "Bumbu / Sayur", 5000L),
                        Triple("+10.000", "Telur / Beras Ltr", 10000L),
                        Triple("+20.000", "Minyak Goreng", 20000L),
                        Triple("+50.000", "Beras Karungan", 50000L)
                    )
                    presets.chunked(3).forEach { rowList ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowList.forEach { (numStr, desc, addVal) ->
                                Surface(
                                    onClick = { currentRawAmount += addVal },
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceContainerLowest,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(numStr, style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = Primary)
                                        Text(desc, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Label Kategori Barang
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("LABEL KATEGORI BARANG", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text(currentCategory, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                    }

                    val catOptions = listOf(
                        "Gorengan / Kerupuk",
                        "Sayur & Bumbu Dapur",
                        "Minuman / Es Lilin",
                        "Jajanan Anak / Snack",
                        "Bawang & Cabai",
                        "Rokok Batangan",
                        "Titipan Tetangga",
                        "Lain-lain"
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        catOptions.forEach { cat ->
                            val isSelected = currentCategory == cat
                            Surface(
                                onClick = { currentCategory = cat },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PrimaryContainer else SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else OnSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    // Optional Note
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.EditNote, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (customNote.isEmpty()) {
                                    Text("Catatan opsional (cth: 3 bungkus pedas)", style = MaterialTheme.typography.bodySmall, color = OutlineColor)
                                }
                                BasicTextField(
                                    value = customNote,
                                    onValueChange = { customNote = it },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = OnSurface),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // 4x4 Numeric Keypad
            item {
                val keypadRows = listOf(
                    listOf("1", "2", "3", "C"),
                    listOf("4", "5", "6", "+"),
                    listOf("7", "8", "9", "BS"),
                    listOf("00", "0", ".000", "×")
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    keypadRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                val bg = when (key) {
                                    "C" -> ErrorContainer
                                    "+", "BS" -> SurfaceContainerHigh
                                    ".000" -> SurfaceContainer
                                    "×" -> SecondaryFixed
                                    else -> SurfaceContainerLowest
                                }
                                val fg = when (key) {
                                    "C" -> OnErrorContainer
                                    "+", ".000" -> Primary
                                    "×" -> OnSecondaryFixed
                                    else -> OnSurface
                                }
                                Surface(
                                    onClick = {
                                        when (key) {
                                            "C" -> {
                                                currentRawAmount = 0L
                                                currentQuantity = 1
                                                isEnteringQty = false
                                            }
                                            "+" -> currentRawAmount += 1000L
                                            "BS" -> handleBackspace()
                                            ".000" -> handleKeyPress("000")
                                            "×" -> {
                                                isEnteringQty = true
                                                viewModel.showToast("Tekan angka (1-9) untuk jumlah barang")
                                            }
                                            else -> handleKeyPress(key)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = bg,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.weight(1f).height(52.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (key == "BS") {
                                            Icon(Icons.Outlined.Backspace, contentDescription = "Backspace", tint = fg)
                                        } else {
                                            Text(
                                                text = key,
                                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                                color = fg
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sticky Dock
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                        Text(
                            text = "${if (currentQuantity > 1) "${currentQuantity}x" else "1 Item Bebas"} ($currentCategory)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = formatRupiah(totalAmount),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                }

                Surface(
                    onClick = {
                        if (totalAmount > 0) {
                            viewModel.addToCart(currentCategory, currentRawAmount, currentQuantity.toDouble(), "pcs", "Eceran")
                            viewModel.completeCheckout(PaymentMethod.TUNAI, totalAmount)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = SecondaryContainer,
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Bolt, contentDescription = null, tint = OnSecondaryContainer)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Langsung Bayar Pas (${formatRupiah(totalAmount)})",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSecondaryContainer
                        )
                    }
                }

                Surface(
                    onClick = {
                        if (totalAmount > 0) {
                            viewModel.addToCart(currentCategory, currentRawAmount, currentQuantity.toDouble(), "pcs", "Eceran", note = customNote.ifBlank { null })
                            viewModel.navigateBack()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryContainer,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_add_manual_to_cart")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.ShoppingCartCheckout, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ Masukkan Keranjang (${formatRupiah(totalAmount)})",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GasGallonTabContent(viewModel: WarungViewModel) {
    val catalog = viewModel.gasGallonCatalog
    var filterType by remember { mutableStateOf("Gas LPG") } // "Gas LPG", "Galon Air", "Semua"
    var selectedProduct by remember { mutableStateOf(catalog.first()) }
    var operationMode by remember { mutableIntStateOf(0) } // 0: Tukar Tabung, 1: Beli Baru, 2: Pinjam Kasbon
    var quantity by remember { mutableIntStateOf(2) }
    var deliverToHome by remember { mutableStateOf(false) }
    var installRegulator by remember { mutableStateOf(false) }
    var orderNote by remember { mutableStateOf("") }

    val unitPrice = if (operationMode == 1) selectedProduct.newCylinderPrice else selectedProduct.refillPrice
    val deliveryFee = if (deliverToHome) 3000L * quantity else 0L
    val installFee = if (installRegulator) 2000L else 0L
    val totalOrder = (unitPrice * quantity) + deliveryFee + installFee

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 180.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sub-filter Gas LPG / Galon Air / Semua
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Gas LPG", "Galon Air", "Semua").forEach { tab ->
                        val isSel = filterType == tab
                        Surface(
                            onClick = { filterType = tab },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Primary else Color.Transparent,
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) Color.White else OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Pantauan Stok Fisik Toko Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
                                Text("PANTAUAN STOK FISIK TOKO", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                            Surface(shape = RoundedCornerShape(50), color = PrimaryFixed) {
                                Text(
                                    text = "✓ Siap Jual",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnPrimaryFixed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainer,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.PropaneTank, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text("LPG 3kg Melon", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text("14 Isi • 8 Kosong", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                                    }
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainer,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Tertiary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text("Galon Aqua 19L", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text("18 Isi • 12 Kosong", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6 Product Cards Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pilih Produk Tabung / Galon", style = MaterialTheme.typography.headlineSmall)
                    Text("6 Item Aktif", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }

                val filteredList = if (filterType == "Semua") catalog else catalog // Show all 6 as in Image 19
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredList.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { prod ->
                                val isSel = selectedProduct.id == prod.id
                                Surface(
                                    onClick = { selectedProduct = prod },
                                    shape = RoundedCornerShape(14.dp),
                                    color = SurfaceContainerLowest,
                                    shadowElevation = if (isSel) 4.dp else 1.dp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(
                                            if (isSel) Modifier.border(2.5.dp, Primary, RoundedCornerShape(14.dp))
                                            else Modifier
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(92.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        ) {
                                            NetworkProductImage(
                                                url = prod.imageUrl,
                                                contentDescription = prod.name,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            if (prod.isBestSeller) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = SecondaryContainer,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(6.dp)
                                                ) {
                                                    Text(
                                                        text = "TERLARIS",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = OnSecondaryContainer,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(prod.name, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(prod.subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(SurfaceContainerLow)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = formatRupiah(prod.refillPrice),
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSel) Primary else OnSurface
                                            )
                                            Text(
                                                text = "● Stok ${prod.stockFilled} ${if (prod.unitType.contains("Gas")) "Tabung" else "Galon"}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Jenis Operasi Tabung
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jenis Operasi Tabung", style = MaterialTheme.typography.headlineSmall)
                            Text("Wajib Pilih", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                        }

                        val ops = listOf(
                            Triple("Tukar Tabung (Refill)", formatRupiah(selectedProduct.refillPrice), "Pelanggan bawa tabung kosong, bawa pulang tabung isi."),
                            Triple("Beli Tabung + Isi Baru", formatRupiah(selectedProduct.newCylinderPrice), "Pembeli belum ada tabung (beli kepemilikan tabung baja baru)."),
                            Triple("Pinjam Tabung Dulu (Kasbon)", "Catat Hutang", "Ambil tabung isi sekarang, tabung kosong diserahkan menyusul.")
                        )
                        ops.forEachIndexed { idx, (title, rightLabel, desc) ->
                            val isSelected = operationMode == idx
                            Surface(
                                onClick = { operationMode = idx },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryFixed.copy(alpha = 0.35f) else SurfaceContainerLow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, Primary, RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) Primary else OutlineColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(title, style = MaterialTheme.typography.labelLarge)
                                            Text(rightLabel, style = MaterialTheme.typography.labelLarge, color = if (idx == 2) Secondary else Primary)
                                        }
                                        Text(desc, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Jumlah Pembelian Stepper
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jumlah Pembelian", style = MaterialTheme.typography.headlineSmall)
                            Text("Maksimal ${selectedProduct.stockFilled} tabung", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerHighest)
                                    .clickable { if (quantity > 1) quantity-- },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Remove, contentDescription = "Kurangi")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = quantity.toString(),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Text(
                                    text = selectedProduct.name.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Primary)
                                    .clickable { if (quantity < selectedProduct.stockFilled) quantity++ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = "Tambah", tint = Color.White)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 5, 10).forEach { q ->
                                Surface(
                                    onClick = { quantity = q.coerceAtMost(selectedProduct.stockFilled) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (quantity == q) PrimaryFixed else SurfaceContainer
                                ) {
                                    Text(
                                        text = if (q == 10) "+10 (Katering)" else "+$q Tabung",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (quantity == q) OnPrimaryFixed else OnSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Layanan Antar & Pasang
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Layanan Antar & Pasang", style = MaterialTheme.typography.headlineSmall)
                            Text("Layanan Warung", style = MaterialTheme.typography.labelSmall, color = Tertiary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLow)
                                .clickable { deliverToHome = !deliverToHome }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.TwoWheeler, contentDescription = null, tint = OnSurfaceVariant)
                                Column {
                                    Text("Diantar ke Rumah", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Ongkir kurir warung (+Rp 3.000/tabung)", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Checkbox(checked = deliverToHome, onCheckedChange = { deliverToHome = it })
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLow)
                                .clickable { installRegulator = !installRegulator }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Outlined.Build, contentDescription = null, tint = OnSurfaceVariant)
                                Column {
                                    Text("Bantu Pasang Regulator", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Jasa pasang & cek kebocoran (+Rp 2.000)", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Checkbox(checked = installRegulator, onCheckedChange = { installRegulator = it })
                        }

                        OutlinedTextField(
                            value = orderNote,
                            onValueChange = { orderNote = it },
                            label = { Text("Catatan Alamat / Pembeli (Opsional)") },
                            placeholder = { Text("Cth: Bu Joko Blok B3 / Kasbon dulu") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Bottom Transactional Dock
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "● ${quantity}x ${selectedProduct.name} (${if (operationMode == 0) "Tukar Tabung" else "Beli Baru"})",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Total: ${formatRupiah(totalOrder)}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            viewModel.addToCart(selectedProduct.name, unitPrice, quantity.toDouble(), "tabung", "Gas & Galon", selectedProduct.imageUrl)
                            viewModel.completeCheckout(PaymentMethod.TUNAI, totalOrder)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = SecondaryContainer,
                        modifier = Modifier.weight(5f).height(54.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("⚡ Uang Pas", style = MaterialTheme.typography.labelLarge, color = OnSecondaryContainer)
                            Text(formatRupiah(totalOrder), style = MaterialTheme.typography.labelSmall, color = OnSecondaryContainer)
                        }
                    }

                    Surface(
                        onClick = {
                            viewModel.addToCart(selectedProduct.name, unitPrice, quantity.toDouble(), "tabung", "Gas & Galon", selectedProduct.imageUrl, orderNote.ifBlank { null })
                            viewModel.navigateTo(ScreenRoute.CartDetail)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = Primary,
                        shadowElevation = 4.dp,
                        modifier = Modifier.weight(7f).height(54.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.ShoppingCartCheckout, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Proses Kasir", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
