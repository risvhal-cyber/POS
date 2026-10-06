package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.components.CartHeaderBadgeButton
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StokManagementScreen(
    viewModel: WarungViewModel,
    modifier: Modifier = Modifier
) {
    val stockItems by viewModel.stockItems.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val mutationLogs by viewModel.stockMutationLogs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Semua") }
    var showManageCategorySheet by remember { mutableStateOf(false) }
    var showAddManualStockSheet by remember { mutableStateOf(false) }
    var preselectedStockItem by remember { mutableStateOf<StockItem?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val criticalItems = stockItems.filter { it.isCritical }
    val routineItems = stockItems.filter { !it.isCritical }

    val totalAssetValue = remember(stockItems) {
        stockItems.sumOf { Math.round(it.stockQty * it.wholesalePrice) }
    }

    val filteredCritical = remember(criticalItems, searchQuery, selectedCategoryFilter) {
        criticalItems.filter { item ->
            val matchSearch = searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.supplier.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCategoryFilter == "Semua" || selectedCategoryFilter == "Hampir Habis" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
            matchSearch && matchCat
        }
    }

    val filteredRoutine = remember(routineItems, searchQuery, selectedCategoryFilter) {
        if (selectedCategoryFilter == "Hampir Habis") emptyList()
        else routineItems.filter { item ->
            val matchSearch = searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true) || item.supplier.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCategoryFilter == "Semua" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
            matchSearch && matchCat
        }
    }

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
                        Text(
                            text = "TOKO KELONTONG • SINKRON KASIR OTOMATIS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                        Text(
                            text = "Manajemen Stok & Kulakan",
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

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 136.dp)
            ) {
                // Search & Barcode Bar
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp).size(22.dp)
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text("Cari sembako / agen supplier / barcode...", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant.copy(alpha = 0.7f))
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                                }
                            }
                            Surface(
                                onClick = { viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = true)) },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan Barcode Kulakan", tint = Primary, modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                    }
                }

                // Quick Status Summary (Bento Snapshot)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Critical Stock Warning Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(ErrorContainer.copy(alpha = 0.85f), ErrorContainer)
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(ErrorColor.copy(alpha = 0.85f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.NotificationImportant, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                                    }
                                    Column {
                                        Text(
                                            text = "PERLU KULAKAN CEPAT",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ErrorColor
                                        )
                                        Row(
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "${criticalItems.size} Barang",
                                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                                color = OnErrorContainer
                                            )
                                            Text(
                                                text = "Stok Kritis",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = OnErrorContainer.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        onClick = { viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = false)) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerLowest.copy(alpha = 0.9f)
                                    ) {
                                        Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.Calculate, contentDescription = "Hitung Kulak", tint = ErrorColor, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Surface(
                                        onClick = {
                                            selectedCategoryFilter = "Hampir Habis"
                                            coroutineScope.launch { listState.animateScrollToItem(3) }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = ErrorColor
                                    ) {
                                        Text(
                                            text = "Lihat Segera",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 2 Columns: Total Barang & Estimasi Modal (Live Dynamic Calculation)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceContainerLowest,
                                shadowElevation = 1.dp,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Total SKU Aktif", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Icon(Icons.Outlined.Category, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    }
                                    Text("${stockItems.size} SKU (${stockItems.sumOf { it.stockQty.toInt() }} Unit)", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                                    Text("Otomatis potong saat bayar", style = MaterialTheme.typography.labelSmall, color = Primary)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SurfaceContainerLowest,
                                shadowElevation = 1.dp,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Nilai Modal Stok", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                    }
                                    Text(formatRupiah(totalAssetValue), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), maxLines = 1)
                                    Text("Aset Kulakan di Rak", style = MaterialTheme.typography.labelSmall, color = Secondary)
                                }
                            }
                        }
                    }
                }

                // Filter Category Chips
                item {
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("FILTER KATEGORI BARANG", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = OnSurfaceVariant)
                            Row(
                                modifier = Modifier.clickable { showManageCategorySheet = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.EditNote, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Text("Kelola Kategori", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { selectedCategoryFilter = "Semua" },
                                shape = RoundedCornerShape(50),
                                color = if (selectedCategoryFilter == "Semua") PrimaryContainer else SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = "Semua (${stockItems.size})",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (selectedCategoryFilter == "Semua") Color.White else OnSurface,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }

                            Surface(
                                onClick = { selectedCategoryFilter = "Hampir Habis" },
                                shape = RoundedCornerShape(50),
                                color = if (selectedCategoryFilter == "Hampir Habis") ErrorColor else SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = if (selectedCategoryFilter == "Hampir Habis") Color.White else ErrorColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Hampir Habis (${criticalItems.size})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (selectedCategoryFilter == "Hampir Habis") Color.White else OnSurface
                                    )
                                }
                            }

                            categories.forEach { cat ->
                                val isSel = selectedCategoryFilter == cat
                                Surface(
                                    onClick = { selectedCategoryFilter = cat },
                                    shape = RoundedCornerShape(50),
                                    color = if (isSel) PrimaryContainer else SurfaceContainerLowest,
                                    shadowElevation = 1.dp
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSel) Color.White else OnSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 1: Prioritas Kulakan (Stok Menipis)
                if (filteredCritical.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ErrorColor))
                                    Text("⚠️ Prioritas Kulakan (Stok Menipis)", style = MaterialTheme.typography.headlineSmall)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ErrorContainer.copy(alpha = 0.7f)
                                ) {
                                    Text(
                                        text = "${filteredCritical.size} Barang Darurat",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ErrorColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            filteredCritical.forEach { item ->
                                CriticalStockCard(
                                    item = item,
                                    onPrimaryRestock = {
                                        viewModel.restockItem(item.id, item.restockPrimaryAddQty, item.restockPrimaryLabel)
                                    },
                                    onSecondaryRestock = {
                                        if (item.restockSecondaryAddQty > 0) {
                                            viewModel.restockItem(item.id, item.restockSecondaryAddQty, item.restockSecondaryLabel ?: "Kulakan Tambahan")
                                        } else {
                                            preselectedStockItem = item
                                            showAddManualStockSheet = true
                                        }
                                    },
                                    onCustomEdit = {
                                        preselectedStockItem = item
                                        showAddManualStockSheet = true
                                    }
                                )
                            }
                        }
                    }
                }

                // Section 2: Daftar Semua Stok Barang (Stok Aman & Rutin)
                if (filteredRoutine.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                    Text("📦 Daftar Stok Barang Aman (${filteredRoutine.size})", style = MaterialTheme.typography.headlineSmall)
                                }
                                Text(
                                    text = "+ Catat Kulak",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Primary,
                                    modifier = Modifier.clickable {
                                        preselectedStockItem = null
                                        showAddManualStockSheet = true
                                    }
                                )
                            }

                            filteredRoutine.forEach { item ->
                                RoutineStockRowCard(
                                    item = item,
                                    onQuickRestock = {
                                        viewModel.restockItem(item.id, item.restockPrimaryAddQty, item.restockPrimaryLabel)
                                    },
                                    onMoreClick = {
                                        preselectedStockItem = item
                                        showAddManualStockSheet = true
                                    }
                                )
                            }
                        }
                    }
                }

                // Section 3: Riwayat Mutasi Stok (Barang Masuk Kulakan & Otomatis Terjual Kasir)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                Icon(Icons.Outlined.History, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                Text("Riwayat Mutasi Stok & Kulakan", style = MaterialTheme.typography.headlineSmall)
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PrimaryFixed
                            ) {
                                Text(
                                    text = "Live Sync Kasir",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = OnPrimaryFixed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        mutationLogs.take(8).forEach { log ->
                            StockMutationRowCard(log = log)
                        }
                    }
                }
            }

            // Sticky Bottom Fast Inventory Action Bar
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = false)) },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(1.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Calculate, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kalkulator HPP", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Surface(
                            onClick = { viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = true)) },
                            shape = RoundedCornerShape(12.dp),
                            color = SecondaryFixed,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .border(1.dp, Secondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Dus Kulakan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnSecondaryFixed)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            preselectedStockItem = null
                            showAddManualStockSheet = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_tambah_stok_manual")
                    ) {
                        Icon(Icons.Outlined.AddCircle, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Catat Barang Masuk / Kulakan", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }

    // Bottom Sheet: Kelola Kategori Toko
    if (showManageCategorySheet) {
        var newCatInput by remember { mutableStateOf("") }
        ModalBottomSheet(
            onDismissRequest = { showManageCategorySheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.Category, contentDescription = null, tint = Primary)
                        Text("Kelola Kategori Toko", style = MaterialTheme.typography.headlineSmall)
                    }
                    IconButton(onClick = { showManageCategorySheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("+ Tambah Kategori Baru", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newCatInput,
                                onValueChange = { newCatInput = it },
                                placeholder = { Text("Contoh: Bumbu Dapur, Es Krim...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    viewModel.addCategory(newCatInput)
                                    newCatInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                            ) {
                                Text("Simpan")
                            }
                        }
                    }
                }

                Text("DAFTAR KATEGORI SAAT INI", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                categories.forEach { cat ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.DragHandle, contentDescription = null, tint = OutlineColor)
                                Text(cat, style = MaterialTheme.typography.labelLarge)
                            }
                            IconButton(
                                onClick = { viewModel.removeCategory(cat) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Hapus", tint = ErrorColor)
                            }
                        }
                    }
                }

                Button(
                    onClick = { showManageCategorySheet = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Text("Selesai & Tutup")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Modal Layar Penuh: Catat Barang Masuk / Kulakan Lengkap (Dengan Pencarian, Kategori & Smart Add Sinkron)
    if (showAddManualStockSheet) {
        val initialTarget = preselectedStockItem ?: stockItems.firstOrNull()
        var modalSearchQuery by remember { mutableStateOf("") }
        var modalCategoryFilter by remember { mutableStateOf("Semua") }
        var itemName by remember(initialTarget) { mutableStateOf(initialTarget?.name ?: "Minyak Goreng Kita 1L") }
        var selectedCat by remember(initialTarget) { mutableStateOf(initialTarget?.category ?: (categories.firstOrNull() ?: "Sembako Curah")) }
        var unitText by remember(initialTarget) { mutableStateOf(initialTarget?.unit ?: "Pch") }
        var addMethod by remember { mutableIntStateOf(0) } // 0: Kulakan Baru, 1: Koreksi Opname, 2: Retur Masuk
        var qtyMasuk by remember(initialTarget) { mutableIntStateOf(initialTarget?.packSize ?: 12) }
        var hargaKulakText by remember(initialTarget) { mutableStateOf((initialTarget?.wholesalePrice ?: 14500L).toString()) }
        var hargaJualText by remember(initialTarget) { mutableStateOf((initialTarget?.sellingPrice ?: 16500L).toString()) }
        var supplierText by remember(initialTarget) { mutableStateOf(initialTarget?.supplier ?: "Agen Sembako Barokah") }

        // Smart Add Detection: sinkron otomatis dengan daftar barang yang ada
        val smartMatchedStock = remember(itemName, stockItems) {
            if (itemName.isBlank()) null
            else stockItems.find { it.name.equals(itemName.trim(), ignoreCase = true) }
                ?: stockItems.find {
                    itemName.trim().length >= 4 &&
                        (it.name.contains(itemName.trim(), ignoreCase = true) ||
                            itemName.trim().contains(it.name.take(8), ignoreCase = true))
                }
        }
        val isExactMatch = remember(itemName, stockItems) {
            stockItems.any { it.name.equals(itemName.trim(), ignoreCase = true) }
        }

        // Daftar barang yang difilter berdasarkan Opsi Cari & Pilihan Kategori di dalam Modal
        val filteredModalStockList = remember(modalSearchQuery, modalCategoryFilter, stockItems) {
            stockItems.filter { stk ->
                val matchCat = modalCategoryFilter == "Semua" || stk.category.equals(modalCategoryFilter, ignoreCase = true)
                val matchQuery = modalSearchQuery.isBlank() ||
                    stk.name.contains(modalSearchQuery, ignoreCase = true) ||
                    stk.supplier.contains(modalSearchQuery, ignoreCase = true) ||
                    stk.category.contains(modalSearchQuery, ignoreCase = true)
                matchCat && matchQuery
            }
        }

        fun applySmartSyncItem(stk: StockItem) {
            itemName = stk.name
            selectedCat = stk.category
            unitText = stk.unit
            qtyMasuk = stk.packSize
            hargaKulakText = stk.wholesalePrice.toString()
            hargaJualText = stk.sellingPrice.toString()
            supplierText = stk.supplier
        }

        val currentStockBefore = smartMatchedStock?.stockQty?.toInt() ?: 0
        val hargaKulak = hargaKulakText.toLongOrNull() ?: 14500L
        val hargaJual = hargaJualText.toLongOrNull() ?: 16500L
        val totalNilaiKulak = hargaKulak * qtyMasuk
        val methodLabels = listOf("Kulakan Baru", "Koreksi Opname", "Retur Masuk")

        Dialog(
            onDismissRequest = { showAddManualStockSheet = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("modal_catat_barang_masuk_fullscreen"),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    // Sticky Top Bar Modal Layar Penuh
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 3.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    onClick = { showAddManualStockSheet = false },
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceContainerHigh,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Tutup Modal",
                                            tint = OnSurface
                                        )
                                    }
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Catat Barang Masuk (Kulakan)",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = PrimaryFixed
                                        ) {
                                            Text(
                                                text = "Layar Penuh",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                color = OnPrimaryFixed,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Cari barang, pilih kategori, atau Smart Add otomatis",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Body Konten yang Dapat Di-scroll Penuh dengan Mudah
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Opsi Cari & Filter Kategori untuk Sinkronisasi Daftar Barang
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "CARI & SINKRON BARANG TERDAFTAR (${filteredModalStockList.size})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Primary
                                        )
                                    }
                                    if (modalSearchQuery.isNotEmpty() || modalCategoryFilter != "Semua") {
                                        Text(
                                            text = "Reset Filter",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ErrorColor,
                                            modifier = Modifier.clickable {
                                                modalSearchQuery = ""
                                                modalCategoryFilter = "Semua"
                                            }
                                        )
                                    }
                                }

                                // Search Bar di dalam Modal
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceContainerLowest,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Outlined.Search, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (modalSearchQuery.isEmpty()) {
                                                Text(
                                                    text = "Cari nama barang / agen supplier...",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = OnSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                            BasicTextField(
                                                value = modalSearchQuery,
                                                onValueChange = { query ->
                                                    modalSearchQuery = query
                                                    val autoMatch = stockItems.find { it.name.contains(query, ignoreCase = true) }
                                                    if (query.length >= 3 && autoMatch != null) {
                                                        applySmartSyncItem(autoMatch)
                                                    } else if (query.isNotBlank() && autoMatch == null) {
                                                        itemName = query
                                                    }
                                                },
                                                singleLine = true,
                                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("modal_kulakan_search_input")
                                            )
                                        }
                                        if (modalSearchQuery.isNotEmpty()) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Bersihkan",
                                                tint = OnSurfaceVariant,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clickable { modalSearchQuery = "" }
                                            )
                                        }
                                    }
                                }

                                // Filter Kategori Cepat di dalam Modal
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val allModalCategories = listOf("Semua") + categories
                                    allModalCategories.forEach { cat ->
                                        val isCatSelected = modalCategoryFilter.equals(cat, ignoreCase = true)
                                        Surface(
                                            onClick = {
                                                modalCategoryFilter = cat
                                                if (cat != "Semua") {
                                                    selectedCat = cat
                                                }
                                            },
                                            shape = RoundedCornerShape(50),
                                            color = if (isCatSelected) PrimaryContainer else SurfaceContainerLowest
                                        ) {
                                            Text(
                                                text = cat,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isCatSelected) Color.White else OnSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }

                                // Hasil Daftar Barang yang Sinkron (Ketuk untuk Smart Fill)
                                if (filteredModalStockList.isEmpty()) {
                                    Surface(
                                        onClick = {
                                            if (modalSearchQuery.isNotBlank()) {
                                                itemName = modalSearchQuery
                                                if (modalCategoryFilter != "Semua") selectedCat = modalCategoryFilter
                                                hargaKulakText = "12000"
                                                hargaJualText = "14500"
                                                qtyMasuk = 12
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SecondaryFixed.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "Belum ada di daftar stok",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Secondary
                                                )
                                                Text(
                                                    text = "Smart Add: Buat SKU baru \"${modalSearchQuery.ifBlank { itemName }}\"",
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = OnSurface
                                                )
                                            }
                                            Surface(shape = RoundedCornerShape(6.dp), color = Secondary) {
                                                Text(
                                                    text = "+ SKU Baru",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        filteredModalStockList.forEach { stk ->
                                            val isSelected = stk.name.equals(itemName, ignoreCase = true)
                                            Surface(
                                                onClick = { applySmartSyncItem(stk) },
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isSelected) Primary else SurfaceContainerLowest,
                                                shadowElevation = 1.dp
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Text(
                                                            text = stk.name,
                                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                            color = if (isSelected) Color.White else OnSurface
                                                        )
                                                        if (stk.isCritical) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(6.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) SecondaryFixed else ErrorColor)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = "Sisa: ${stk.stockQty.toInt()} ${stk.unit} • ${stk.category}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else OnSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Indikator Status Smart Add (Sinkronisasi Barang Lama vs Barang Baru)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (smartMatchedStock != null) PrimaryFixed.copy(alpha = 0.45f) else SecondaryFixed.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (smartMatchedStock != null) Icons.Outlined.Sync else Icons.Outlined.NewReleases,
                                        contentDescription = null,
                                        tint = if (smartMatchedStock != null) Primary else Secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (smartMatchedStock != null) {
                                                "SMART ADD SINKRON: ${smartMatchedStock.name}"
                                            } else {
                                                "SMART ADD BARANG BARU: ${itemName.ifBlank { "Ketik Nama Barang" }}"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = if (smartMatchedStock != null) Primary else Secondary
                                        )
                                        Text(
                                            text = if (smartMatchedStock != null) {
                                                "Otomatis tambah ke stok lama (${smartMatchedStock.stockQty.toInt()} ${smartMatchedStock.unit}) & sinkron harga"
                                            } else {
                                                "Belum terdaftar di rak • Akan otomatis dibuatkan kartu stok baru"
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }
                                if (smartMatchedStock != null && !isExactMatch) {
                                    Surface(
                                        onClick = { applySmartSyncItem(smartMatchedStock) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = Primary
                                    ) {
                                        Text(
                                            text = "Samakan",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Input Nama Barang & Satuan (Dengan Auto-Sync saat mengetik)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = itemName,
                                onValueChange = { typed ->
                                    itemName = typed
                                    val exact = stockItems.find { it.name.equals(typed.trim(), ignoreCase = true) }
                                    if (exact != null) {
                                        applySmartSyncItem(exact)
                                    }
                                },
                                label = { Text("Nama Barang Sembako") },
                                singleLine = true,
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = unitText,
                                onValueChange = { unitText = it },
                                label = { Text("Satuan") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // 4. Pilihan Kategori Barang (Bisa dipilih untuk barang baru atau ubah kategori)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PILIH KATEGORI BARANG:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurfaceVariant
                                )
                                Text(
                                    text = "Aktif: $selectedCat",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Primary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                categories.forEach { catOption ->
                                    val isSelectedCat = selectedCat.equals(catOption, ignoreCase = true)
                                    Surface(
                                        onClick = { selectedCat = catOption },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelectedCat) Primary else SurfaceContainerHigh,
                                        modifier = Modifier.border(
                                            width = 1.dp,
                                            color = if (isSelectedCat) Primary else OutlineVariant.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Category,
                                                contentDescription = null,
                                                tint = if (isSelectedCat) Color.White else OnSurfaceVariant,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = catOption,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelectedCat) Color.White else OnSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Tujuan / Metode Penambahan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            methodLabels.forEachIndexed { idx, label ->
                                val isSel = addMethod == idx
                                Surface(
                                    onClick = { addMethod = idx },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) PrimaryContainer else SurfaceContainerLowest,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSel) Color.White else OnSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                                    )
                                }
                            }
                        }

                        // 6. Jumlah Masuk Stepper
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceContainerLowest)
                                            .clickable { if (qtyMasuk > 1) qtyMasuk-- },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("−", style = MaterialTheme.typography.headlineMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "+$qtyMasuk $unitText",
                                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Primary
                                        )
                                        Text(
                                            text = "Stok awal: $currentStockBefore $unitText ➔ Baru: ${currentStockBefore + qtyMasuk} $unitText",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PrimaryContainer)
                                            .clickable { qtyMasuk++ },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val packPresetQty = smartMatchedStock?.packSize ?: 12
                                    val packPresetName = smartMatchedStock?.packUnitName ?: "Dus"
                                    listOf(
                                        Pair("+5", 5),
                                        Pair("+10", 10),
                                        Pair("+1 $packPresetName ($packPresetQty)", packPresetQty),
                                        Pair("+2 $packPresetName (${packPresetQty * 2})", packPresetQty * 2)
                                    ).forEach { (lbl, q) ->
                                        Surface(
                                            onClick = { qtyMasuk = q },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (qtyMasuk == q) PrimaryFixed else SurfaceContainerHigh,
                                            modifier = Modifier.weight(1f).height(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = lbl,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 7. Harga Modal Kulak & Harga Jual Kasir
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = hargaKulakText,
                                onValueChange = {
                                    val clean = it.filter { c -> c.isDigit() }
                                    hargaKulakText = clean
                                    if (smartMatchedStock == null) {
                                        val modalVal = clean.toLongOrNull() ?: 0L
                                        if (modalVal > 0L) {
                                            val suggestedSell = (Math.round(modalVal * 1.18 / 500.0) * 500L)
                                            hargaJualText = suggestedSell.toString()
                                        }
                                    }
                                },
                                label = { Text("Modal Kulak / $unitText") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = hargaJualText,
                                onValueChange = { hargaJualText = it.filter { c -> c.isDigit() } },
                                label = { Text("Harga Jual Kasir") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = supplierText,
                            onValueChange = { supplierText = it },
                            label = { Text("Nama Agen / Supplier Grosir") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Sticky Bottom Bar (Ringkasan Total Kulakan & Tombol Simpan)
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 10.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PrimaryContainer.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total Belanja Kulakan:", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text(formatRupiah(totalNilaiKulak), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Primary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Estimasi Laba / $unitText:", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text(
                                            text = "+${formatRupiah((hargaJual - hargaKulak).coerceAtLeast(0L))}",
                                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Primary
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    val targetName = smartMatchedStock?.name ?: itemName.ifBlank { "Barang Sembako Baru" }
                                    viewModel.addManualStock(
                                        name = targetName,
                                        category = selectedCat,
                                        addQty = qtyMasuk.toDouble(),
                                        unit = unitText.ifBlank { "Pcs" },
                                        wholesalePrice = hargaKulak,
                                        supplier = supplierText,
                                        sellingPriceOverride = hargaJual,
                                        methodLabel = methodLabels[addMethod]
                                    )
                                    showAddManualStockSheet = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_simpan_stok_masuk_fullscreen"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                            ) {
                                Icon(
                                    imageVector = if (smartMatchedStock != null) Icons.Outlined.Sync else Icons.Outlined.AddCircle,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (smartMatchedStock != null) {
                                        "Smart Add: Tambah +$qtyMasuk $unitText ke Stok"
                                    } else {
                                        "Smart Add: Simpan Barang Baru (+$qtyMasuk $unitText)"
                                    },
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockMutationRowCard(log: StockMutationLog) {
    val isIncoming = log.qtyDelta >= 0
    val badgeColor = when (log.type) {
        StockMutationType.KULAKAN_MASUK -> PrimaryFixed
        StockMutationType.TERJUAL_KASIR -> SecondaryFixed
        StockMutationType.OPNAME_KOREKSI -> TertiaryFixed
    }
    val badgeTextColor = when (log.type) {
        StockMutationType.KULAKAN_MASUK -> OnPrimaryFixed
        StockMutationType.TERJUAL_KASIR -> OnSecondaryFixed
        StockMutationType.OPNAME_KOREKSI -> OnTertiaryFixed
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isIncoming) Icons.Outlined.SouthWest else Icons.Outlined.ShoppingCartCheckout,
                        contentDescription = null,
                        tint = badgeTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = log.itemName,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = badgeColor
                        ) {
                            Text(
                                text = log.type.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = badgeTextColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${log.referenceNote} • ${log.timeLabel}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val sign = if (isIncoming) "+" else ""
                val qtyStr = if (log.qtyDelta % 1.0 == 0.0) log.qtyDelta.toInt().toString() else log.qtyDelta.toString()
                Text(
                    text = "$sign$qtyStr ${log.unit}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (isIncoming) Primary else Secondary
                )
                Text(
                    text = "Stok: ${log.stockBefore.toInt()} ➔ ${log.stockAfter.toInt()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CriticalStockCard(
    item: StockItem,
    onPrimaryRestock: () -> Unit,
    onSecondaryRestock: () -> Unit,
    onCustomEdit: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
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
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ErrorColor,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    ) {
                        val qtyStr = if (item.stockQty % 1.0 == 0.0) item.stockQty.toInt().toString() else item.stockQty.toString()
                        Text(
                            text = if (item.emptyQty > 0) "Isi: $qtyStr" else "Sisa $qtyStr",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.emptyQty > 0) SecondaryFixed.copy(alpha = 0.6f) else ErrorContainer.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = if (item.emptyQty > 0) "Kosong: ${item.emptyQty} ${item.unit}" else "Batas: ${item.minLimit.toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (item.emptyQty > 0) Secondary else ErrorColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = Tertiary, modifier = Modifier.size(14.dp))
                        Text(item.supplier, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Kulak: ${formatRupiah(item.wholesalePrice)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurface
                        )
                        Text(
                            text = "Jual: ${formatRupiah(item.sellingPrice)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                    }
                }
            }

            // Quick Restock Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerLow)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onPrimaryRestock,
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.name.contains("Gas")) Secondary else PrimaryContainer,
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = item.restockPrimaryLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                if (item.restockSecondaryLabel != null) {
                    Surface(
                        onClick = onSecondaryRestock,
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.weight(0.75f).height(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.restockSecondaryLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = OnSurface
                            )
                        }
                    }
                }

                Surface(
                    onClick = onCustomEdit,
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainerLowest,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.EditNote, contentDescription = "Edit Stok", tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineStockRowCard(
    item: StockItem,
    onQuickRestock: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            item.name.contains("Indomie") -> Icons.Outlined.RamenDining
                            item.name.contains("Beras") -> Icons.Outlined.Grain
                            item.name.contains("Gula") -> Icons.Outlined.BakeryDining
                            else -> Icons.Outlined.Inventory2
                        },
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryFixed
                        ) {
                            Text(
                                text = "● ${item.stockQty.toInt()} ${item.unit} (Aman)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = OnPrimaryFixedVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "${formatRupiah(item.sellingPrice)} / ${item.unit.lowercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = onQuickRestock,
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.restockPrimaryLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }
                }

                Surface(
                    onClick = onMoreClick,
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Opsi", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
