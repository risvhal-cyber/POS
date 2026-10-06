package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.MainTab
import com.example.model.ScreenRoute
import com.example.model.formatRupiah
import com.example.ui.components.FloatingToastBanner
import com.example.ui.components.TransactionReceiptSuccessDialog
import com.example.ui.components.WarungBottomNavigationBar
import com.example.ui.screens.*
import com.example.ui.theme.WarungTheme
import com.example.viewmodel.WarungViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarungPosApp()
        }
    }
}

@Composable
fun WarungPosApp(
    viewModel: WarungViewModel = viewModel()
) {
    val isDarkTheme by viewModel.darkTheme.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val navStack by viewModel.navigationStack.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val activeCompletedReceipt by viewModel.activeCompletedReceipt.collectAsState()
    val storeName by viewModel.storeName.collectAsState()
    val storeAddress by viewModel.storeAddress.collectAsState()
    val storePhone by viewModel.storePhone.collectAsState()
    val paperSize58mm by viewModel.paperSize58mm.collectAsState()

    val currentRoute = navStack.lastOrNull() ?: ScreenRoute.MainTabs

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2300)
            viewModel.clearToast()
        }
    }

    WarungTheme(darkTheme = isDarkTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val route = currentRoute) {
                is ScreenRoute.MainTabs -> {
                    BackHandler(enabled = currentTab != MainTab.KASIR) {
                        viewModel.selectTab(MainTab.KASIR)
                    }
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            when (currentTab) {
                                MainTab.RIWAYAT -> RiwayatHistoryScreen(viewModel = viewModel)
                                MainTab.STOK -> StokManagementScreen(viewModel = viewModel)
                                MainTab.KASIR -> KasirPosScreen(viewModel = viewModel)
                                MainTab.KASBON -> KasbonLedgerScreen(viewModel = viewModel)
                                MainTab.LAPORAN -> LaporanAnalyticsScreen(viewModel = viewModel)
                            }
                        }
                        WarungBottomNavigationBar(
                            currentTab = currentTab,
                            onSelectTab = { viewModel.selectTab(it) }
                        )
                    }
                }
                is ScreenRoute.CartDetail -> {
                    CartDetailScreen(viewModel = viewModel)
                }
                is ScreenRoute.PaymentCheckout -> {
                    PaymentCheckoutScreen(viewModel = viewModel)
                }
                is ScreenRoute.ManualInput -> {
                    ManualInputSuiteScreen(
                        initialTab = route.initialTab,
                        viewModel = viewModel
                    )
                }
                is ScreenRoute.WholesaleCalculator -> {
                    WholesaleCalculatorScreen(
                        openScanSheetInitially = route.openScanSheet,
                        initialSkuName = route.initialSkuName,
                        viewModel = viewModel
                    )
                }
                is ScreenRoute.CustomerDirectory -> {
                    CustomerDirectoryScreen(viewModel = viewModel)
                }
                is ScreenRoute.Settings -> {
                    SettingsScreen(viewModel = viewModel)
                }
            }

            // Global Completed Transaction Detail & Printed Thermal Receipt Dialog
            activeCompletedReceipt?.let { receipt ->
                TransactionReceiptSuccessDialog(
                    receipt = receipt,
                    storeName = storeName,
                    storeAddress = storeAddress,
                    storePhone = storePhone,
                    paperSize58mm = paperSize58mm,
                    onDismiss = { viewModel.dismissCompletedReceipt() },
                    onPrintAgain = {
                        viewModel.showToast("Mencetak ulang struk ${receipt.code} ke Printer Thermal ${if (paperSize58mm) "58mm" else "80mm"}...")
                    },
                    onShareWhatsapp = {
                        viewModel.showToast("Mengirim resi digital ${receipt.code} (${formatRupiah(receipt.totalAmount)}) ke WhatsApp ${receipt.customerName}...")
                    },
                    onOpenHistory = {
                        viewModel.dismissCompletedReceipt()
                        viewModel.selectTab(MainTab.RIWAYAT)
                    }
                )
            }

            // Global Floating Toast Notification
            FloatingToastBanner(
                message = toastMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 64.dp)
            )
        }
    }
}
