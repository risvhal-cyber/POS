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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasbonLedgerScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val customers by viewModel.customers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var paymentTargetCustomer by remember { mutableStateOf<CustomerDebt?>(null) }
    var showAddKasbonSheet by remember { mutableStateOf(false) }

    val totalPiutang = customers.sumOf { it.debtAmount }.let { if (it > 0) it else 1485000L }
    val activeDebtorCount = customers.count { !it.isPaid }.coerceAtLeast(3)

    val filteredCustomers = remember(customers, searchQuery, selectedFilter) {
        customers.filter { c ->
            val matchesSearch = searchQuery.isBlank() ||
                    c.name.contains(searchQuery, ignoreCase = true) ||
                    c.addressPhone.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Belum Lunas" -> !c.isPaid
                "Jatuh Tempo" -> c.isOverdue || c.dueBadge.contains("Besok")
                "Sudah Lunas" -> c.isPaid
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.selectTab(MainTab.KASIR) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                    Text(
                        text = "Buku Kasbon Pelanggan",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(onClick = { viewModel.showToast("Catat & tagih kasbon tetangga secara berkala") }) {
                        Icon(Icons.Outlined.HelpOutline, contentDescription = "Bantuan", tint = OnSurfaceVariant)
                    }
                    ProfileAvatarButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.Settings) },
                        icon = Icons.Outlined.Storefront
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ringkasan Kasbon Summary Card
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
                                Text("TOTAL PIUTANG BELUM LUNAS", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = formatRupiah(totalPiutang),
                                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold)
                                    )
                                    Surface(shape = RoundedCornerShape(50), color = PrimaryFixed.copy(alpha = 0.4f)) {
                                        Text(
                                            text = "8 Pelanggan",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrimaryFixed.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.MenuBook, contentDescription = null, tint = Primary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                        Text("Terbayar Bulan Ini", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                    }
                                    Text("Rp 820.000", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SecondaryFixed.copy(alpha = 0.35f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                                        Text("Perlu Ditagih", style = MaterialTheme.typography.labelSmall, color = Secondary)
                                    }
                                    Text("$activeDebtorCount Orang Jatuh Tempo", style = MaterialTheme.typography.labelLarge, color = OnSecondaryFixed, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondaryFixed.copy(alpha = 0.3f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Catat & tagih secara berkala untuk kelancaran arus kas toko.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSecondaryContainer
                            )
                        }
                    }
                }
            }

            // Search & Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = OutlineColor, modifier = Modifier.size(20.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text("Cari nama tetangga/pelanggan kasbon...", style = MaterialTheme.typography.bodySmall, color = OutlineColor)
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("Semua", "Semua (8)"),
                            Pair("Belum Lunas", "Belum Lunas (5)"),
                            Pair("Jatuh Tempo", "● Jatuh Tempo (3)"),
                            Pair("Sudah Lunas", "Sudah Lunas")
                        ).forEach { (key, label) ->
                            val isSel = selectedFilter == key
                            Surface(
                                onClick = { selectedFilter = key },
                                shape = RoundedCornerShape(50),
                                color = if (isSel) Primary else SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) Color.White else OnSurface,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daftar Buku Hutang", style = MaterialTheme.typography.headlineSmall)
                    Text("Urutkan: Jatuh Tempo", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }

            // Customer Debt Cards
            items(filteredCustomers.size) { index ->
                val cust = filteredCustomers[index]
                KasbonCustomerCard(
                    cust = cust,
                    onTagihWa = { viewModel.showToast("Kirim pengingat WhatsApp ramah ke ${cust.name}...") },
                    onPayClick = { paymentTargetCustomer = cust }
                )
            }

            // Bottom Action Bar: + Catat Kasbon Baru | Daftar Pelanggan | Unduh Rekap
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddKasbonSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_open_new_kasbon")
                    ) {
                        Icon(Icons.Outlined.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ Catat Kasbon Baru", style = MaterialTheme.typography.labelLarge)
                    }

                    Surface(
                        onClick = { viewModel.navigateTo(ScreenRoute.CustomerDirectory) },
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(50.dp)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .testTag("btn_customer_directory")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Group, contentDescription = "Daftar Pelanggan Tetangga", tint = OnSurface)
                        }
                    }

                    Surface(
                        onClick = { viewModel.showToast("Menyiapkan laporan Buku Bon (.PDF / Excel)...") },
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(50.dp)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.FileDownload, contentDescription = "Unduh Rekap", tint = OnSurface)
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheet: Catat Pembayaran Kasbon
    paymentTargetCustomer?.let { target ->
        var payInputText by remember(target) { mutableStateOf(target.debtAmount.toString()) }
        ModalBottomSheet(
            onDismissRequest = { paymentTargetCustomer = null },
            containerColor = SurfaceContainerLowest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Catat Pembayaran Kasbon", style = MaterialTheme.typography.headlineSmall)
                        Text(target.name, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    IconButton(onClick = { paymentTargetCustomer = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryFixed.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Hutang Tercatat:", style = MaterialTheme.typography.bodySmall)
                        Text(formatRupiah(target.debtAmount), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Primary)
                    }
                }

                OutlinedTextField(
                    value = payInputText,
                    onValueChange = { payInputText = it.filter { c -> c.isDigit() } },
                    label = { Text("Jumlah yang Dibayar Sekarang (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { payInputText = target.debtAmount.toString() },
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryFixed
                    ) {
                        Text("Bayar Penuh (Lunas)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = OnPrimaryFixed, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                    Surface(
                        onClick = { payInputText = "50000" },
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHigh
                    ) {
                        Text("Rp 50.000", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                    Surface(
                        onClick = { payInputText = "100000" },
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHigh
                    ) {
                        Text("Rp 100.000", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }

                Button(
                    onClick = {
                        val amt = payInputText.toLongOrNull() ?: 0L
                        if (amt > 0) {
                            viewModel.payCustomerDebt(target.id, amt)
                            paymentTargetCustomer = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Simpan Pembayaran & Cetak Bukti", style = MaterialTheme.typography.headlineSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Bottom Sheet: Tambah Kasbon Baru
    if (showAddKasbonSheet) {
        var newName by remember { mutableStateOf("") }
        var newItems by remember { mutableStateOf("") }
        var newAmountText by remember { mutableStateOf("") }
        var newDueDate by remember { mutableStateOf("Minggu Depan") }

        ModalBottomSheet(
            onDismissRequest = { showAddKasbonSheet = false },
            containerColor = SurfaceContainerLowest
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tambah Kasbon Baru", style = MaterialTheme.typography.headlineSmall)
                        Text("Tulis nama tetangga & barang belanjaan", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    IconButton(onClick = { showAddKasbonSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nama Pelanggan / Tetangga") },
                    placeholder = { Text("Contoh: Bu Hj. Maryam, Mas Bagus...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newItems,
                    onValueChange = { newItems = it },
                    label = { Text("Rincian Barang yang Diambil") },
                    placeholder = { Text("Contoh: Beras Ramos 5kg, Minyak 2L...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newAmountText,
                    onValueChange = { newAmountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Kasbon (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newDueDate,
                    onValueChange = { newDueDate = it },
                    label = { Text("Janji Bayar / Jatuh Tempo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val amt = newAmountText.toLongOrNull() ?: 50000L
                        viewModel.recordNewKasbon(newName, newItems, amt, newDueDate)
                        showAddKasbonSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Simpan ke Buku Kasbon", style = MaterialTheme.typography.headlineSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun KasbonCustomerCard(
    cust: CustomerDebt,
    onTagihWa: () -> Unit,
    onPayClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (cust.isOverdue) ErrorContainer.copy(alpha = 0.25f) else SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (cust.isOverdue) Modifier.border(1.dp, ErrorColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (cust.isOverdue) ErrorContainer else PrimaryFixed.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cust.initials,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (cust.isOverdue) OnErrorContainer else Primary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(cust.name, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when {
                                    cust.isPaid -> PrimaryFixed
                                    cust.isOverdue -> ErrorContainer
                                    else -> SecondaryFixed
                                }
                            ) {
                                Text(
                                    text = cust.statusBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = when {
                                        cust.isPaid -> Primary
                                        cust.isOverdue -> ErrorColor
                                        else -> Secondary
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "📍 ${cust.addressPhone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (cust.isPaid) "Sisa Hutang" else "Total Bon",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = formatRupiah(cust.debtAmount),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (cust.isPaid) Primary else ErrorColor
                    )
                }
            }

            // Item Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerLow)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cust.lastItems, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                    Text(
                        text = cust.lastTakenTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cust.isOverdue) ErrorColor else OnSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (cust.isOverdue) ErrorContainer else SecondaryFixed.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = cust.dueBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (cust.isOverdue) ErrorColor else Secondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (!cust.isPaid) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = onTagihWa,
                        shape = RoundedCornerShape(10.dp),
                        color = if (cust.isOverdue) Primary else SurfaceContainerLowest,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .then(
                                if (!cust.isOverdue) Modifier.border(1.dp, Primary, RoundedCornerShape(10.dp))
                                else Modifier
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Chat, contentDescription = null, tint = if (cust.isOverdue) Color.White else Primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (cust.isOverdue) "Tagih WA Sekarang" else "Tagih WA",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (cust.isOverdue) Color.White else Primary
                            )
                        }
                    }

                    Surface(
                        onClick = onPayClick,
                        shape = RoundedCornerShape(10.dp),
                        color = Primary,
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Catat Bayar / Cicil", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDirectoryScreen(
    viewModel: WarungViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val customers by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua (28)") }
    var showAddNeighborSheet by remember { mutableStateOf(false) }

    val filteredList = remember(customers, searchQuery, selectedFilter) {
        customers.filter { c ->
            val matchesQuery = searchQuery.isBlank() ||
                    c.name.contains(searchQuery, ignoreCase = true) ||
                    c.addressPhone.contains(searchQuery, ignoreCase = true)
            val matchesCat = when {
                selectedFilter.startsWith("Ada Kasbon") -> !c.isPaid
                selectedFilter.startsWith("Langganan") -> c.categoryTag == "Tetap" || c.categoryTag == "Grosir"
                else -> true
            }
            matchesQuery && matchesCat
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                        AsyncImage(
                            model = WarungImages.LOGO,
                            contentDescription = "Logo",
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = "Daftar Pelanggan Tetangga",
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { showAddNeighborSheet = true },
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryContainer)
                        ) {
                            Icon(Icons.Outlined.PersonAdd, contentDescription = "Tambah Pelanggan", tint = Color.White)
                        }
                        ProfileAvatarButton(onClick = { viewModel.navigateTo(ScreenRoute.Settings) })
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 3 Metrics Row: 28 Total Warga | 8 Ada Kasbon | 12 Prioritas
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(14.dp), color = SurfaceContainerLowest, shadowElevation = 1.dp, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Outlined.Groups, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                Text("28", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                                Text("Total Warga", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(14.dp), color = SurfaceContainerLowest, shadowElevation = 1.dp, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                                Text("8", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = Secondary)
                                Text("Ada Kasbon", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }
                        Surface(shape = RoundedCornerShape(14.dp), color = SurfaceContainerLowest, shadowElevation = 1.dp, modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Outlined.Stars, contentDescription = null, tint = Tertiary, modifier = Modifier.size(20.dp))
                                Text("12", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                                Text("Prioritas", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }
                    }
                }

                // Search & Filter Pills
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari nama warga, gang, atau telepon...") },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Semua (28)", "Ada Kasbon (8)", "Langganan Tetap (15)").forEach { pill ->
                                val isSel = selectedFilter == pill
                                Surface(
                                    onClick = { selectedFilter = pill },
                                    shape = RoundedCornerShape(50),
                                    color = if (isSel) PrimaryContainer else SurfaceContainerLowest,
                                    shadowElevation = 1.dp
                                ) {
                                    Text(
                                        text = pill,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSel) Color.White else OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Neighbor Cards
                items(filteredList.size) { index ->
                    val neighbor = filteredList[index]
                    NeighborDirectoryCard(
                        neighbor = neighbor,
                        onPrimaryAction = {
                            if (neighbor.isPaid) {
                                viewModel.selectTab(MainTab.KASIR)
                            } else {
                                viewModel.payCustomerDebt(neighbor.id, neighbor.debtAmount)
                            }
                        },
                        onSecondaryAction = {
                            viewModel.showToast("Menghubungi ${neighbor.name} via WhatsApp...")
                        }
                    )
                }
            }
        }

        // Sticky Bottom Button: + Tambah Tetangga Baru
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showAddNeighborSheet = true },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Icon(Icons.Outlined.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ Tambah Tetangga Baru", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }

    if (showAddNeighborSheet) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var limitText by remember { mutableStateOf("150000") }

        ModalBottomSheet(
            onDismissRequest = { showAddNeighborSheet = false },
            containerColor = SurfaceContainerLowest
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Data Tetangga Baru", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap / Panggilan Warga *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor WhatsApp Aktif *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Alamat Rumah / Patokan *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it.filter { c -> c.isDigit() } },
                    label = { Text("Batas Maksimal Plafon Bon (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addNewNeighborCustomer(name, phone, address, limitText.toLongOrNull() ?: 150000L)
                            showAddNeighborSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Simpan Tetangga", style = MaterialTheme.typography.headlineSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun NeighborDirectoryCard(
    neighbor: CustomerDebt,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
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
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    NetworkProductImage(
                        url = neighbor.avatarUrl,
                        contentDescription = neighbor.name,
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(neighbor.name, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (neighbor.name.contains("Rohanah")) {
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                            } else {
                                Surface(shape = RoundedCornerShape(4.dp), color = Primary.copy(alpha = 0.1f)) {
                                    Text(neighbor.categoryTag, style = MaterialTheme.typography.labelSmall, color = Primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text(neighbor.addressPhone, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (neighbor.isPaid) "Status Bersih" else "Kasbon Berjalan",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (neighbor.isPaid) Primary else OnSurfaceVariant
                    )
                    Text(
                        text = if (neighbor.isPaid) "Rp 0" else formatRupiah(neighbor.debtAmount),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            neighbor.isPaid -> OnSurface
                            neighbor.isOverdue -> ErrorColor
                            else -> Secondary
                        }
                    )
                }
            }

            if (neighbor.friendlyAlert != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ErrorContainer.copy(alpha = 0.6f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(18.dp))
                    Text(neighbor.friendlyAlert, style = MaterialTheme.typography.bodySmall, color = OnErrorContainer)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = neighbor.lastItems,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(neighbor.dueBadge, style = MaterialTheme.typography.labelSmall, color = Primary)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onSecondaryAction,
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceContainer,
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Chat, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim WA", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Surface(
                    onClick = onPrimaryAction,
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryContainer,
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (neighbor.isPaid) Icons.Outlined.PointOfSale else Icons.Outlined.Payments,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (neighbor.isPaid) "Buka Kasir" else "Catat Bayar",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
