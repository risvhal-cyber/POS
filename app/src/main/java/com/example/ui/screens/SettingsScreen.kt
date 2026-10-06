package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.WarungImages
import com.example.ui.components.NetworkProductImage
import com.example.ui.components.ProfileAvatarButton
import com.example.ui.theme.*
import com.example.viewmodel.WarungViewModel

@Composable
fun SettingsScreen(
    viewModel: WarungViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val storeName by viewModel.storeName.collectAsState()
    val storeAddress by viewModel.storeAddress.collectAsState()
    val storePhone by viewModel.storePhone.collectAsState()
    val isDarkTheme by viewModel.darkTheme.collectAsState()
    val paperSize58 by viewModel.paperSize58mm.collectAsState()
    val autoPrint by viewModel.autoPrint.collectAsState()

    var showLogoOnReceipt by remember { mutableStateOf(true) }
    var showKasbonOnReceipt by remember { mutableStateOf(true) }
    var extraLargeButtons by remember { mutableStateOf(true) }
    var beepSoundOnScan by remember { mutableStateOf(true) }
    var showReceiptPreviewModal by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
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
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                    Text("Pengaturan", style = MaterialTheme.typography.headlineSmall)
                }
                ProfileAvatarButton(onClick = {})
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status Bar Toko Ringkas
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryFixed.copy(alpha = 0.45f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Primary))
                        Text("Status Toko: Buka & Aktif Kasir", style = MaterialTheme.typography.labelMedium, color = OnPrimaryFixedVariant)
                    }
                    Surface(shape = RoundedCornerShape(50), color = SurfaceContainerLowest, shadowElevation = 1.dp) {
                        Text(
                            text = "Shift 1 Pagi",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 1. Profil & Identitas Toko
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
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box {
                                NetworkProductImage(
                                    url = WarungImages.STORE_PROFILE,
                                    contentDescription = storeName,
                                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(14.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(storeName, style = MaterialTheme.typography.headlineMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SecondaryFixed,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text("Akun Utama", style = MaterialTheme.typography.labelSmall, color = OnSecondaryFixed, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                                Text(storeAddress, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(Icons.Outlined.Call, contentDescription = null, tint = Primary, modifier = Modifier.size(15.dp))
                                    Text(storePhone, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                                }
                            }
                        }

                        Surface(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profil & Keterangan Nota", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            // 2. Printer Thermal Kasir
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
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Print, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                }
                                Text("Printer Thermal Kasir", style = MaterialTheme.typography.headlineSmall)
                            }
                            Surface(shape = RoundedCornerShape(50), color = PrimaryFixed) {
                                Text("Bluetooth", style = MaterialTheme.typography.labelSmall, color = Primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(modifier = Modifier.padding(top = 4.dp).size(12.dp).clip(CircleShape).background(Primary))
                            Column {
                                Text("Printer POS Bluetooth 58mm", style = MaterialTheme.typography.labelLarge)
                                Text("Terkoneksi • Siap Cetak Kertas", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Primary)
                                Text("Sinyal Bluetooth: Stabil (-52dBm)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }

                        Text("Pilihan Ukuran Kertas:", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { viewModel.setPaperSize58(true) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (paperSize58) Primary else SurfaceContainerHigh,
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "58mm (Standar Warung)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (paperSize58) Color.White else OnSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Surface(
                                onClick = { viewModel.setPaperSize58(false) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (!paperSize58) Primary else SurfaceContainerHigh,
                                modifier = Modifier.weight(1f).height(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "80mm (Lebar Supermarket)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (!paperSize58) Color.White else OnSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Cetak Otomatis Selesai Transaksi", style = MaterialTheme.typography.labelLarge)
                                Text("Langsung cetak struk tanpa perlu tekan tombol cetak", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Switch(
                                checked = autoPrint,
                                onCheckedChange = { viewModel.setAutoPrint(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }

                        Surface(
                            onClick = { viewModel.showToast("Berhasil mencetak lembar tes ke Printer 58mm!") },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.AssignmentTurnedIn, contentDescription = null, tint = Primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tes Cetak Nota Percobaan", style = MaterialTheme.typography.headlineSmall, color = Primary)
                            }
                        }
                    }
                }
            }

            // 3. Desain Struk Pembelian
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Tertiary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Receipt, contentDescription = null, tint = Tertiary, modifier = Modifier.size(20.dp))
                            }
                            Text("Desain Struk Pembelian", style = MaterialTheme.typography.headlineSmall)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Catatan Kaki / Footer Nota:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Tersimpan", style = MaterialTheme.typography.labelSmall, color = Primary)
                            }
                            Text(
                                text = "“Terima kasih sudah belanja di Warung Laufi. Barang yg sdh dibeli tdk dpt ditukar kembali.”",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = OnSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerLowest)
                                    .padding(10.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Tampilkan Logo Toko di Nota", style = MaterialTheme.typography.labelLarge)
                                Text("Cetak gambar logo monokrom pada kepala nota", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Switch(
                                checked = showLogoOnReceipt,
                                onCheckedChange = { showLogoOnReceipt = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Tampilkan Catatan Hutang / Kasbon", style = MaterialTheme.typography.labelLarge)
                                Text("Cetak sisa batas kasbon pelanggan pada struk transaksi", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Switch(
                                checked = showKasbonOnReceipt,
                                onCheckedChange = { showKasbonOnReceipt = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }

                        Surface(
                            onClick = { showReceiptPreviewModal = true },
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceContainer,
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pratinjau / Preview Nota Fisik", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            // 4. Tampilan & Suara
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(SecondaryContainer.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Palette, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                            }
                            Text("Tampilan & Suara", style = MaterialTheme.typography.headlineSmall)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { viewModel.toggleDarkTheme(false) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (!isDarkTheme) Primary else SurfaceContainerHigh,
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Outlined.LightMode, contentDescription = null, tint = if (!isDarkTheme) Color.White else OnSurfaceVariant)
                                    Text("Terang", style = MaterialTheme.typography.labelMedium, color = if (!isDarkTheme) Color.White else OnSurfaceVariant)
                                }
                            }
                            Surface(
                                onClick = { viewModel.toggleDarkTheme(true) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDarkTheme) Primary else SurfaceContainerHigh,
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = if (isDarkTheme) Color.White else OnSurfaceVariant)
                                    Text("Gelap", style = MaterialTheme.typography.labelMedium, color = if (isDarkTheme) Color.White else OnSurfaceVariant)
                                }
                            }
                            Surface(
                                onClick = { viewModel.toggleDarkTheme(false) },
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Outlined.SettingsBrightness, contentDescription = null, tint = OnSurfaceVariant)
                                    Text("Ikuti HP", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Tombol Kasir Ukuran Ekstra Besar", style = MaterialTheme.typography.labelLarge)
                                    Surface(shape = RoundedCornerShape(4.dp), color = SecondaryFixed) {
                                        Text("Lansia", style = MaterialTheme.typography.labelSmall, color = OnSecondaryFixed, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                                    }
                                }
                                Text("Mencegah salah ketik harga saat warung ramai pembeli", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Switch(
                                checked = extraLargeButtons,
                                onCheckedChange = { extraLargeButtons = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Suara Bip Saat Scan Barcode", style = MaterialTheme.typography.labelLarge)
                                Text("Bunyi konfirmasi instan barang berhasil masuk keranjang", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                            Switch(
                                checked = beepSoundOnScan,
                                onCheckedChange = { beepSoundOnScan = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                            )
                        }
                    }
                }
            }

            // 5. Database & Cadangan
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLowest,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(PrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.CloudSync, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Database & Cadangan", style = MaterialTheme.typography.headlineSmall)
                                Text("100% Offline First Terlindungi", style = MaterialTheme.typography.labelSmall, color = Primary)
                            }
                        }

                        Surface(
                            onClick = { viewModel.showToast("Data berhasil dicadangkan ke Memori & Drive!") },
                            shape = RoundedCornerShape(12.dp),
                            color = Primary,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = Color.White)
                                    Column {
                                        Text("Cadangkan Data Toko Sekarang", style = MaterialTheme.typography.labelLarge, color = Color.White)
                                        Text("Terakhir: Hari ini, 08:30 WIB", style = MaterialTheme.typography.labelSmall, color = OnPrimaryContainer)
                                    }
                                }
                                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.White)
                            }
                        }

                        Surface(
                            onClick = { viewModel.showToast("File Laporan_Warung_Laufi.xlsx berhasil diunduh!") },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(Icons.Outlined.TableView, contentDescription = null, tint = Tertiary)
                                    Column {
                                        Text("Ekspor Laporan ke Excel (.xlsx)", style = MaterialTheme.typography.labelLarge)
                                        Text("Rekap penjualan & stok bulanan rapi", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                    }
                                }
                                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Footer Version & Logout
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Laufi POS Kelontong • v2.4.0", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Edisi Warung Madura & Sembako (Offline Native)", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Text("ID Lisensi Toko: LFK-88219-JKT", style = MaterialTheme.typography.labelSmall, color = Primary)

                    Surface(
                        onClick = {
                            viewModel.showToast("Shift kasir disimpan. Siap ganti kasir.")
                            viewModel.navigateBack()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = ErrorContainer,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Logout, contentDescription = null, tint = OnErrorContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ganti Kasir / Keluar Akun", style = MaterialTheme.typography.labelLarge, color = OnErrorContainer)
                        }
                    }
                }
            }
        }
    }

    // Modal Pratinjau Struk 58mm
    if (showReceiptPreviewModal) {
        Dialog(onDismissRequest = { showReceiptPreviewModal = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainerLowest,
                shadowElevation = 20.dp,
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
                        Text("Pratinjau Nota Cetak", style = MaterialTheme.typography.headlineSmall)
                        IconButton(onClick = { showReceiptPreviewModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceBg)
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(storeName.uppercase(), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(storeAddress, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Text("WA: $storePhone", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Text("================================", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Beras Ramos Wangi 5kg", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            Text("68.000", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Minyak Goreng 2L", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            Text("34.500", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gula Pasir 1kg", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            Text("14.500", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                        Text("--------------------------------", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL:", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Rp 117.000", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("================================", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Text(
                            text = "Terima kasih sudah belanja di Warung Laufi.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.showToast("Mengirim sinyal cetak via Bluetooth...")
                            showReceiptPreviewModal = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Outlined.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cetak Sekarang")
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(storeName) }
        var addressInput by remember { mutableStateOf(storeAddress) }
        var phoneInput by remember { mutableStateOf(storePhone) }
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profil Toko") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Nama Warung") })
                    OutlinedTextField(value = addressInput, onValueChange = { addressInput = it }, label = { Text("Alamat") })
                    OutlinedTextField(value = phoneInput, onValueChange = { phoneInput = it }, label = { Text("Telepon / WA") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateStoreProfile(nameInput, addressInput, phoneInput)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
