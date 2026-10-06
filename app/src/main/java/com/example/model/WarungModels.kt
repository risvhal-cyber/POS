package com.example.model

import java.text.NumberFormat
import java.util.Locale

enum class MainTab {
    RIWAYAT, STOK, KASIR, KASBON, LAPORAN
}

sealed class ScreenRoute {
    data object MainTabs : ScreenRoute()
    data object CartDetail : ScreenRoute()
    data object PaymentCheckout : ScreenRoute()
    data class ManualInput(val initialTab: Int = 0) : ScreenRoute() // 0: Eceran/Curah, 1: Ketik Bebas Rp, 2: Gas & Galon
    data class WholesaleCalculator(val openScanSheet: Boolean = false, val initialSkuName: String = "Korek Api Gas Tokai") : ScreenRoute()
    data object CustomerDirectory : ScreenRoute()
    data object Settings : ScreenRoute()
}

data class CartItem(
    val id: String,
    val name: String,
    val price: Long,
    val qty: Double,
    val unit: String = "pcs",
    val category: String = "Sembako",
    val imageUrl: String? = null,
    val badgeText: String? = null,
    val note: String? = null
) {
    val subtotal: Long
        get() = Math.round(price * qty)
}

data class StockItem(
    val id: String,
    val name: String,
    val category: String,
    val stockQty: Double,
    val minLimit: Double,
    val emptyQty: Int = 0,
    val unit: String,
    val wholesalePrice: Long,
    val sellingPrice: Long,
    val supplier: String,
    val imageUrl: String? = null,
    val restockPrimaryLabel: String,
    val restockPrimaryAddQty: Double,
    val restockSecondaryLabel: String? = null,
    val restockSecondaryAddQty: Double = 0.0,
    val isCritical: Boolean = false,
    val packSize: Int = 12,
    val packUnitName: String = "Dus"
)

enum class PaymentMethod(val label: String) {
    TUNAI("Tunai"),
    QRIS("QRIS"),
    KASBON("Kasbon"),
    SPLIT_BON("Sebagian Bon")
}

data class TransactionItemPreview(
    val label: String,
    val qtyLabel: String,
    val imageUrl: String? = null
)

data class ReceiptLineItem(
    val name: String,
    val qty: Double,
    val unit: String,
    val unitPrice: Long,
    val subtotal: Long,
    val note: String? = null
) {
    val qtyFormatted: String
        get() = if (qty % 1.0 == 0.0) qty.toInt().toString() else qty.toString()
}

data class CompletedTransactionReceipt(
    val transactionId: String,
    val code: String,
    val timestampLabel: String,
    val cashierName: String = "Kasir Warung #01",
    val customerName: String,
    val customerBadge: String,
    val method: PaymentMethod,
    val lineItems: List<ReceiptLineItem>,
    val subtotalBeforeDiscount: Long,
    val discountAmount: Long,
    val totalAmount: Long,
    val tenderedAmount: Long,
    val changeAmount: Long,
    val remainingKasbonAmount: Long = 0L,
    val deliveryNote: String = "",
    val printThermalRequested: Boolean = true,
    val sendWhatsappRequested: Boolean = false,
    val updatedStockItemsCount: Int = 0
)

data class TransactionRecord(
    val id: String,
    val code: String,
    val customerName: String,
    val customerBadge: String,
    val timeWib: String,
    val timeGroup: String, // "Sore Ini (15:00 - 18:00)" or "Siang Hari (11:00 - 14:59)"
    val totalAmount: Long,
    val method: PaymentMethod,
    val itemsSummary: String,
    val totalItemsCount: String,
    val footerLeft: String,
    val footerRight: String,
    val previews: List<TransactionItemPreview> = emptyList(),
    val isKasbonPending: Boolean = false,
    val lineItems: List<ReceiptLineItem> = emptyList(),
    val subtotalBeforeDiscount: Long = totalAmount,
    val discountAmount: Long = 0L,
    val tenderedAmount: Long = totalAmount,
    val changeAmount: Long = 0L,
    val remainingKasbonAmount: Long = 0L,
    val deliveryNote: String = ""
)

data class CustomerDebt(
    val id: String,
    val initials: String,
    val name: String,
    val statusBadge: String,
    val addressPhone: String,
    val debtAmount: Long,
    val lastItems: String,
    val lastTakenTime: String,
    val dueBadge: String,
    val isOverdue: Boolean = false,
    val isPaid: Boolean = false,
    val avatarUrl: String? = null,
    val categoryTag: String = "Tetap",
    val plafonLimit: Long = 200000L,
    val friendlyAlert: String? = null
)

