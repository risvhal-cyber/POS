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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentCheckoutScreen(
    viewModel: WarungViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val cartItems by viewModel.cartItems.collectAsState()
    val customerName by viewModel.selectedCustomerName.collectAsState()
    val initialTendered by viewModel.initialTenderedAmount.collectAsState()

    val billAmount = remember(cartItems) {
        val sum = cartItems.sumOf { it.subtotal }
        if (sum > 0) sum else 64000L
    }

    var selectedMethod by remember { mutableStateOf(PaymentMethod.TUNAI) }
    var currentTendered by remember(initialTendered) { mutableStateOf(if (initialTendered > 0L) initialTendered else 100000L) }
    var printThermal by remember { mutableStateOf(true) }
    var sendWhatsapp by remember { mutableStateOf(false) }

    val diff = currentTendered - billAmount
    val isEnough = diff >= 0

    val drawerBreakdown = remember(diff) {
        if (diff <= 0L) emptyList()
        else {
            var rem = diff
            val denoms = listOf(50000L, 20000L, 10000L, 5000L, 2000L, 1000L, 500L)
            val result = mutableListOf<String>()
            for (d in denoms) {
                if (rem >= d) {
                    val count = rem / d
                    rem %= d
                    val suffix = if (d <= 1000L) " (Koin/Lembar)" else ""
                    result.add("${count}x ${formatRupiah(d)}$suffix")
                }
            }
            result
        }
    }

    fun pressDigit(digitStr: String) {
        val curStr = if (currentTendered == 0L) "" else currentTendered.toString()
        val nextStr = curStr + digitStr
        if (nextStr.length <= 9) {
            currentTendered = nextStr.toLongOrNull() ?: 0L
        }
    }

    fun handleBackspace() {
        val str = currentTendered.toString()
        currentTendered = if (str.length <= 1) 0L else str.dropLast(1).toLongOrNull() ?: 0L
    }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                    Text(
                        text = "Pembayaran & Kembalian",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(onClick = { viewModel.showToast("Pilih nominal cepat atau ketik uang pelanggan") }) {
                        Icon(Icons.Outlined.HelpOutline, contentDescription = "Bantuan", tint = OnSurfaceVariant)
                    }
                    ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Bill Header Summary Card
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "#KLT-20250524-0042",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = OnSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = SurfaceContainer
                            ) {
                                Text(
                                    text = "${cartItems.size.coerceAtLeast(4)} ITEM BELANJA",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column {
                            Text("Total Tagihan Belanja", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = formatRupiah(billAmount),
                                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = OnSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PrimaryContainer.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "Lunas saat bayar",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Payment Methods Selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaymentMethodChip(
                                label = "Tunai (Cash)",
                                icon = Icons.Outlined.Payments,
                                selected = selectedMethod == PaymentMethod.TUNAI,
                                onClick = {
                                    selectedMethod = PaymentMethod.TUNAI
                                    currentTendered = 100000L
                                }
                            )
                            PaymentMethodChip(
                                label = "QRIS Toko",
                                icon = Icons.Outlined.QrCode2,
                                selected = selectedMethod == PaymentMethod.QRIS,
                                onClick = {
                                    selectedMethod = PaymentMethod.QRIS
                                    currentTendered = billAmount
                                    viewModel.showToast("Mode QRIS: Nominal diatur sesuai total tagihan")
                                }
                            )
                            PaymentMethodChip(
                                label = "Kasbon / Hutang",
                                icon = Icons.Outlined.MenuBook,
                                selected = selectedMethod == PaymentMethod.KASBON,
                                onClick = {
                                    selectedMethod = PaymentMethod.KASBON
                                    currentTendered = 0L
                                    viewModel.showToast("Mode Kasbon: Tagihan masuk buku piutang")
                                }
                            )
                        }
                    }
                }
            }

            // 2. Cash Input & Tendered Display + Keypad
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "UANG DITERIMA PELANGGAN",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainer)
                                    .clickable { handleBackspace() }
                                    .testTag("payment_backspace_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Backspace, contentDescription = "Hapus digit", modifier = Modifier.size(20.dp))
                            }
                        }

                        // Large Tendered Display
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLow)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rp",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = formatNumberOnly(currentTendered),
                                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = OnSurface
                            )
                        }

                        // Quick Fast Cash Chips (2x3 Grid)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    onClick = { currentTendered = billAmount },
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceContainerLow,
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("Uang Pas", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text(formatNumberOnly(billAmount), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                                    }
                                }
                                FastCashButton(
                                    label = "70.000",
                                    isSelected = currentTendered == 70000L,
                                    onClick = { currentTendered = 70000L },
                                    modifier = Modifier.weight(1f)
                                )
                                FastCashButton(
                                    label = "100.000",
                                    isSelected = currentTendered == 100000L,
                                    onClick = { currentTendered = 100000L },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FastCashButton(
                                    label = "150.000",
                                    isSelected = currentTendered == 150000L,
                                    onClick = { currentTendered = 150000L },
                                    modifier = Modifier.weight(1f)
                                )
                                FastCashButton(
                                    label = "200.000",
                                    isSelected = currentTendered == 200000L,
                                    onClick = { currentTendered = 200000L },
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    onClick = { currentTendered = 0L },
                                    shape = RoundedCornerShape(8.dp),
                                    color = ErrorContainer,
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("Reset (C)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnErrorContainer)
                                    }
                                }
                            }
                        }

                        // Numeric Keypad (4x3 Grid)
                        val rows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("00", "0", ".000")
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            rows.forEach { rowKeys ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowKeys.forEach { key ->
                                        val isSpecial = key == "00" || key == ".000"
                                        Surface(
                                            onClick = {
                                                pressDigit(if (key == ".000") "000" else key)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSpecial) SurfaceContainer else SurfaceContainerLow,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(54.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = key,
                                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = OnSurface
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

            // 3. Automatic Change Indicator Card
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isEnough) PrimaryContainer else ErrorContainer,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val contentColor = if (isEnough) Color.White else OnErrorContainer
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
                                Icon(Icons.Outlined.PriceCheck, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
                                Text("UANG KEMBALIAN", style = MaterialTheme.typography.labelLarge, color = contentColor)
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isEnough) Color.White.copy(alpha = 0.2f) else ErrorColor
                            ) {
                                Text(
                                    text = when {
                                        diff == 0L -> "Uang Pas"
                                        diff > 0L -> "Uang Cukup"
                                        else -> "Kurang Bayar"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = if (isEnough) formatRupiah(diff) else "- ${formatRupiah(Math.abs(diff))}",
                                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = contentColor
                            )
                            Text(
                                text = "Tunai Kasir",
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.9f)
                            )
                        }

                        // Smart Cash Drawer Recommendation Breakdown
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.15f))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.PointOfSale, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Panduan Ambil Uang Laci Kasir:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = contentColor
                                )
                            }
                            if (diff == 0L) {
                                Text(
                                    text = "Tidak ada kembalian (Uang Pas)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = contentColor
                                )
                            } else if (diff > 0L) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    drawerBreakdown.forEach { piece ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White.copy(alpha = 0.22f)
                                        ) {
                                            Text(
                                                text = piece,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Terdapat kekurangan pembayaran sebesar ${formatRupiah(Math.abs(diff))}. Tawarkan catat kasbon.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }

            // 4. Customer & Receipt Settings
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLow)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SecondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = OnSecondaryContainer)
                                }
                                Column {
                                    Text(customerName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Sisa Kasbon Lalu: Rp 0", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Surface(
                                onClick = { viewModel.navigateTo(ScreenRoute.CustomerDirectory) },
                                shape = RoundedCornerShape(6.dp),
                                color = SurfaceContainer
                            ) {
                                Text(
                                    text = "Ganti",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Outlined.Print, contentDescription = null, tint = OnSurfaceVariant)
                                Column {
                                    Text("Cetak Struk Thermal", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Text("Printer Bluetooth 58mm Aktif", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Checkbox(
                                checked = printThermal,
                                onCheckedChange = { printThermal = it },
                                colors = CheckboxDefaults.colors(checkedColor = Primary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Outlined.Chat, contentDescription = null, tint = OnSurfaceVariant)
                                Column {
                                    Text("Kirim Nota ke WhatsApp", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Text("+62 812-3490-8812", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Checkbox(
                                checked = sendWhatsapp,
                                onCheckedChange = { sendWhatsapp = it },
                                colors = CheckboxDefaults.colors(checkedColor = Primary)
                            )
                        }
                    }
                }
            }

            // 5. Complete Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = {
                            if (currentTendered < billAmount && selectedMethod != PaymentMethod.KASBON) {
                                viewModel.showToast("Uang kurang! Tambah nominal atau klik 'Sisa Jadi Kasbon'")
                            } else {
                                viewModel.completeCheckout(selectedMethod, currentTendered)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = PrimaryContainer,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_finish_print")
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
                                Icon(Icons.Outlined.TaskAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                Text("SELESAI & CETAK", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("KEMBALIAN", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.9f))
                                Text(
                                    text = if (isEnough) formatRupiah(diff) else "Kurang ${formatNumberOnly(Math.abs(diff))}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.completeCheckout(selectedMethod, currentTendered) },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tanpa Struk", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Surface(
                            onClick = { viewModel.completeCheckout(PaymentMethod.SPLIT_BON, currentTendered) },
                            shape = RoundedCornerShape(10.dp),
                            color = SecondaryContainer,
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sisa Jadi Kasbon", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnSecondaryContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Primary else SurfaceContainerHigh,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else OnSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun FastCashButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) PrimaryContainer else SurfaceContainerLow,
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.White else OnSurface
            )
        }
    }
}
