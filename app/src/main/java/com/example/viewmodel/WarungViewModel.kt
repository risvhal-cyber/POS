package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WarungViewModel : ViewModel() {

    private val _currentTab = MutableStateFlow(MainTab.KASIR)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _navigationStack = MutableStateFlow<List<ScreenRoute>>(listOf(ScreenRoute.MainTabs))
    val navigationStack: StateFlow<List<ScreenRoute>> = _navigationStack.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _activeCompletedReceipt = MutableStateFlow<CompletedTransactionReceipt?>(null)
    val activeCompletedReceipt: StateFlow<CompletedTransactionReceipt?> = _activeCompletedReceipt.asStateFlow()

    // Store & Theme Settings
    private val _storeName = MutableStateFlow("Warung Laufi Kelontong")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _storeAddress = MutableStateFlow("Jl. Mawar No. 12, RT 03/05")
    val storeAddress: StateFlow<String> = _storeAddress.asStateFlow()

    private val _storePhone = MutableStateFlow("0812-3456-7890")
    val storePhone: StateFlow<String> = _storePhone.asStateFlow()

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _paperSize58mm = MutableStateFlow(true)
    val paperSize58mm: StateFlow<Boolean> = _paperSize58mm.asStateFlow()

    private val _autoPrint = MutableStateFlow(true)
    val autoPrint: StateFlow<Boolean> = _autoPrint.asStateFlow()

    // Active Cart Items (Matches Image 1 & Image 23)
    private val _cartItems = MutableStateFlow(
        listOf(
            CartItem(
                id = "item-beras-1kg",
                name = "Beras Ramos 1 Kg (Ecer)",
                price = 15000L,
                qty = 1.0,
                unit = "pcs",
                category = "Sembako",
                imageUrl = WarungImages.BERAS_GULA,
                badgeText = "Timbangan Pas",
                note = "Kemasan karung putih bersih"
            ),
            CartItem(
                id = "item-beras-ecer",
                name = "Beras Ramos (Ecer)",
                price = 15000L,
                qty = 2.0,
                unit = "kg",
                category = "Sembako",
                imageUrl = WarungImages.BERAS_SCOOP,
                badgeText = "Timbangan Pas"
            ),
            CartItem(
                id = "item-minyak",
                name = "Minyak Goreng Kita 1L",
                price = 16500L,
                qty = 2.0,
                unit = "pch",
                category = "Minyak",
                imageUrl = WarungImages.MINYAK_KITA_2,
                badgeText = "Stok Toko: 18",
                note = "Harga HET Pemerintah Terverifikasi"
            ),
            CartItem(
                id = "item-mie",
                name = "Indomie Goreng",
                price = 3500L,
                qty = 5.0,
                unit = "bks",
                category = "Mie Instan",
                imageUrl = WarungImages.INDOMIE,
                badgeText = "Stok Toko: 48"
            )
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _selectedCustomerName = MutableStateFlow("Ibu Anisa (Langganan Tetap)")
    val selectedCustomerName: StateFlow<String> = _selectedCustomerName.asStateFlow()

    private val _deliveryNote = MutableStateFlow("Diantar ke rumah Bu RT Rohanah nanti sore pukul 16:30")
    val deliveryNote: StateFlow<String> = _deliveryNote.asStateFlow()

    private val _discountAmount = MutableStateFlow(5000L)
    val discountAmount: StateFlow<Long> = _discountAmount.asStateFlow()

    private val _initialTenderedAmount = MutableStateFlow(100000L)
    val initialTenderedAmount: StateFlow<Long> = _initialTenderedAmount.asStateFlow()

    // POS Quick Selectable & Scannable Product Catalog
    val posCatalog = listOf(
        PosCatalogItem(
            id = "cat-minyak-kita",
            barcode = "8992775211025",
            name = "Minyak Goreng Kita 1L",
            price = 16500L,
            unit = "pch",
            category = "Sembako & Eceran",
            stockLabel = "Stok: 18 Pch",
            imageUrl = WarungImages.MINYAK_KITA_2,
            badgeText = "HET Resmi",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-beras-1kg",
            barcode = "8991002104512",
            name = "Beras Ramos 1 Kg (Ecer)",
            price = 15000L,
            unit = "kg",
            category = "Sembako & Eceran",
            stockLabel = "Stok: 85 Kg",
            imageUrl = WarungImages.BERAS_SCOOP,
            badgeText = "Timbangan Pas",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-telur-1kg",
            barcode = "8991002998120",
            name = "Telur Ayam Ras 1 Kg",
            price = 28000L,
            unit = "kg",
            category = "Sembako & Eceran",
            stockLabel = "Stok: 14 Kg",
            imageUrl = WarungImages.TELUR_1,
            badgeText = "Segar Utuh",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-indomie",
            barcode = "089686010947",
            name = "Indomie Goreng Spesial",
            price = 3500L,
            unit = "bks",
            category = "Mie & Makanan",
            stockLabel = "Stok: 48 Bks",
            imageUrl = WarungImages.INDOMIE,
            badgeText = "Terlaris",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-gulaku",
            barcode = "8995177101012",
            name = "Gula Pasir Gulaku 1 Kg",
            price = 17500L,
            unit = "bks",
            category = "Sembako & Eceran",
            stockLabel = "Stok: 15 Bks",
            imageUrl = WarungImages.BERAS_GULA,
            badgeText = "Murni",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-gas-3kg",
            barcode = "8990011223344",
            name = "Gas LPG 3 Kg Melon (Isi)",
            price = 21000L,
            unit = "tbg",
            category = "Gas & Galon",
            stockLabel = "Stok: 14 Tbg",
            imageUrl = WarungImages.GAS_LPG_2,
            badgeText = "Tukar Tabung",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-aqua-galon",
            barcode = "8886008101091",
            name = "Galon Aqua 19L (Refill)",
            price = 20000L,
            unit = "gln",
            category = "Gas & Galon",
            stockLabel = "Stok: 18 Gln",
            imageUrl = WarungImages.GALON_AQUA_2,
            badgeText = "Segel Asli",
            isPopular = true
        ),
        PosCatalogItem(
            id = "cat-le-minerale",
            barcode = "8996001600269",
            name = "Galon Le Minerale 15L",
            price = 19000L,
            unit = "gln",
            category = "Minuman Dingin",
            stockLabel = "Stok: 12 Gln",
            imageUrl = WarungImages.LE_MINERALE,
            badgeText = "Bebas Galon",
            isPopular = false
        ),
        PosCatalogItem(
            id = "cat-rokok-mild",
            barcode = "8999909002517",
            name = "Sampoerna Mild 16",
            price = 34000L,
            unit = "bks",
            category = "Rokok",
            stockLabel = "Stok: 10 Bks",
            imageUrl = WarungImages.BERAS_GULA,
            badgeText = "Cukai Baru",
            isPopular = false
        ),
        PosCatalogItem(
            id = "cat-sabun-cuci",
            barcode = "8998866200318",
            name = "Sunlight Jeruk Nipis 650ml",
            price = 12500L,
            unit = "pch",
            category = "Sabun & Bumbu",
            stockLabel = "Stok: 22 Pch",
            imageUrl = WarungImages.MINYAK_KITA_1,
            badgeText = "Hemat",
            isPopular = false
        )
    )

    // Stock Items (Matches Image 9)
    private val _stockItems = MutableStateFlow(
        listOf(
            StockItem(
                id = "stk-minyak",
                name = "Minyak Goreng Kita 1L",
                category = "Sembako Curah",
                stockQty = 2.0,
                minLimit = 10.0,
                unit = "Pch",
                wholesalePrice = 14500L,
                sellingPrice = 16500L,
                supplier = "Agen Sembako Barokah",
                imageUrl = WarungImages.MINYAK_KITA_1,
                restockPrimaryLabel = "+1 Dus (12 pch)",
                restockPrimaryAddQty = 12.0,
                restockSecondaryLabel = "+2 Dus (24)",
                restockSecondaryAddQty = 24.0,
                isCritical = true,
                packSize = 12,
                packUnitName = "Dus"
            ),
            StockItem(
                id = "stk-gas",
                name = "Gas LPG 3kg Melon",
                category = "Gas & Galon",
                stockQty = 3.0,
                minLimit = 10.0,
                emptyQty = 19,
                unit = "Tabung",
                wholesalePrice = 18500L,
                sellingPrice = 21000L,
                supplier = "Pangkalan Gas H. Syamsul",
                imageUrl = WarungImages.GAS_LPG_1,
                restockPrimaryLabel = "+ Tukar 20 Tabung",
                restockPrimaryAddQty = 20.0,
                restockSecondaryLabel = "Catat Kiriman",
                restockSecondaryAddQty = 5.0,
                isCritical = true,
                packSize = 20,
                packUnitName = "Dropping"
            ),
            StockItem(
                id = "stk-telur",
                name = "Telur Ayam Ras (Curah)",
                category = "Sembako Curah",
                stockQty = 3.5,
                minLimit = 15.0,
                unit = "Kg",
                wholesalePrice = 24500L,
                sellingPrice = 28000L,
                supplier = "Peternak Blitar Langsung",
                imageUrl = WarungImages.TELUR_1,
                restockPrimaryLabel = "+ 1 Peti Kayu (15 Kg)",
                restockPrimaryAddQty = 15.0,
                isCritical = true,
                packSize = 15,
                packUnitName = "Peti"
            ),
            StockItem(
                id = "stk-aqua",
                name = "Galon Aqua 19L (Refill)",
                category = "Gas & Galon",
                stockQty = 4.0,
                minLimit = 10.0,
                emptyQty = 16,
                unit = "Galon",
                wholesalePrice = 19000L,
                sellingPrice = 22000L,
                supplier = "Depo Resmi Aqua Sejahtera",
                imageUrl = WarungImages.GALON_AQUA_1,
                restockPrimaryLabel = "+ Truk Datang (20 Galon)",
                restockPrimaryAddQty = 20.0,
                isCritical = true,
                packSize = 20,
                packUnitName = "Truk"
            ),
            StockItem(
                id = "stk-indomie",
                name = "Indomie Goreng Spesial",
                category = "Jajanan & Minuman",
                stockQty = 48.0,
                minLimit = 15.0,
                unit = "Bks",
                wholesalePrice = 2800L,
                sellingPrice = 3100L,
                supplier = "Agen Grosir Makmur",
                imageUrl = WarungImages.INDOMIE,
                restockPrimaryLabel = "+ 1 Dus (40)",
                restockPrimaryAddQty = 40.0,
                isCritical = false,
                packSize = 40,
                packUnitName = "Dus"
            ),
            StockItem(
                id = "stk-beras",
                name = "Beras Ramos Premium",
                category = "Sembako Curah",
                stockQty = 85.0,
                minLimit = 25.0,
                unit = "Kg",
                wholesalePrice = 12800L,
                sellingPrice = 14000L,
                supplier = "Gudang Beras Karawang",
                imageUrl = WarungImages.BERAS_SCOOP,
                restockPrimaryLabel = "+ 1 Karung (50kg)",
                restockPrimaryAddQty = 50.0,
                isCritical = false,
                packSize = 50,
                packUnitName = "Karung"
            ),
            StockItem(
                id = "stk-gulaku",
                name = "Gula Pasir Gulaku Hijau 1kg",
                category = "Sembako Curah",
                stockQty = 15.0,
                minLimit = 10.0,
                unit = "Bks",
                wholesalePrice = 15800L,
                sellingPrice = 17500L,
                supplier = "Agen Sembako Barokah",
                imageUrl = WarungImages.BERAS_GULA,
                restockPrimaryLabel = "+ Tambah",
                restockPrimaryAddQty = 10.0,
                isCritical = false,
                packSize = 24,
                packUnitName = "Dus"
            ),
            StockItem(
                id = "stk-rokok",
                name = "Sampoerna Mild 16",
                category = "Rokok",
                stockQty = 10.0,
                minLimit = 5.0,
                unit = "Bks",
                wholesalePrice = 31500L,
                sellingPrice = 34000L,
                supplier = "Sales Grosir Rokok",
                imageUrl = null,
                restockPrimaryLabel = "+ 1 Slop (10)",
                restockPrimaryAddQty = 10.0,
                isCritical = false,
                packSize = 10,
                packUnitName = "Slop"
            )
        )
    )
    val stockItems: StateFlow<List<StockItem>> = _stockItems.asStateFlow()

    private val _categories = MutableStateFlow(
        listOf("Sembako Curah", "Gas & Galon", "Rokok", "Jajanan & Minuman", "Mie & Makanan", "Sabun & Bumbu")
    )
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _stockMutationLogs = MutableStateFlow(
        listOf(
            StockMutationLog(
                id = "mut-1",
                itemName = "Indomie Goreng Spesial",
                type = StockMutationType.KULAKAN_MASUK,
                qtyDelta = 40.0,
                unit = "Bks",
                stockBefore = 8.0,
                stockAfter = 48.0,
                timeLabel = "Pagi ini, 08:30",
                referenceNote = "Kulakan 1 Dus • Agen Grosir Makmur"
            ),
            StockMutationLog(
                id = "mut-2",
                itemName = "Beras Ramos Premium",
                type = StockMutationType.TERJUAL_KASIR,
                qtyDelta = -2.0,
                unit = "Kg",
                stockBefore = 87.0,
                stockAfter = 85.0,
                timeLabel = "17:15 WIB",
                referenceNote = "#TRX-20240524-0042 (Ibu Anisa)"
            ),
            StockMutationLog(
                id = "mut-3",
                itemName = "Gas LPG 3kg Melon",
                type = StockMutationType.TERJUAL_KASIR,
                qtyDelta = -2.0,
                unit = "Tabung",
                stockBefore = 5.0,
                stockAfter = 3.0,
                timeLabel = "16:40 WIB",
                referenceNote = "#TRX-20240524-0041 (Mas Dian)"
            )
        )
    )
    val stockMutationLogs: StateFlow<List<StockMutationLog>> = _stockMutationLogs.asStateFlow()

    // Transactions History (Matches Image 11)
    private val _transactions = MutableStateFlow(
        listOf(
            TransactionRecord(
                id = "trx-1",
                code = "#TRX-20240524-0042",
                customerName = "Ibu Anisa",
                customerBadge = "Langganan",
                timeWib = "17:15 WIB",
                timeGroup = "Sore Ini (15:00 - 18:00)",
                totalAmount = 64000L,
                method = PaymentMethod.TUNAI,
                itemsSummary = "2x Beras Ramos 1kg, 1x Minyak Kita 2L, 1x Gula",
                totalItemsCount = "4 Item",
                footerLeft = "Uang Diterima: Rp 100.000",
                footerRight = "Kembalian: Rp 36.000",
                previews = listOf(
                    TransactionItemPreview("Beras & Gula", "3 Item", WarungImages.BERAS_GULA),
                    TransactionItemPreview("Minyak Kita 2L", "1 Pcs", null)
                ),
                lineItems = listOf(
                    ReceiptLineItem("Beras Ramos 1 Kg (Ecer)", 2.0, "kg", 15000L, 30000L, "Timbangan Pas"),
                    ReceiptLineItem("Minyak Goreng Kita 1L", 1.0, "pch", 16500L, 16500L, "Harga HET"),
                    ReceiptLineItem("Gula Pasir Gulaku 1 Kg", 1.0, "bks", 17500L, 17500L)
                ),
                subtotalBeforeDiscount = 64000L,
                discountAmount = 0L,
                tenderedAmount = 100000L,
                changeAmount = 36000L
            ),
            TransactionRecord(
                id = "trx-2",
                code = "#TRX-20240524-0041",
                customerName = "Mas Dian (Bengkel)",
                customerBadge = "Tempo 7 Hari",
                timeWib = "16:40 WIB",
                timeGroup = "Sore Ini (15:00 - 18:00)",
                totalAmount = 76000L,
                method = PaymentMethod.KASBON,
                itemsSummary = "2x Gas LPG 3kg Melon (Tukar), 1x Rokok Surya 16",
                totalItemsCount = "3 Item",
                footerLeft = "Jatuh Tempo: 31 Mei 2024",
                footerRight = "Total Bon Mas Dian: Rp 210.000",
                previews = listOf(
                    TransactionItemPreview("Gas LPG Melon", "2 Tabung", null),
                    TransactionItemPreview("Rokok Surya 16", "1 Bks", null)
                ),
                isKasbonPending = true,
                lineItems = listOf(
                    ReceiptLineItem("Gas LPG 3kg Melon (Tukar Tabung)", 2.0, "tbg", 21000L, 42000L),
                    ReceiptLineItem("Rokok Surya Gudang Garam 16", 1.0, "bks", 34000L, 34000L)
                ),
                subtotalBeforeDiscount = 76000L,
                discountAmount = 0L,
                tenderedAmount = 0L,
                changeAmount = 0L,
                remainingKasbonAmount = 76000L
            ),
            TransactionRecord(
                id = "trx-3",
                code = "#TRX-20240524-0040",
                customerName = "Pak RT Bambang",
                customerBadge = "QRIS Statis Toko",
                timeWib = "15:20 WIB",
                timeGroup = "Sore Ini (15:00 - 18:00)",
                totalAmount = 73500L,
                method = PaymentMethod.QRIS,
                itemsSummary = "1x Telur Ayam Curah 2kg, 5x Indomie Goreng",
                totalItemsCount = "6 Item",
                footerLeft = "Ref: QRIS-BCA-98124910",
                footerRight = "Dana Masuk Otomatis",
                previews = listOf(
                    TransactionItemPreview("Telur Curah", "2 Kg", WarungImages.TELUR_2),
                    TransactionItemPreview("Indomie Goreng", "5 Bks", WarungImages.INDOMIE)
                ),
                lineItems = listOf(
                    ReceiptLineItem("Telur Ayam Ras Curah", 2.0, "kg", 28000L, 56000L),
                    ReceiptLineItem("Indomie Goreng Spesial", 5.0, "bks", 3500L, 17500L)
                ),
                subtotalBeforeDiscount = 73500L,
                discountAmount = 0L,
                tenderedAmount = 73500L,
                changeAmount = 0L
            ),
            TransactionRecord(
                id = "trx-4",
                code = "#TRX-20240524-0039",
                customerName = "Bu Siti (Tetangga Blkg)",
                customerBadge = "Split Bayar",
                timeWib = "14:05 WIB",
                timeGroup = "Siang Hari (11:00 - 14:59)",
                totalAmount = 68000L,
                method = PaymentMethod.SPLIT_BON,
                itemsSummary = "1x Minyak Goreng 2L, 1x Terigu Segitiga Biru 1kg",
                totalItemsCount = "2 Item",
                footerLeft = "Tunai: Rp 20.000",
                footerRight = "Masuk Bon: Rp 48.000",
                previews = listOf(
                    TransactionItemPreview("Terigu Segitiga", "1 Kg", WarungImages.BERAS_GULA),
                    TransactionItemPreview("Minyak Goreng", "2 Liter", null)
                ),
                isKasbonPending = true,
                lineItems = listOf(
                    ReceiptLineItem("Minyak Goreng Kita 2L", 1.0, "pch", 33000L, 33000L),
                    ReceiptLineItem("Terigu Segitiga Biru 1kg", 1.0, "bks", 15000L, 15000L),
                    ReceiptLineItem("Galon Aqua 19L (Refill)", 1.0, "gln", 20000L, 20000L)
                ),
                subtotalBeforeDiscount = 68000L,
                discountAmount = 0L,
                tenderedAmount = 20000L,
                changeAmount = 0L,
                remainingKasbonAmount = 48000L
            ),
            TransactionRecord(
                id = "trx-5",
                code = "#TRX-20240524-0035",
                customerName = "Pelanggan Umum",
                customerBadge = "Tunai Pas",
                timeWib = "11:30 WIB",
                timeGroup = "Siang Hari (11:00 - 14:59)",
                totalAmount = 15000L,
                method = PaymentMethod.TUNAI,
                itemsSummary = "Manual Eceran: Bumbu Dapur Racik + Cabai Rawit",
                totalItemsCount = "Eceran",
                footerLeft = "Uang Pas: Rp 15.000",
                footerRight = "Kembalian: Rp 0",
                lineItems = listOf(
                    ReceiptLineItem("Bumbu Dapur Racik Komplit", 1.0, "pkt", 5000L, 5000L),
                    ReceiptLineItem("Cabai Rawit Merah Eceran (2 Ons)", 0.2, "kg", 50000L, 10000L)
                ),
                subtotalBeforeDiscount = 15000L,
                discountAmount = 0L,
                tenderedAmount = 15000L,
                changeAmount = 0L
            )
        )
    )
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    // Customers & Kasbon Ledger (Matches Image 17 & Image 25)
    private val _customers = MutableStateFlow(
        listOf(
            CustomerDebt(
                id = "cust-1",
                initials = "RT",
                name = "Pak RT Bambang",
                statusBadge = "Belum Lunas",
                addressPhone = "Jl. Mawar No. 4 • 0812-9844-xxxx",
                debtAmount = 175000L,
                lastItems = "Beras Ramos 5kg, Telur 1/2kg",
                lastTakenTime = "Terakhir ambil: Kemarin (16:20)",
                dueBadge = "Jatuh tempo: Besok",
                isOverdue = false,
                isPaid = false,
                avatarUrl = WarungImages.AVATAR_PAK_RT,
                categoryTag = "Tetap",
                plafonLimit = 300000L
            ),
            CustomerDebt(
                id = "cust-2",
                initials = "BS",
                name = "Bu Siti",
                statusBadge = "Menunggu Bayar",
                addressPhone = "Tetangga Gang 3 No. 14",
                debtAmount = 68000L,
                lastItems = "Gas LPG 3kg, Gula Pasir 1kg",
                lastTakenTime = "Terakhir ambil: 24 Mei",
                dueBadge = "Tempo: 3 hari lagi",
                isOverdue = false,
                isPaid = false,
                avatarUrl = WarungImages.AVATAR_BU_SITI,
                categoryTag = "Tempo Dekat",
                plafonLimit = 200000L
            ),
            CustomerDebt(
                id = "cust-3",
                initials = "MD",
                name = "Mas Dian Bengkel",
                statusBadge = "Lewat 3 Hari",
                addressPhone = "Bengkel Depan Gapura",
                debtAmount = 230000L,
                lastItems = "Rokok Surya 2 bks, Kopi Kapal Api 5 sachet, Pocari",
                lastTakenTime = "Jatuh tempo: 3 hari lalu",
                dueBadge = "Perlu Follow-up",
                isOverdue = true,
                isPaid = false,
                avatarUrl = WarungImages.AVATAR_MAS_DIAN,
                categoryTag = "Kasbon",
                plafonLimit = 250000L,
                friendlyAlert = "Tempo lewat tanggal 20. Sapa santai saat mampir ngopi sore nanti."
            ),
            CustomerDebt(
                id = "cust-4",
                initials = "BR",
                name = "Bu Rina Perumahan",
                statusBadge = "LUNAS",
                addressPhone = "Perum Graha Indah Blok C2",
                debtAmount = 0L,
                lastItems = "Lunas baru saja (Rp 95.000 via Tunai)",
                lastTakenTime = "Hari ini",
                dueBadge = "Lunas",
                isOverdue = false,
                isPaid = true,
                avatarUrl = WarungImages.AVATAR_BU_RT,
                categoryTag = "Lunas",
                plafonLimit = 200000L
            ),
            CustomerDebt(
                id = "cust-5",
                initials = "RR",
                name = "Bu RT Rohanah",
                statusBadge = "VIP Tunai",
                addressPhone = "Rumah Utama RT 01",
                debtAmount = 0L,
                lastItems = "Pelanggan VIP Tunai Lunas (14 Belanja / bln)",
                lastTakenTime = "Hari ini",
                dueBadge = "Status Bersih",
                isOverdue = false,
                isPaid = true,
                avatarUrl = WarungImages.AVATAR_BU_RT,
                categoryTag = "Tetap",
                plafonLimit = 500000L
            ),
            CustomerDebt(
                id = "cust-6",
                initials = "JH",
                name = "Pak Joko Hansip",
                statusBadge = "Kasbon Jaga Malam",
                addressPhone = "Pos Ronda Samping Warung",
                debtAmount = 45000L,
                lastItems = "Kopi Sachet 4x, Biskuit Kaleng",
                lastTakenTime = "Tadi malam",
                dueBadge = "Sisa Plafon Rp 155.000",
                isOverdue = false,
                isPaid = false,
                avatarUrl = WarungImages.AVATAR_PAK_JOKO,
                categoryTag = "Kasbon",
                plafonLimit = 200000L
            ),
            CustomerDebt(
                id = "cust-7",
                initials = "AK",
                name = "Bu Ani Katering",
                statusBadge = "Grosir",
                addressPhone = "Dapur Berkah Gang Melati",
                debtAmount = 0L,
                lastItems = "Plafon Bon Grosir: Rp 2.000.000 (Tersedia Penuh)",
                lastTakenTime = "Minggu lalu",
                dueBadge = "Lunas",
                isOverdue = false,
                isPaid = true,
                avatarUrl = WarungImages.AVATAR_BU_ANI,
                categoryTag = "Grosir",
                plafonLimit = 2000000L
            )
        )
    )
    val customers: StateFlow<List<CustomerDebt>> = _customers.asStateFlow()

    // Gas & Galon Catalog (Matches Image 19)
    val gasGallonCatalog = listOf(
        GasGallonProduct(
            id = "gg-1",
            name = "Gas 3 Kg Subsidi",
            subtitle = "Gas Melon Hijau",
            refillPrice = 21000L,
            newCylinderPrice = 165000L,
            stockFilled = 14,
            stockEmpty = 8,
            unitType = "Gas LPG",
            imageUrl = WarungImages.GAS_LPG_2,
            isBestSeller = true
        ),
        GasGallonProduct(
            id = "gg-2",
            name = "Bright Gas 5.5 Kg",
            subtitle = "Pink Komersil",
            refillPrice = 105000L,
            newCylinderPrice = 360000L,
            stockFilled = 4,
            stockEmpty = 2,
            unitType = "Gas LPG",
            imageUrl = WarungImages.BRIGHT_GAS_5_5
        ),
        GasGallonProduct(
            id = "gg-3",
            name = "Galon Aqua 19L",
            subtitle = "Refill Air Mineral",
            refillPrice = 20000L,
            newCylinderPrice = 55000L,
            stockFilled = 18,
            stockEmpty = 12,
            unitType = "Galon Air",
            imageUrl = WarungImages.GALON_AQUA_2
        ),
        GasGallonProduct(
            id = "gg-4",
            name = "Le Minerale 15L",
            subtitle = "Galon Sekali Pakai",
            refillPrice = 19000L,
            newCylinderPrice = 19000L,
            stockFilled = 12,
            stockEmpty = 0,
            unitType = "Galon Air",
            imageUrl = WarungImages.LE_MINERALE
        ),
        GasGallonProduct(
            id = "gg-5",
            name = "Bright Gas 12 Kg",
            subtitle = "Rumah Tangga/Resto",
            refillPrice = 220000L,
            newCylinderPrice = 650000L,
            stockFilled = 3,
            stockEmpty = 2,
            unitType = "Gas LPG",
            imageUrl = WarungImages.BRIGHT_GAS_12
        ),
        GasGallonProduct(
            id = "gg-6",
            name = "Galon Vit / Cleo",
            subtitle = "Air Minum 19 Liter",
            refillPrice = 16000L,
            newCylinderPrice = 48000L,
            stockFilled = 9,
            stockEmpty = 5,
            unitType = "Galon Air",
            imageUrl = WarungImages.GALON_VIT
        )
    )

    val curahPresets = listOf(
        CurahPreset("cur-1", "Beras Eceran", 15000L, "Kg"),
        CurahPreset("cur-2", "Telur Ayam", 28000L, "Kg", "1/2kg 14rb"),
        CurahPreset("cur-3", "Gula Pasir", 17000L, "Kg"),
        CurahPreset("cur-4", "Minyak Curah", 16000L, "Kg"),
        CurahPreset("cur-5", "Tepung Terigu", 12000L, "Kg"),
        CurahPreset("cur-6", "Cabai Rawit", 50000L, "Kg", "Rp 5rb/ons")
    )

    // Navigation & Tab Actions
    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        _navigationStack.value = listOf(ScreenRoute.MainTabs)
    }

    fun navigateTo(route: ScreenRoute) {
        _navigationStack.update { it + route }
    }

    fun navigateBack() {
        _navigationStack.update { stack ->
            if (stack.size > 1) stack.dropLast(1) else stack
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Cart Operations
    fun addToCart(name: String, price: Long, qty: Double = 1.0, unit: String = "pcs", category: String = "Sembako", imageUrl: String? = null, note: String? = null) {
        _cartItems.update { current ->
            val existingIdx = current.indexOfFirst { it.name.equals(name, ignoreCase = true) && it.price == price }
            if (existingIdx >= 0) {
                val item = current[existingIdx]
                current.toMutableList().apply {
                    set(existingIdx, item.copy(qty = item.qty + qty))
                }
            } else {
                listOf(
                    CartItem(
                        id = "item-${System.currentTimeMillis()}",
                        name = name,
                        price = price,
                        qty = qty,
                        unit = unit,
                        category = category,
                        imageUrl = imageUrl ?: WarungImages.BERAS_GULA,
                        note = note
                    )
                ) + current
            }
        }
        showToast("✓ $name ditambahkan ke keranjang!")
    }

    fun updateCartItemQty(id: String, delta: Double) {
        _cartItems.update { current ->
            val idx = current.indexOfFirst { it.id == id }
            if (idx == -1) return@update current
            val item = current[idx]
            val newQty = item.qty + delta
            if (newQty <= 0.05) {
                showToast("${item.name} dihapus dari keranjang")
                current.filterNot { it.id == id }
            } else {
                current.toMutableList().apply {
                    set(idx, item.copy(qty = newQty))
                }
            }
        }
    }

    fun removeCartItem(id: String) {
        _cartItems.update { current ->
            val item = current.find { it.id == id }
            if (item != null) showToast("${item.name} dihapus")
            current.filterNot { it.id == id }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        showToast("Keranjang belanja telah dikosongkan")
    }

    fun getCartTotal(): Long {
        return _cartItems.value.sumOf { it.subtotal }
    }

    fun getCartTotalQty(): Int {
        return _cartItems.value.sumOf { Math.ceil(it.qty).toInt() }
    }

    fun updateDeliveryNote(note: String) {
        _deliveryNote.value = note
    }

    fun toggleDiscount() {
        _discountAmount.update { current ->
            val next = if (current > 0L) 0L else 5000L
            showToast(if (next > 0L) "Diskon Pelanggan Tetap Rp 5.000 diaktifkan" else "Diskon dilepas")
            next
        }
    }

    fun openPaymentWithPreset(tendered: Long) {
        _initialTenderedAmount.value = tendered
        navigateTo(ScreenRoute.PaymentCheckout)
    }

    fun scanOrSelectCatalogItem(item: PosCatalogItem, qty: Double = 1.0) {
        addToCart(
            name = item.name,
            price = item.price,
            qty = qty,
            unit = item.unit,
            category = item.category,
            imageUrl = item.imageUrl,
            note = item.badgeText
        )
    }

    fun dismissCompletedReceipt() {
        _activeCompletedReceipt.value = null
    }

    fun openTransactionReceiptDialog(trx: TransactionRecord, autoPrintRequest: Boolean = false) {
        val fallbackLines = if (trx.lineItems.isNotEmpty()) {
            trx.lineItems
        } else {
            listOf(
                ReceiptLineItem(
                    name = trx.itemsSummary,
                    qty = 1.0,
                    unit = "paket",
                    unitPrice = trx.totalAmount,
                    subtotal = trx.totalAmount
                )
            )
        }
        _activeCompletedReceipt.value = CompletedTransactionReceipt(
            transactionId = trx.id,
            code = trx.code,
            timestampLabel = "24 Mei 2025 • ${trx.timeWib}",
            customerName = trx.customerName,
            customerBadge = trx.customerBadge,
            method = trx.method,
            lineItems = fallbackLines,
            subtotalBeforeDiscount = trx.subtotalBeforeDiscount.coerceAtLeast(trx.totalAmount),
            discountAmount = trx.discountAmount,
            totalAmount = trx.totalAmount,
            tenderedAmount = trx.tenderedAmount,
            changeAmount = trx.changeAmount,
            remainingKasbonAmount = trx.remainingKasbonAmount,
            deliveryNote = trx.deliveryNote,
            printThermalRequested = autoPrintRequest || _autoPrint.value,
            sendWhatsappRequested = false,
            updatedStockItemsCount = fallbackLines.size
        )
    }

    fun completeCheckout(
        method: PaymentMethod,
        tenderedAmount: Long,
        customerName: String = _selectedCustomerName.value,
        printThermal: Boolean = _autoPrint.value,
        sendWhatsapp: Boolean = false
    ) {
        val rawCartSubtotal = getCartTotal().let { if (it > 0) it else 64000L }
        val activeDiscount = _discountAmount.value.coerceAtMost(rawCartSubtotal)
        val total = (rawCartSubtotal - activeDiscount).coerceAtLeast(0L).let { if (it > 0) it else rawCartSubtotal }
        val itemsList = _cartItems.value
        val receiptLines = if (itemsList.isNotEmpty()) {
            itemsList.map { cart ->
                ReceiptLineItem(
                    name = cart.name,
                    qty = cart.qty,
                    unit = cart.unit,
                    unitPrice = cart.price,
                    subtotal = cart.subtotal,
                    note = cart.note
                )
            }
        } else {
            listOf(
                ReceiptLineItem(
                    name = "Beras Ramos 1 Kg (Ecer)",
                    qty = 2.0,
                    unit = "kg",
                    unitPrice = 15000L,
                    subtotal = 30000L
                ),
                ReceiptLineItem(
                    name = "Minyak Goreng Kita 1L",
                    qty = 2.0,
                    unit = "pch",
                    unitPrice = 17000L,
                    subtotal = 34000L
                )
            )
        }

        val summaryStr = if (itemsList.isNotEmpty()) {
            itemsList.joinToString(", ") {
                val q = if (it.qty % 1.0 == 0.0) it.qty.toInt().toString() else it.qty.toString()
                "${q}x ${it.name}"
            }
        } else {
            "2x Beras Ramos 1 Kg, 2x Minyak Goreng Kita 1L"
        }
        val trxCode = "#TRX-20250524-00${_transactions.value.size + 43}"
        val cleanCustName = customerName.substringBefore(" (").ifBlank { "Pelanggan Umum" }
        val effectiveTendered = when (method) {
            PaymentMethod.KASBON -> 0L
            PaymentMethod.QRIS -> total
            else -> tenderedAmount
        }
        val change = (effectiveTendered - total).coerceAtLeast(0L)
        val remainingDebt = when {
            method == PaymentMethod.KASBON -> total
            method == PaymentMethod.SPLIT_BON && effectiveTendered < total -> (total - effectiveTendered).coerceAtLeast(0L)
            effectiveTendered < total -> (total - effectiveTendered).coerceAtLeast(0L)
            else -> 0L
        }
        val badgeLabel = when (method) {
            PaymentMethod.TUNAI -> "Tunai Lunas"
            PaymentMethod.QRIS -> "QRIS Toko"
            PaymentMethod.KASBON -> "Tempo 7 Hari"
            PaymentMethod.SPLIT_BON -> "Split Bayar"
        }

        // Pembaruan Stok Otomatis Setelah Transaksi Kasir
        var updatedStockCount = 0
        if (itemsList.isNotEmpty()) {
            val newMutations = mutableListOf<StockMutationLog>()
            _stockItems.update { currentStock ->
                currentStock.map { stock ->
                    val matchingCartQty = itemsList.filter { cart ->
                        val cName = cart.name.lowercase()
                        val sName = stock.name.lowercase()
                        cName.contains(sName.take(8)) || sName.contains(cName.take(8)) ||
                            (cName.contains("beras") && sName.contains("beras")) ||
                            (cName.contains("minyak") && sName.contains("minyak")) ||
                            (cName.contains("indomie") && sName.contains("indomie")) ||
                            (cName.contains("telur") && sName.contains("telur")) ||
                            (cName.contains("gas") && sName.contains("gas")) ||
                            (cName.contains("aqua") && sName.contains("aqua")) ||
                            (cName.contains("gula") && sName.contains("gula")) ||
                            (cName.contains("mild") && sName.contains("mild"))
                    }.sumOf { it.qty }

                    if (matchingCartQty > 0.0) {
                        updatedStockCount++
                        val newStockQty = (stock.stockQty - matchingCartQty).coerceAtLeast(0.0)
                        val newEmptyQty = if (stock.emptyQty > 0) stock.emptyQty + matchingCartQty.toInt() else stock.emptyQty
                        newMutations.add(
                            StockMutationLog(
                                id = "mut-${System.currentTimeMillis()}-${stock.id}",
                                itemName = stock.name,
                                type = StockMutationType.TERJUAL_KASIR,
                                qtyDelta = -matchingCartQty,
                                unit = stock.unit,
                                stockBefore = stock.stockQty,
                                stockAfter = newStockQty,
                                timeLabel = "Baru saja",
                                referenceNote = "$trxCode ($cleanCustName)"
                            )
                        )
                        stock.copy(
                            stockQty = newStockQty,
                            emptyQty = newEmptyQty,
                            isCritical = newStockQty <= stock.minLimit
                        )
                    } else {
                        stock
                    }
                }
            }
            if (newMutations.isNotEmpty()) {
                _stockMutationLogs.update { newMutations + it }
            }
        }

        val newTrxId = "trx-${System.currentTimeMillis()}"
        val previews = itemsList.take(3).map {
            val q = if (it.qty % 1.0 == 0.0) it.qty.toInt().toString() else it.qty.toString()
            TransactionItemPreview(it.name, "$q ${it.unit}", it.imageUrl)
        }
        val newTrx = TransactionRecord(
            id = newTrxId,
            code = trxCode,
            customerName = cleanCustName,
            customerBadge = badgeLabel,
            timeWib = "Baru saja",
            timeGroup = "Sore Ini (15:00 - 18:00)",
            totalAmount = total,
            method = method,
            itemsSummary = summaryStr,
            totalItemsCount = "${receiptLines.size} Item",
            footerLeft = if (method == PaymentMethod.KASBON) "Jatuh Tempo: Minggu Depan" else "Uang Diterima: ${formatRupiah(effectiveTendered)}",
            footerRight = if (remainingDebt > 0L) "Masuk Bon: ${formatRupiah(remainingDebt)}" else "Kembalian: ${formatRupiah(change)}",
            previews = previews,
            isKasbonPending = method == PaymentMethod.KASBON || method == PaymentMethod.SPLIT_BON || remainingDebt > 0L,
            lineItems = receiptLines,
            subtotalBeforeDiscount = rawCartSubtotal,
            discountAmount = activeDiscount,
            tenderedAmount = effectiveTendered,
            changeAmount = change,
            remainingKasbonAmount = remainingDebt,
            deliveryNote = _deliveryNote.value
        )
        _transactions.update { listOf(newTrx) + it }

        if (remainingDebt > 0L) {
            recordNewKasbon(cleanCustName, summaryStr, remainingDebt, "Minggu Depan")
        }

        _activeCompletedReceipt.value = CompletedTransactionReceipt(
            transactionId = newTrxId,
            code = trxCode,
            timestampLabel = "24 Mei 2025 • 17:38 WIB",
            customerName = cleanCustName,
            customerBadge = badgeLabel,
            method = method,
            lineItems = receiptLines,
            subtotalBeforeDiscount = rawCartSubtotal,
            discountAmount = activeDiscount,
            totalAmount = total,
            tenderedAmount = effectiveTendered,
            changeAmount = change,
            remainingKasbonAmount = remainingDebt,
            deliveryNote = _deliveryNote.value,
            printThermalRequested = printThermal,
            sendWhatsappRequested = sendWhatsapp,
            updatedStockItemsCount = updatedStockCount
        )

        _cartItems.value = emptyList()
        val stockSuffix = if (updatedStockCount > 0) " • Stok $updatedStockCount barang diperbarui!" else ""
        showToast("Transaksi ${formatRupiah(total)} Berhasil!$stockSuffix")
        _navigationStack.value = listOf(ScreenRoute.MainTabs)
    }

    // Stock Operations (Barang Masuk Kulakan & Penyesuaian)
    fun restockItem(id: String, addQty: Double, sourceNote: String = "Kulakan Cepat") {
        _stockItems.update { list ->
            list.map { item ->
                if (item.id == id) {
                    val beforeQty = item.stockQty
                    val updatedQty = beforeQty + addQty
                    val updatedEmpty = if (item.emptyQty > 0) (item.emptyQty - addQty.toInt()).coerceAtLeast(0) else 0
                    _stockMutationLogs.update { logs ->
                        listOf(
                            StockMutationLog(
                                id = "mut-${System.currentTimeMillis()}",
                                itemName = item.name,
                                type = StockMutationType.KULAKAN_MASUK,
                                qtyDelta = addQty,
                                unit = item.unit,
                                stockBefore = beforeQty,
                                stockAfter = updatedQty,
                                timeLabel = "Baru saja",
                                referenceNote = "$sourceNote • ${item.supplier}"
                            )
                        ) + logs
                    }
                    showToast("Barang masuk: ${item.name} +${addQty.toInt()} ${item.unit} (Total: ${updatedQty.toInt()} ${item.unit})")
                    item.copy(
                        stockQty = updatedQty,
                        emptyQty = updatedEmpty,
                        isCritical = updatedQty <= item.minLimit
                    )
                } else item
            }
        }
    }

    fun addManualStock(
        name: String,
        category: String,
        addQty: Double,
        unit: String,
        wholesalePrice: Long,
        supplier: String,
        sellingPriceOverride: Long? = null,
        methodLabel: String = "Kulakan Baru"
    ) {
        val existing = _stockItems.value.find {
            it.name.equals(name, ignoreCase = true) ||
                it.name.contains(name.take(8), ignoreCase = true) ||
                name.contains(it.name.take(8), ignoreCase = true)
        }
        if (existing != null) {
            _stockItems.update { list ->
                list.map { item ->
                    if (item.id == existing.id) {
                        val beforeQty = item.stockQty
                        val updatedQty = beforeQty + addQty
                        val newSell = sellingPriceOverride ?: item.sellingPrice
                        _stockMutationLogs.update { logs ->
                            listOf(
                                StockMutationLog(
                                    id = "mut-${System.currentTimeMillis()}",
                                    itemName = item.name,
                                    type = if (methodLabel.contains("Opname", ignoreCase = true)) StockMutationType.OPNAME_KOREKSI else StockMutationType.KULAKAN_MASUK,
                                    qtyDelta = addQty,
                                    unit = item.unit,
                                    stockBefore = beforeQty,
                                    stockAfter = updatedQty,
                                    timeLabel = "Baru saja",
                                    referenceNote = "$methodLabel • $supplier (Modal ${formatRupiah(wholesalePrice)})"
                                )
                            ) + logs
                        }
                        showToast("Stok ${item.name} ditambah +${addQty.toInt()} ${item.unit} dari $supplier!")
                        item.copy(
                            stockQty = updatedQty,
                            category = category.ifBlank { item.category },
                            unit = unit.ifBlank { item.unit },
                            wholesalePrice = wholesalePrice,
                            sellingPrice = newSell,
                            supplier = supplier.ifBlank { item.supplier },
                            isCritical = updatedQty <= item.minLimit
                        )
                    } else item
                }
            }
        } else {
            val calcSelling = sellingPriceOverride ?: (Math.round(wholesalePrice * 1.18 / 500.0) * 500L)
            val newItem = StockItem(
                id = "stk-${System.currentTimeMillis()}",
                name = name,
                category = category,
                stockQty = addQty,
                minLimit = 5.0,
                unit = unit,
                wholesalePrice = wholesalePrice,
                sellingPrice = calcSelling,
                supplier = supplier.ifBlank { "Agen Pasar Grosir" },
                imageUrl = WarungImages.MINYAK_KITA_1,
                restockPrimaryLabel = "+1 Dus (12)",
                restockPrimaryAddQty = 12.0,
                isCritical = addQty <= 5.0
            )
            _stockItems.update { listOf(newItem) + it }
            _stockMutationLogs.update { logs ->
                listOf(
                    StockMutationLog(
                        id = "mut-${System.currentTimeMillis()}",
                        itemName = newItem.name,
                        type = StockMutationType.KULAKAN_MASUK,
                        qtyDelta = addQty,
                        unit = unit,
                        stockBefore = 0.0,
                        stockAfter = addQty,
                        timeLabel = "Baru saja",
                        referenceNote = "Barang Baru ($methodLabel) • ${newItem.supplier}"
                    )
                ) + logs
            }
            showToast("Barang baru $name (+${addQty.toInt()} $unit) disimpan ke stok!")
        }
    }

    fun addCategory(newCat: String) {
        if (newCat.isNotBlank() && !_categories.value.contains(newCat.trim())) {
            _categories.update { it + newCat.trim() }
            showToast("Kategori '$newCat' ditambahkan")
        }
    }

    fun removeCategory(cat: String) {
        _categories.update { it.filterNot { c -> c == cat } }
        showToast("Kategori '$cat' dihapus")
    }

    fun saveWholesaleCalcToCatalog(name: String, hpp: Long, sellingPrice: Long, packQty: Int) {
        val existing = _stockItems.value.find { it.name.contains(name.take(8), ignoreCase = true) }
        if (existing != null) {
            val beforeQty = existing.stockQty
            val afterQty = beforeQty + packQty
            _stockItems.update { list ->
                list.map {
                    if (it.id == existing.id) {
                        it.copy(wholesalePrice = hpp, sellingPrice = sellingPrice, stockQty = afterQty, isCritical = afterQty <= it.minLimit)
                    } else it
                }
            }
            _stockMutationLogs.update { logs ->
                listOf(
                    StockMutationLog(
                        id = "mut-${System.currentTimeMillis()}",
                        itemName = existing.name,
                        type = StockMutationType.KULAKAN_MASUK,
                        qtyDelta = packQty.toDouble(),
                        unit = existing.unit,
                        stockBefore = beforeQty,
                        stockAfter = afterQty,
                        timeLabel = "Baru saja",
                        referenceNote = "Kalkulator Kulakan (+$packQty ${existing.unit} • HPP ${formatRupiah(hpp)})"
                    )
                ) + logs
            }
        } else {
            val newItem = StockItem(
                id = "stk-${System.currentTimeMillis()}",
                name = name.ifBlank { "Barang Kulakan Baru" },
                category = "Sembako Curah",
                stockQty = packQty.toDouble(),
                minLimit = 5.0,
                unit = "Pcs",
                wholesalePrice = hpp,
                sellingPrice = sellingPrice,
                supplier = "Grosir Pasar",
                restockPrimaryLabel = "+1 Pak ($packQty)",
                restockPrimaryAddQty = packQty.toDouble(),
                isCritical = false
            )
            _stockItems.update { listOf(newItem) + it }
            _stockMutationLogs.update { logs ->
                listOf(
                    StockMutationLog(
                        id = "mut-${System.currentTimeMillis()}",
                        itemName = newItem.name,
                        type = StockMutationType.KULAKAN_MASUK,
                        qtyDelta = packQty.toDouble(),
                        unit = "Pcs",
                        stockBefore = 0.0,
                        stockAfter = packQty.toDouble(),
                        timeLabel = "Baru saja",
                        referenceNote = "Kalkulator Kulakan Baru • HPP ${formatRupiah(hpp)}"
                    )
                ) + logs
            }
        }
        showToast("Kulakan $name (+$packQty pcs) masuk stok! Harga jual: ${formatRupiah(sellingPrice)}")
    }

    // Kasbon & Customer Operations
    fun payCustomerDebt(customerId: String, payAmount: Long) {
        _customers.update { list ->
            list.map { cust ->
                if (cust.id == customerId) {
                    val remaining = (cust.debtAmount - payAmount).coerceAtLeast(0L)
                    val isNowPaid = remaining == 0L
                    showToast("Alhamdulillah! Pembayaran ${cust.name} ${formatRupiah(payAmount)} tercatat.")
                    cust.copy(
                        debtAmount = remaining,
                        isPaid = isNowPaid,
                        isOverdue = if (isNowPaid) false else cust.isOverdue,
                        statusBadge = if (isNowPaid) "LUNAS" else "Cicilan Masuk",
                        dueBadge = if (isNowPaid) "Lunas" else cust.dueBadge
                    )
                } else cust
            }
        }
    }

    fun recordNewKasbon(customerName: String, itemsDesc: String, amount: Long, dueDateText: String) {
        val existing = _customers.value.find { it.name.contains(customerName, ignoreCase = true) }
        if (existing != null) {
            _customers.update { list ->
                list.map { cust ->
                    if (cust.id == existing.id) {
                        cust.copy(
                            debtAmount = cust.debtAmount + amount,
                            isPaid = false,
                            statusBadge = "Belum Lunas",
                            lastItems = itemsDesc.ifBlank { "Belanja sembako warung" },
                            lastTakenTime = "Baru saja",
                            dueBadge = "Tempo: ${dueDateText.ifBlank { "7 Hari" }}"
                        )
                    } else cust
                }
            }
        } else {
            val initials = customerName.trim().split(" ").take(2).joinToString("") { it.take(1).uppercase() }.ifEmpty { "PL" }
            val newCust = CustomerDebt(
                id = "cust-${System.currentTimeMillis()}",
                initials = initials,
                name = customerName.ifBlank { "Tetangga Baru" },
                statusBadge = "Belum Lunas",
                addressPhone = "Warga Sekitar Warung",
                debtAmount = amount,
                lastItems = itemsDesc.ifBlank { "Belanja sembako harian" },
                lastTakenTime = "Baru saja",
                dueBadge = "Tempo: ${dueDateText.ifBlank { "Minggu Depan" }}",
                isOverdue = false,
                isPaid = false,
                avatarUrl = WarungImages.AVATAR_BU_SITI
            )
            _customers.update { listOf(newCust) + it }
        }
        showToast("Catatan kasbon $customerName (${formatRupiah(amount)}) disimpan!")
    }

    fun addNewNeighborCustomer(name: String, phone: String, address: String, limit: Long) {
        val initials = name.trim().split(" ").take(2).joinToString("") { it.take(1).uppercase() }.ifEmpty { "WG" }
        val newCust = CustomerDebt(
            id = "cust-${System.currentTimeMillis()}",
            initials = initials,
            name = name,
            statusBadge = "Tetap",
            addressPhone = "$address • $phone",
            debtAmount = 0L,
            lastItems = "Pelanggan baru terdaftar",
            lastTakenTime = "Hari ini",
            dueBadge = "Plafon: ${formatRupiah(limit)}",
            isOverdue = false,
            isPaid = true,
            avatarUrl = WarungImages.AVATAR_PAK_RT,
            categoryTag = "Tetap",
            plafonLimit = limit
        )
        _customers.update { listOf(newCust) + it }
        showToast("Warga baru $name berhasil didaftarkan!")
    }

    fun toggleDarkTheme(dark: Boolean) {
        _darkTheme.value = dark
        showToast(if (dark) "Tema Gelap Kasir Aktif" else "Tema Terang Kasir Aktif")
    }

    fun setPaperSize58(is58: Boolean) {
        _paperSize58mm.value = is58
        showToast(if (is58) "Ukuran kertas diatur ke 58mm" else "Ukuran kertas diatur ke 80mm")
    }

    fun setAutoPrint(enabled: Boolean) {
        _autoPrint.value = enabled
    }

    fun updateStoreProfile(name: String, address: String, phone: String) {
        _storeName.value = name
        _storeAddress.value = address
        _storePhone.value = phone
        showToast("Profil toko berhasil diperbarui!")
    }
}