data class GasGallonProduct(
    val id: String,
    val name: String,
    val subtitle: String,
    val refillPrice: Long,
    val newCylinderPrice: Long,
    val stockFilled: Int,
    val stockEmpty: Int,
    val unitType: String, // "Gas LPG" or "Galon Air"
    val imageUrl: String,
    val isBestSeller: Boolean = false
)

data class CurahPreset(
    val id: String,
    val name: String,
    val ratePerUnit: Long,
    val unit: String,
    val badge: String? = null
)

enum class StockMutationType(val label: String) {
    KULAKAN_MASUK("Kulakan Masuk"),
    TERJUAL_KASIR("Terjual Kasir"),
    OPNAME_KOREKSI("Opname / Koreksi")
}

data class StockMutationLog(
    val id: String,
    val itemName: String,
    val type: StockMutationType,
    val qtyDelta: Double,
    val unit: String,
    val stockBefore: Double,
    val stockAfter: Double,
    val timeLabel: String,
    val referenceNote: String
)

data class PosCatalogItem(
    val id: String,
    val barcode: String,
    val name: String,
    val price: Long,
    val unit: String,
    val category: String,
    val stockLabel: String,
    val imageUrl: String? = null,
    val badgeText: String? = null,
    val isPopular: Boolean = true
)

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(amount)}"
}

fun formatNumberOnly(amount: Long): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return formatter.format(amount)
}

