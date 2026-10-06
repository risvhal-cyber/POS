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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Hampir Habis") }
    var showManageCategorySheet by remember { mutableStateOf(false) }
    var showAddManualStockSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val criticalItems = stockItems.filter { it.isCritical }
    val routineItems = stockItems.filter { !it.isCritical }

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
                            text = "TOKO KELONTONG",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "Manajemen Stok Kelontong",
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
                contentPadding = PaddingValues(bottom = 128.dp)
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
                                    Text("Cari sembako / scan barcode...", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant.copy(alpha = 0.7f))
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
                                onClick = { viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = true)) },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan Barcode", tint = Primary, modifier = Modifier.size(22.dp))
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

                        // 2 Columns: Total Barang & Estimasi Modal
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
                                        Text("Total Barang", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Icon(Icons.Outlined.Category, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    }
                                    Text("148", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                                    Text("Jenis Produk Sembako", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
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
                                        Text("Estimasi Modal", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                    }
                                    Text("Rp 18.450.000", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), maxLines = 1)
                                    Text("Aset di Rak Toko", style = MaterialTheme.typography.labelSmall, color = Secondary)
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
                            Text("KATEGORI BARANG", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = OnSurfaceVariant)
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

                            Surface(
                                onClick = { selectedCategoryFilter = "Semua" },
                                shape = RoundedCornerShape(50),
                                color = if (selectedCategoryFilter == "Semua") PrimaryContainer else SurfaceContainerLowest,
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = "Semua (148)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (selectedCategoryFilter == "Semua") Color.White else OnSurface,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
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
                                    text = "${criticalItems.size} Barang Darurat",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ErrorColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        criticalItems.filter {
                            searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
                        }.forEach { item ->
                            CriticalStockCard(
                                item = item,
                                onPrimaryRestock = { viewModel.restockItem(item.id, item.restockPrimaryAddQty) },
                                onSecondaryRestock = {
                                    if (item.restockSecondaryAddQty > 0) {
                                        viewModel.restockItem(item.id, item.restockSecondaryAddQty)
                                    } else {
                                        showAddManualStockSheet = true
                                    }
                                },
                                onCustomEdit = { showAddManualStockSheet = true }
                            )
                        }
                    }
                }

                // Section 2: Daftar Semua Stok Barang (Stok Aman & Rutin)
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
                                Text("📦 Daftar Semua Stok Barang", style = MaterialTheme.typography.headlineSmall)
                            }
                            Text("Urutkan ⇅", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Primary)
                        }

                        routineItems.filter {
                            searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
                        }.forEach { item ->
                            RoutineStockRowCard(
                                item = item,
                                onQuickRestock = { viewModel.restockItem(item.id, item.restockPrimaryAddQty) },
                                onMoreClick = {
                                    viewModel.navigateTo(ScreenRoute.WholesaleCalculator(openScanSheet = false, initialSkuName = item.name))
                                }
                            )
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
                                Text("Hitung Kulak", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
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
                                Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Nota Pasar", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnSecondaryFixed)
                            }
                        }
                    }

                    Button(
                        onClick = { showAddManualStockSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_tambah_stok_manual")
                    ) {
                        Icon(Icons.Outlined.AddCircle, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+ Tambah Stok Manual", style = MaterialTheme.typography.headlineSmall)
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

    // Bottom Sheet: Tambah Stok Manual Lengkap
    if (showAddManualStockSheet) {
        var itemName by remember { mutableStateOf("Minyak Goreng Kita 1L") }
        var selectedCat by remember { mutableStateOf("Sembako & Minyak") }
        var addMethod by remember { mutableIntStateOf(0) } // 0: Kulakan Baru, 1: Koreksi Opname, 2: Retur Masuk
        var qtyMasuk by remember { mutableIntStateOf(12) }
        var hargaKulakText by remember { mutableStateOf("14500") }
        var supplierText by remember { mutableStateOf("Agen Sembako Barokah") }

        val hargaKulak = hargaKulakText.toLongOrNull() ?: 14500L
        val totalNilaiKulak = hargaKulak * qtyMasuk

        ModalBottomSheet(
            onDismissRequest = { showAddManualStockSheet = false },
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
                        Icon(Icons.Outlined.AddCircle, contentDescription = null, tint = Primary)
                        Column {
                            Text("Tambah Stok Manual", style = MaterialTheme.typography.headlineSmall)
                            Text("Kulakan, Koreksi Opname, atau Retur", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { showAddManualStockSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Pilih Barang Sembako") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Tujuan / Metode Penambahan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Kulakan Baru", "Koreksi Opname", "Retur Masuk").forEachIndexed { idx, label ->
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
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }

                // Jumlah Masuk Stepper
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
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
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLowest)
                                    .clickable { if (qtyMasuk > 1) qtyMasuk-- },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("−", style = MaterialTheme.typography.headlineMedium)
                            }
                            Text(
                                text = "+$qtyMasuk Pcs/Pch",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = Primary
                            )
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
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
                            listOf(Pair("+1", 1), Pair("+5", 5), Pair("+10", 10), Pair("+1 Dus", 12), Pair("+2 Dus", 24)).forEach { (lbl, q) ->
                                Surface(
                                    onClick = { qtyMasuk = q },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (qtyMasuk == q) PrimaryFixed else SurfaceContainerHigh,
                                    modifier = Modifier.weight(1f).height(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(lbl, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hargaKulakText,
                        onValueChange = { hargaKulakText = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga Kulak / Satuan") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = supplierText,
                        onValueChange = { supplierText = it },
                        label = { Text("Supplier / Agen") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryContainer.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Nilai Kulak Masuk:", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Text(formatRupiah(totalNilaiKulak), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Primary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Stok Baru Menjadi:", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            Text("${qtyMasuk + 2} Pcs", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.addManualStock(
                            name = itemName,
                            category = selectedCat,
                            addQty = qtyMasuk.toDouble(),
                            unit = "Pcs",
                            wholesalePrice = hargaKulak,
                            supplier = supplierText
                        )
                        showAddManualStockSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Icon(Icons.Outlined.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Stok", style = MaterialTheme.typography.headlineSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
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
