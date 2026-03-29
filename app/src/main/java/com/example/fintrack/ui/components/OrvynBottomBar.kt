package com.example.fintrack.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomBarItemModel(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun OrvynBottomBar(
    items: List<BottomBarItemModel>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        BoxWithConstraints {
            val selectedIndex = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0
            val itemWidth = maxWidth / items.size
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selectedIndex,
                animationSpec = tween(280),
                label = "bottomNavIndicatorOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
            ) {
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .width(itemWidth)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .background(Color(0xFFF2F4FF))
                )
            }

            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                items.forEach { destination ->
                    val selected = currentRoute == destination.route
                    val iconScale by animateFloatAsState(
                        targetValue = if (selected) 1.15f else 1f,
                        label = "navIconScale"
                    )
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onNavigate(destination.route) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color(0xFF2B3141),
                            selectedTextColor = Color(0xFF2B3141),
                            unselectedIconColor = Color(0xFF8E95A3),
                            unselectedTextColor = Color(0xFF8E95A3)
                        ),
                        label = {
                            Text(
                                destination.label,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 11.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size((19 * iconScale).dp)
                            )
                        }
                    )
                }
            }
        }
    }
}
