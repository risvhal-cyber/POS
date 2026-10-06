package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CompletedTransactionReceipt
import com.example.model.PaymentMethod
import com.example.model.formatRupiah
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TransactionReceiptSuccessDialog(
    receipt: CompletedTransactionReceipt,
    storeName: String,
    storeAddress: String,
    storePhone: String,
    paperSize58mm: Boolean,
    onDismiss: () -> Unit,
    onPrintAgain: () -> Unit,
    onShareWhatsapp: () -> Unit,
    onOpenHistory: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    // 0: Tampilan Resi Cetak & Ringkasan, 1: Rincian Transaksi Lengkap
    var selectedViewMode by remember { mutableIntStateOf(0) }
    var isPrintingAnimation by remember(receipt.transactionId) { mutableStateOf(receipt.printThermalRequested) }
    var printCount by remember(receipt.transactionId) { mutableIntStateOf(if (receipt.printThermalRequested) 1 else 0) }

    LaunchedEffect(isPrintingAnimation) {
        if (isPrintingAnimation) {
            delay(1400)
            isPrintingAnimation = false
        }
    }

    val totalUnits = receipt.lineItems.sumOf { Math.ceil(it.qty).toInt() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InverseSurface.copy(alpha = 0.72f))
            .clickable(onClick = onDismiss)
            .testTag("transaction_receipt_dialog"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceContainerLowest,
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 20.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .heightIn(max = 760.dp)
                .clickable(enabled = false) {}
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Pinned Top Success Banner Header
                Surface(
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = PrimaryFixed,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "TRANSAKSI BERHASIL",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.8.sp
                                            ),
                                            color = PrimaryFixed
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = receipt.method.label,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Detail Transaksi & Resi Cetak",
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.White
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .testTag("btn_close_receipt_dialog")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup Dialog Resi",
                                    tint = Color.White
                                )
                            }
                        }

                        // Live Thermal Printer Status Pill + Mode Switcher
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.Black.copy(alpha = 0.22f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isPrintingAnimation) SecondaryFixed else PrimaryFixed)
                                )
                                Text(
                                    text = when {
                                        isPrintingAnimation -> "Mencetak struk ke Thermal ${if (paperSize58mm) "58mm" else "80mm"}..."
                                        printCount > 0 -> "Resi tercetak (${if (paperSize58mm) "58mm" else "80mm"} • Cetakan #$printCount)"
                                        else -> "Mode Tanpa Cetak Kertas (Resi Digital Siap)"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                            }

                            if (receipt.updatedStockItemsCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = PrimaryFixed
                                ) {
                                    Text(
                                        text = "Stok -${receipt.updatedStockItemsCount} SKU Sinkron",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = OnPrimaryFixed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Segmented Tab Switch: Semua (Detail + Resi) vs Fokus Kertas Resi
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val tabs = listOf(
                                Pair(0, "Resi Cetak & Ringkasan"),
                                Pair(1, "Detail Transaksi Lengkap")
                            )
                            tabs.forEach { (idx, title) ->
                                val isSel = selectedViewMode == idx
                                Surface(
                                    onClick = { selectedViewMode = idx },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color.White else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSel) Primary else Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Scrollable Body: Transaction Summary Card + Printed Thermal Receipt Paper
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Transaction Financial Summary Card (Always shown at top)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "RINGKASAN PEMBAYARAN KASIR",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = OnSurfaceVariant
                                    )
                                    Text(
                                        text = "${receipt.code} • ${receipt.timestampLabel}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OutlineColor
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (receipt.remainingKasbonAmount > 0L) SecondaryFixed else PrimaryFixed
                                ) {
                                    Text(
                                        text = if (receipt.remainingKasbonAmount > 0L) "TERCATAT BON" else "LUNAS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = if (receipt.remainingKasbonAmount > 0L) OnSecondaryFixed else OnPrimaryFixed,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // 3-Column Metrics: Total Tagihan, Uang Diterima, Kembalian / Sisa Bon
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
                                        Text("Total Belanja", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                        Text(
                                            text = formatRupiah(receipt.totalAmount),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = OnSurface,
                                            maxLines = 1
                                        )
                                        Text("${receipt.lineItems.size} Jenis ($totalUnits Unit)", style = MaterialTheme.typography.labelSmall, color = OutlineColor)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SurfaceContainerLow,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = if (receipt.method == PaymentMethod.QRIS) "Dana QRIS" else "Uang Diterima",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                        Text(
                                            text = formatRupiah(receipt.tenderedAmount),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Primary,
                                            maxLines = 1
                                        )
                                        Text("Metode: ${receipt.method.label}", style = MaterialTheme.typography.labelSmall, color = Primary)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (receipt.remainingKasbonAmount > 0L) SecondaryContainer.copy(alpha = 0.35f) else PrimaryFixed.copy(alpha = 0.55f),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = if (receipt.remainingKasbonAmount > 0L) "Masuk Kasbon" else "Kembalian",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (receipt.remainingKasbonAmount > 0L) Secondary else OnPrimaryFixed
                                        )
                                        Text(
                                            text = if (receipt.remainingKasbonAmount > 0L) formatRupiah(receipt.remainingKasbonAmount) else formatRupiah(receipt.changeAmount),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = if (receipt.remainingKasbonAmount > 0L) Secondary else Primary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = if (receipt.remainingKasbonAmount > 0L) "Tempo 7 Hari" else "Serahkan Tunai",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (receipt.remainingKasbonAmount > 0L) Secondary else OnPrimaryFixed
                                        )
                                    }
                                }
                            }

                            // Customer & Cashier Info Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLow)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text(
                                            text = "Pelanggan: ${receipt.customerName}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${receipt.customerBadge} • Dilayani oleh ${receipt.cashierName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }
                                Icon(Icons.Outlined.Verified, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Detailed Transaction Breakdown Section (When tab 1 is active OR shown alongside receipt)
                    if (selectedViewMode == 1) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
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
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                        Text(
                                            text = "Rincian Item Belanja (${receipt.lineItems.size} Barang)",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                    }
                                    Text(
                                        text = "$totalUnits Unit Terjual",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Primary
                                    )
                                }

                                receipt.lineItems.forEachIndexed { idx, item ->
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
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryFixed),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = OnPrimaryFixed
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                    color = OnSurface
                                                )
                                                Text(
                                                    text = "${item.qtyFormatted} ${item.unit} × ${formatRupiah(item.unitPrice)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = OnSurfaceVariant
                                                )
                                                if (!item.note.isNullOrBlank()) {
                                                    Text(
                                                        text = "Catatan: ${item.note}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Primary
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = formatRupiah(item.subtotal),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = OnSurface
                                        )
                                    }
                                }

                                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal Barang", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                                    Text(formatRupiah(receipt.subtotalBeforeDiscount), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                }
                                if (receipt.discountAmount > 0L) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Potongan Diskon Pelanggan", style = MaterialTheme.typography.bodyMedium, color = Primary)
                                        Text("-${formatRupiah(receipt.discountAmount)}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Akhir", style = MaterialTheme.typography.headlineSmall)
                                    Text(formatRupiah(receipt.totalAmount), style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Primary)
                                }
                            }
                        }
                    }

                    // Realistic Thermal Paper Receipt Preview (Tampilan Resi yang Dicetak)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Outlined.Print, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "TAMPILAN RESI STRUK CETAK (${if (paperSize58mm) "THERMAL 58MM" else "THERMAL 80MM"})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = OnSurfaceVariant
                                )
                            }
                            Text(
                                text = "Bluetooth POS Ready",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                        }

                        PrintedThermalReceiptPaper(
                            receipt = receipt,
                            storeName = storeName,
                            storeAddress = storeAddress,
                            storePhone = storePhone,
                            paperSize58mm = paperSize58mm
                        )
                    }
                }

                // 3. Pinned Bottom Action Bar (Cetak Ulang, Kirim WA, Lihat Riwayat, Transaksi Baru)
                Surface(
                    color = SurfaceContainerLowest,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Cetak Ulang Resi Button
                            Surface(
                                onClick = {
                                    isPrintingAnimation = true
                                    printCount++
                                    onPrintAgain()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerHigh,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .testTag("btn_reprint_receipt")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Print, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPrintingAnimation) "Mencetak..." else "Cetak Struk",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Primary
                                    )
                                }
                            }

                            // Kirim Resi WhatsApp Button
                            Surface(
                                onClick = onShareWhatsapp,
                                shape = RoundedCornerShape(12.dp),
                                color = SecondaryFixed,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_share_wa_receipt")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Share, contentDescription = null, tint = OnSecondaryFixed, modifier = Modifier.size(17.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Kirim WA",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = OnSecondaryFixed
                                    )
                                }
                            }

                            // Lihat di Riwayat Button
                            Surface(
                                onClick = onOpenHistory,
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLow,
                                modifier = Modifier
                                    .height(44.dp)
                                    .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Outlined.History, contentDescription = "Riwayat", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = "Riwayat",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_done_new_transaction")
                        ) {
                            Icon(Icons.Outlined.PointOfSale, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Selesai & Transaksi Baru",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrintedThermalReceiptPaper(
    receipt: CompletedTransactionReceipt,
    storeName: String,
    storeAddress: String,
    storePhone: String,
    paperSize58mm: Boolean
) {
    val receiptPaperBg = Color(0xFFFFFDF7)
    val receiptInk = Color(0xFF1B1C19)
    val receiptMutedInk = Color(0xFF555852)

    Column(
        modifier = Modifier
            .fillMaxWidth(if (paperSize58mm) 0.94f else 1f)
            .clip(RoundedCornerShape(6.dp))
            .background(receiptPaperBg)
            .border(1.dp, OutlineVariant.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
            .testTag("thermal_receipt_paper_preview")
    ) {
        // Top Serrated Paper Edge
        SerratedPaperEdge(isTop = true, paperColor = receiptPaperBg)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Store Header on Thermal Paper
            Text(
                text = storeName.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = receiptInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = storeAddress,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = receiptMutedInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Telp/WA: $storePhone",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = receiptMutedInk,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))
            ThermalDashedDivider(color = receiptMutedInk)
            Spacer(modifier = Modifier.height(6.dp))

            // Receipt Metadata Rows
            ThermalKeyValueRow("No. Nota", receipt.code, receiptInk, receiptMutedInk)
            ThermalKeyValueRow("Waktu", receipt.timestampLabel, receiptInk, receiptMutedInk)
            ThermalKeyValueRow("Kasir", receipt.cashierName, receiptInk, receiptMutedInk)
            ThermalKeyValueRow("Pelanggan", "${receipt.customerName} (${receipt.customerBadge})", receiptInk, receiptMutedInk)
            ThermalKeyValueRow("Pembayaran", receipt.method.label.uppercase(), receiptInk, receiptMutedInk, boldValue = true)

            Spacer(modifier = Modifier.height(6.dp))
            ThermalDashedDivider(color = receiptMutedInk)
            Spacer(modifier = Modifier.height(6.dp))

            // Line Items Table Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ITEM BELANJA",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = receiptMutedInk
                )
                Text(
                    text = "SUBTOTAL",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = receiptMutedInk
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Each Line Item in Thermal Format
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                receipt.lineItems.forEach { item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.name,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = receiptInk,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "  ${item.qtyFormatted} ${item.unit} x ${formatRupiah(item.unitPrice)}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = receiptMutedInk
                            )
                            Text(
                                text = formatRupiah(item.subtotal),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = receiptInk
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            ThermalDashedDivider(color = receiptMutedInk)
            Spacer(modifier = Modifier.height(6.dp))

            // Subtotal & Discount
            val totalQtyUnits = receipt.lineItems.sumOf { Math.ceil(it.qty).toInt() }
            ThermalKeyValueRow(
                label = "Subtotal (${receipt.lineItems.size} Jenis / $totalQtyUnits Pcs)",
                value = formatRupiah(receipt.subtotalBeforeDiscount),
                inkColor = receiptInk,
                mutedColor = receiptMutedInk
            )
            if (receipt.discountAmount > 0L) {
                ThermalKeyValueRow(
                    label = "Diskon Pelanggan",
                    value = "-${formatRupiah(receipt.discountAmount)}",
                    inkColor = Primary,
                    mutedColor = receiptMutedInk,
                    boldValue = true
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL BAYAR",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = receiptInk
                )
                Text(
                    text = formatRupiah(receipt.totalAmount),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = receiptInk
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            ThermalKeyValueRow(
                label = when (receipt.method) {
                    PaymentMethod.QRIS -> "DIBAYAR (QRIS)"
                    PaymentMethod.KASBON -> "TUNAI DITERIMA"
                    else -> "TUNAI DITERIMA"
                },
                value = formatRupiah(receipt.tenderedAmount),
                inkColor = receiptInk,
                mutedColor = receiptMutedInk,
                boldValue = true
            )

            if (receipt.remainingKasbonAmount > 0L) {
                ThermalKeyValueRow(
                    label = "SISA MASUK KASBON",
                    value = formatRupiah(receipt.remainingKasbonAmount),
                    inkColor = Secondary,
                    mutedColor = receiptMutedInk,
                    boldValue = true
                )
            } else {
                ThermalKeyValueRow(
                    label = "KEMBALIAN",
                    value = formatRupiah(receipt.changeAmount),
                    inkColor = Primary,
                    mutedColor = receiptMutedInk,
                    boldValue = true
                )
            }

            if (receipt.deliveryNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                ThermalDashedDivider(color = receiptMutedInk)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan Pesanan: ${receipt.deliveryNote}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = receiptMutedInk,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            ThermalDashedDivider(color = receiptMutedInk)
            Spacer(modifier = Modifier.height(8.dp))

            // Simulated 1D Barcode Graphic + Code
            SimulatedThermalBarcode(code = receipt.code, inkColor = receiptInk)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "*** TERIMA KASIH ATAS KUNJUNGAN ANDA ***",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = receiptInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Barang yang sudah dibeli tidak dapat ditukar\nkecuali ada perjanjian tertulis.",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                color = receiptMutedInk,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Bottom Serrated Paper Edge
        SerratedPaperEdge(isTop = false, paperColor = receiptPaperBg)
    }
}

@Composable
private fun ThermalKeyValueRow(
    label: String,
    value: String,
    inkColor: Color,
    mutedColor: Color,
    boldValue: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = mutedColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = if (boldValue) FontWeight.Bold else FontWeight.Medium,
            color = inkColor,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ThermalDashedDivider(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
    }
}

@Composable
private fun SimulatedThermalBarcode(
    code: String,
    inkColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .height(34.dp)
        ) {
            val pattern = intArrayOf(3, 1, 2, 2, 1, 3, 1, 1, 2, 3, 1, 2, 1, 3, 2, 1, 2, 1, 3, 1, 2, 2, 1, 3, 1, 2, 1, 2, 3)
            val totalUnits = pattern.sum() * 2
            val unitWidth = size.width / totalUnits
            var currentX = 0f
            pattern.forEachIndexed { index, units ->
                val barW = units * unitWidth
                drawRect(
                    color = inkColor,
                    topLeft = Offset(currentX, 0f),
                    size = androidx.compose.ui.geometry.Size(barW, size.height)
                )
                currentX += barW + (if (index % 2 == 0) unitWidth * 1.4f else unitWidth * 2.2f)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            color = inkColor
        )
    }
}

@Composable
private fun SerratedPaperEdge(
    isTop: Boolean,
    paperColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(SurfaceContainerHigh.copy(alpha = 0.55f))
    ) {
        val toothWidth = 18f
        val toothCount = (size.width / toothWidth).toInt().coerceAtLeast(10)
        val actualToothW = size.width / toothCount
        val path = Path().apply {
            if (isTop) {
                moveTo(0f, size.height)
                for (i in 0 until toothCount) {
                    val xMid = (i + 0.5f) * actualToothW
                    val xEnd = (i + 1f) * actualToothW
                    lineTo(xMid, 0f)
                    lineTo(xEnd, size.height)
                }
                close()
            } else {
                moveTo(0f, 0f)
                for (i in 0 until toothCount) {
                    val xMid = (i + 0.5f) * actualToothW
                    val xEnd = (i + 1f) * actualToothW
                    lineTo(xMid, size.height)
                    lineTo(xEnd, 0f)
                }
                close()
            }
        }
        drawPath(path = path, color = paperColor)
    }
}
