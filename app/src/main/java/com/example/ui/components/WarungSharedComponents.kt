package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MainTab
import com.example.model.formatRupiah
import com.example.ui.theme.*

@Composable
fun WarungBottomNavigationBar(
    currentTab: MainTab,
    onSelectTab: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.98f),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(68.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Riwayat",
                    icon = Icons.Outlined.ReceiptLong,
                    activeIcon = Icons.Filled.ReceiptLong,
                    selected = currentTab == MainTab.RIWAYAT,
                    onClick = { onSelectTab(MainTab.RIWAYAT) },
                    testTag = "nav_riwayat",
                    modifier = Modifier.weight(1f)
                )
                BottomNavItem(
                    label = "Stok",
                    icon = Icons.Outlined.Inventory2,
                    activeIcon = Icons.Filled.Inventory2,
                    selected = currentTab == MainTab.STOK,
                    onClick = { onSelectTab(MainTab.STOK) },
                    testTag = "nav_stok",
                    modifier = Modifier.weight(1f)
                )
                // Center elevated Kasir button
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = (-8).dp)
                        .clickable { onSelectTab(MainTab.KASIR) }
                        .testTag("nav_kasir"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (currentTab == MainTab.KASIR) Primary else PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PointOfSale,
                            contentDescription = "Kasir",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kasir",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (currentTab == MainTab.KASIR) Primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BottomNavItem(
                    label = "Kasbon",
                    icon = Icons.Outlined.MenuBook,
                    activeIcon = Icons.Filled.MenuBook,
                    selected = currentTab == MainTab.KASBON,
                    onClick = { onSelectTab(MainTab.KASBON) },
                    testTag = "nav_kasbon",
                    modifier = Modifier.weight(1f)
                )
                BottomNavItem(
                    label = "Laporan",
                    icon = Icons.Outlined.BarChart,
                    activeIcon = Icons.Filled.BarChart,
                    selected = currentTab == MainTab.LAPORAN,
                    onClick = { onSelectTab(MainTab.LAPORAN) },
                    testTag = "nav_laporan",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    activeIcon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) Primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) activeIcon else icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            ),
            color = contentColor
        )
    }
}

@Composable
fun CartHeaderBadgeButton(
    itemCount: Int,
    totalAmount: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val displayCount = if (itemCount > 0) itemCount else 3
    val displayTotal = if (totalAmount > 0) totalAmount else 64000L

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("header_cart_button")
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF005323))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Keranjang Belanja",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            if (compact) {
                Text(
                    text = "$displayCount ITEM - ${formatRupiah(displayTotal)}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            } else {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "$displayCount ITEM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = PrimaryFixed
                    )
                    Text(
                        text = formatRupiah(displayTotal),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-4).dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(ErrorColor)
                .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayCount.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileAvatarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Person
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Primary)
            .testTag("profile_settings_button")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Pengaturan & Profil",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun NetworkProductImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    fallbackIcon: ImageVector = Icons.Outlined.Inventory2,
    fallbackTint: Color = Primary
) {
    Box(
        modifier = modifier.background(SurfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = fallbackIcon,
            contentDescription = null,
            tint = fallbackTint.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun FloatingToastBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = InverseSurface,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryFixed,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = message ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = InverseOnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