object WarungImages {
    const val LOGO = "https://lh3.googleusercontent.com/aida/AEtjO1VZm_9a_UpHVDQ1hSi4K9hdQ1rNUx5rr1lXZ9GGIC1-2tYeAFxbFwjh0fKq6wVcHd0QoUgl8nHwxHan76dF1bGHt-OousCUlm4FWdVQHbpXAjG30Vie2NC9hh-8_Z0Igpv0mWFWcwMe2IAmRM8whp_TWM36Ku0ir8ZKzTdM2Lj0viibkNUf6CcqFbVJgEOJvoktW-SFJrDMcU-iiYcXM_4OnO0Jaj8LeUT_d7nblfegRI-zYDU90Xe3ti8o"
    const val MINYAK_KITA_1 = "https://lh3.googleusercontent.com/aida-public/AB6AXuDmBBHZgVCWBzy80YyBljOyMZxALaAen0-cx9VXE_VPn2j57zGD12BGRCecj4tHkjNRLKLlml5uCf0Qz1E08skt1PN3uPlqG3JTRZUW08co5QtP5xUNNjqAW3THqg27FHB-Jpl57lvs68URkTEnRsLItKp8B-99iLtWVf2dYPUE3VY_OUqNuX8cMFwAmlW2Fz9W44XH9U1sztSLYund45QbpElKHfhpq-LU5JLAsioMqbIKfpav7XNIpA"
    const val MINYAK_KITA_2 = "https://lh3.googleusercontent.com/aida-public/AB6AXuBnHdROpvyduXRYEzAJbYK45K94-k1NUQjPl8NCjQrvVT-AaZlOXs1cuF6Y1arSY4i7Vgejd8ugJlCxMe8P5vQNFvsHMIh6_Ncwo3bJkAIwfLQSWKwhVoNFIjxfU6_SkJKgqrYHEeS7gD6l54yjmfK-STqXXUGp_Zf0_r6bbcHKTJPoPOHUGS1iNTZJJfRtvN5dJ-P10wCdV5xQX388MMygHZKs6rRc-xVpG7qhBYVZKDmsGBVx3v-SCA"
    const val GAS_LPG_1 = "https://lh3.googleusercontent.com/aida-public/AB6AXuCmTSulUdmEHBsoxGEkNLBXdDmMZ1QqpPoNGa0CwHypbf58xXwemHSCQsIMzUyRGDkPQ2h8MtY1WtlL3WoGjMWmgfHiFNnoeYYwYjZ4va9PanR5wXWvyNHtuKLylUbI1yK8ihYXljX6KMWRda1j0ZfNkTWQarAHeAOmahVe-QuQGZ_rQpjK8prebBDHZLlRPIT2CgPxbXbAZbEpzPAmdmuJmPGG8IgBK_7ZZCggULIr_mImWc970KBN3g"
    const val GAS_LPG_2 = "https://lh3.googleusercontent.com/aida-public/AB6AXuB0JffG95d-AKZhvDssYax8vIhpj5k_otQCWcyktMHy92vXa_XaXZd4Z5NrgFwnrBbt-Ml85aPwZnRovuao8AuthnkhKFRDak0qDypDauTYiEe9bUS6XjbBDdN8BjapMmYw98O9yrO0U0cF40E36HZwN3gizAAZuP6Nr6fffaIxCmJBMFUptu_JysvjFH2RPVEisvx29IAUoDfsvcDH0dAof0RppgiCcU9aidNHGBjP9EH6d5HClOiwVw"
    const val BRIGHT_GAS_5_5 = "https://lh3.googleusercontent.com/aida-public/AB6AXuB0tO5f4pLESW-2o82YBHMTLQKF5flQ-SmXNH1x4W-YSDEbS8xZw0lR68A1D2cU15GOwW9qGDN0MlLF0M7abwI5WYyTx0Yipmgk8dIZ3663QQ2XJgM-uMn8gIoqkslpceSv-VMlThQW-B27aZg8nCjr8I9slOgr_D59b0aqvru_CAFXRBZ3jJd2suDlPKLXCO5DLs-FLFcZIC1WqB7P4pmCzUNX9duJJR4mcXd4uEQakZC4QOUmOOjNOA"
    const val BRIGHT_GAS_12 = "https://lh3.googleusercontent.com/aida-public/AB6AXuDxJ9ZE1IeLSBbXGeK1VxdLNPnf32Ybnj1wBYj6KFGyQrMQaTVgV5rlnedQTpLXC3enkW93mBynPrq-QEXMzIGLTveWY4d5qhtOCo22T8UnulPp_QDv5rIIy3E5HfEzobGWeldQflB0llSYkCQzF5QdRe-zKQ4SJE50B0elp6slUnZlpyeDw3D9FftTi-T86kUlyguUqMMnctXpJ86q4kd27arjBbIOVU7f1BgkImfrEbLkHbezgttZMQ"
    const val TELUR_1 = "https://lh3.googleusercontent.com/aida-public/AB6AXuCOgc0RdwASkcfd38ms4V_BBBSqnxHIAKGyRp9B60hI4FpFVDZ0r9TNWdR1z3chuIbUToK1PzvCSrXBTFJIUoTipBY6MpaWzNv1XvoXS9evahV7PjOct351nFmpXO85dQfGP0ls7iZkvNdhHbwOyNEkAbuJ6Vnl0NF4TeFFeCDAOr2AFP7dP533Dxfq6cC37CscnN3Z71brwtJFuXOOah59YMrbreOZaIagSb5CxnaTBzhIL9vBPVwAyg"
    const val TELUR_2 = "https://lh3.googleusercontent.com/aida-public/AB6AXuCJ97_uo6z8v8Ac9dVObEYXD4apvBzSG-f2mpSZQKR0hOpzinu-mmRvZ3kvecJxJhAJfMy1_ZDFJBdvvqQtvy6v-Wxjks_rLovGV6-x1srxpXKYds_Hf9FzzgInDCnchg5y45yGeM_axDXsRtaIt2hIV6jCAVBasM2MUxCtKi2Z8BwrR2JPywX67t_eJebS5C0WVomidjrJs_9DGiS8eHAj061_RnvkhT8RnlSl1g9Uq5HwJ2yfuvO4kg"
    const val GALON_AQUA_1 = "https://lh3.googleusercontent.com/aida-public/AB6AXuD4Zy_EI8Ptl7LTjyl__6kN-VvYv3Z8sDyKwUWFh0oUaBgB3xTQ18T-crlIS26eK-KsBLgbmeG8vCgsac1xjCei0bGHMRVnrP6oKVlzhttRSJRlIeQxwwDHqqL_FhvDerb4MyncsPHfsoYi9L-jwXDoRFpHv6d4GKdfEXxX36guevwngqa84vyan5GyK5pfAgYoPnHy8BSMlivcS8LFch3qnoGBleN9T-LF1IUa8iIZDYYsiPpBuWZ6JQ"
    const val GALON_AQUA_2 = "https://lh3.googleusercontent.com/aida-public/AB6AXuCPxRC68GMqdd6Nt6YHN4K29la2zpBLxqYaZh1IYdYm6lLCTzP1iBLvNrX_sDUwwMZN8eos8voBMH8mgmDRNSl4x77RAlTZauAv7O1ThZSL2NmoWer1Tif0T4WdRLaKH3h_QaxI3kDQs0o172l-ZL8Syk06t5Lh-red8k2o_RoNdASufS6R7wJcrn6-ZSH8pFiPwZP6vuakMJS-l9N1Q4Dv-cNAhrPG_HK0ObgGHY4SOnr9bbm0Y_mrmQ"
    const val LE_MINERALE = "https://lh3.googleusercontent.com/aida-public/AB6AXuBJxv_VyFM8rVh0tNdF8npR3JpQP2sbbTsmvOSxmd1yXHn9OaPl_6fTocLCrIg2F5vEcoLAcdfLrGIuGCCEih0nrm8TAMPSal3vv4dauapREvi5CWrWl5m_3pSn3YaE-nsnSljtl-5bMvlBskFo2K8OokM6MaXe8E-U2oefdX6Rc3kFk1bLAnt4gei9MNvbxLVrsCpfiyx9cvg9WCPl0m7y-_U2tLn9Lub9FyCm4n7T5MumjSJRUVPF7g"
    const val GALON_VIT = "https://lh3.googleusercontent.com/aida-public/AB6AXuBl7at1SMQfyzxzJ-mfhafvzcPmhitX8nTWy9tLWh8ln6b-VnsdZ88WOemU5Dp8MPADY7H6zXSCOPwWceRmEd4p5A-u0LtQ-kU-qkEnvyru93JWvE22x9ebpSM6DTtn5YMoA4OWjwXh5DObpFf2xxgqK212eCnWwnwBfPRMiJp_7oaSFp_Oo3P2j1ybt8ukWA7ErUP4PIMji4Wctwz3qPRdaCYXbbqJnha486OpDapdVP_svY1dDUppYg"
    const val BERAS_GULA = "https://lh3.googleusercontent.com/aida-public/AB6AXuCI8SuJ-xk-iC80_Yf98SOUSP8gCrVLIrKPmVrVvRlCEmc6mfNylUSFweolNjJ8JyBfsuYeSkeVHP23eUfufhwq46-Q2iV_Y_3C1RuFA9xrXD7XBvlHQtItKXRwWk_fiCJ3iG53Hs3crzXpI0mPyufQHTb98M1ROFjtJgfNRlKSFxJIyXW5I3UcvB1fQLFB-e9SJO_WYrtn077O8_36Yvrmoum75fOkiwW1azvDdmy1LnSVJcE91U2ahg"
    const val BERAS_SCOOP = "https://lh3.googleusercontent.com/aida-public/AB6AXuD8GuA_ejjjKRdhTic_O4delZYFC8EHBBqVpaKVpukbeAgekGcjXtm1GqwAfTwW9qFydET9eR0PlR8eNORIdWObjxAyrrYmLULvaHpY5GuSq42WtrPvLSOLhLdbMU2gQLX3-jJtR17tdZc9pQk1VVJgjqiv-6OGynv1effkgQ7l8cXR_YmDzta3OeIg4_atTWPfyavuMoGs72U1Mh4mSv4vsueT7bO_anE_HiDUZ3BYiVSP2h4B3fF2DQ"
    const val INDOMIE = "https://lh3.googleusercontent.com/aida-public/AB6AXuBlNTf60YMew5ITPkCpB50H2hBdRB0QuLNj33DRvyOQbHxnop3Prn9cKq3-GgQ9scUvlqqr6F7iOF7sTuBIVeBgHBdk7uIaOZEPbaNJErYoszA8Pxa6KcqAxVDX-LR4W65mYH9iUeTWk1hoYxNhm3JtOgvgPjhp0N8Lp2k3t7KD0ztfbYjG9dUZu062l3mvCbHAYaQEdT67o5Jrn2J_2Mse1BOqajXSkF8oNB4e79hmHZB81dQ3qdQ8xg"
    const val STORE_PROFILE = "https://lh3.googleusercontent.com/aida-public/AB6AXuC6GU4nu4My8Fic9xDTnJCvrb54HbKzof4oo-tNeC5DSxdVjNyqbJqhO19cy5htzD4LoFITDSmzeRKAmC5l9KizbxSPTRowjyYTObHS-DsvQKra8LEjAaPm_Gu-orJFfYEcK9HBp4GFntVyB97S3Pxz9zLSLDQHCNHK6nVW3DsFTMwp5K04sqMv-a18qUEvntTqFJZNcIjCbex_WmyD5kuFNUdwoFMCwiF4n2aXPQDyQ-kb7plDWaua7w"
    const val AVATAR_PAK_RT = "https://lh3.googleusercontent.com/aida-public/AB6AXuDo01ljZdmMLNNac95N_ec35C4ukpVSvRPfc_8FtOjz_GElpMvmKS4YhXOeAtoZPaTW_Y3gSUQM821OxkMTbs4S2CrDIdXGBTzdtBkoAakJ8ukzyiVAwT4R1rDBlvONc15InePdOCEiRNrFTKxYQKsEYPtKYlxJ4H5HPcrl45f036ce56znafXvouUZlzCLIuhzJBS3AGlbkpPt1KZNPopXiN5ekIEmguE49EmYbZSjGVed8fBiyo5wKg"
    const val AVATAR_BU_SITI = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnOXhu9X_krRVm9wlP2b3EnuUBHR214HILA3bzT8oHddS-mtcYkIwOPvmqpFG_z5T1AKAKujCGpg5TAWdc9sDPMi5BUbv4B7ZJN-r_pUJSkFGPaxZjx6dU3uITJtZbP_q9lpVy4Hhhjc1383cc0qVDFGGjsQCitxV5FtljjfCz9alZuJH_A6YvFI2xbJT57H1PtxRqxwyypAIL08LY0gfRmCqnH-jQiEy-hY1MM4gMOSzQFzHhPuGVpg"
    const val AVATAR_MAS_DIAN = "https://lh3.googleusercontent.com/aida-public/AB6AXuD6JMYitwat4nMiuSZ8fcO2dyh-ahZxWfoA_gK6XpS_qtQpsfVSmEohte5HOTVrnlyTHqi5Ya-7qhEU302YpypJwwkHaILlIBAyx1yC6M3KDEyqvllCNfgsu6VvuXTrq9NYCnKiUABuUW8bRadsAT4P6518epHZZRZePUHSmPzTqkmN1DyZkmXHuydkRVtp6ooO0-3VEKiJq1MbjJ5_ZP3cT9hgsdFl4ht_c3SWcFsgvrmrdO3GKzXwCg"
    const val AVATAR_BU_RT = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsM1DycpE3Lt-oEOAQ7Kx0WwNXyi658MrbsUI3_R3-2w5a9ZnsO-s6aWidYZIxTpM06Qh6Nayscx1esaYDxH5oRMI3u8OgBiL9WBQFYTp3-XFUesME68Ne1AY_GsHJE-6anFnJkxkO4caEqytXkRXRb5mDV_aHPDdUcwSmXUcut_ktbHhB1xdqtSDVDau-kiz2Up0E3M5r9gUlrLYwNt44wEeiwcfkNOzq9ZV4GuuRzsw6jIHNgiHjOg"
    const val AVATAR_PAK_JOKO = "https://lh3.googleusercontent.com/aida-public/AB6AXuDOOPNnyf9_2wY4csvBip87DQDh_rug-Q-xVL2lAGQVn01agkVtkPcOSu3sRF7jVf8Lwl4zW5WcvmiKC2Ajd0Bc2liTchk1Vgd_-83OjQm4QaEelsQSL8UZmDSGfbAfXMxk0ZGid_mIC0IsIvqC7jXCeWXrrs97KijttEHblx84qbKcTfVaZP3--s7wKUJAnSMIOiGZkkaRrhs58RsYs6EkPq-2e6KsMSll3Ieb9HL_7oR4VvzQ-q_Jug"
    const val AVATAR_BU_ANI = "https://lh3.googleusercontent.com/aida-public/AB6AXuBMXEJpARNihtbhwLuIrfNPnXwE2ShHL8nKIxATVm8Gi4FEuCv5CNc6CyoL-0x5Z-ReaTUaOKVz3PgEuoiWTyrKQ7FeoCEP2IEpr8ZLziA2O8skL09l7VzhwMr_xC354zW1RvlYrMzvZl5TJzT6O8W2Qn72ZBkN01tOCnhjxY9c8_6eJdD9rz_G_nUFEh6Ri9m24n-mhX-qxh4LwfaSc7bbWAatpq7dJz1iThxVMheSbzZN5bd_9p5sVA"
}
