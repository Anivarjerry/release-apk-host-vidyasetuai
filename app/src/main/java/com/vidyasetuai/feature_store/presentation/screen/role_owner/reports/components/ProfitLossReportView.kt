package com.vidyasetuai.feature_store.presentation.screen.role_owner.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.vidyasetuai.feature_store.domain.model.ProfitLossUiModel

@Composable
fun ProfitLossReportView(
    isHindi: Boolean = false,
    profitLoss: ProfitLossUiModel
) {
    val isProfitable = profitLoss.isProfitable
    val profitColor = if (isProfitable) Color(0xFF10B981) else Color(0xFFEF4444)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Primary Highlight Banner: Net Profit & Margin %
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, profitColor.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(profitColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isProfitable) Lucide.TrendingUp else Lucide.TrendingDown,
                                contentDescription = "P&L",
                                tint = profitColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (isHindi) "शुद्ध लाभ (Net Operating Profit)" else "NET OPERATING PROFIT (शुद्ध लाभ)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = profitColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${"%.1f".format(profitLoss.profitMarginPct)}% Margin",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = profitColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Text(
                    text = "₹${"%,.2f".format(profitLoss.netProfit)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = profitColor
                )

                Text(
                    text = "Gross Sales (₹${"%.2f".format(profitLoss.totalRevenue)}) - Purchase Cost (₹${"%.2f".format(profitLoss.cogsTotal)})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. P&L Metric Cards Grid (3 Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card 1: Total Sales Revenue
            PlMetricCard(
                title = "Total Sales Revenue",
                amount = profitLoss.totalRevenue,
                subtitle = "Total Bill Value from Customers",
                icon = Lucide.ShoppingBag,
                color = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )

            // Card 2: Cost of Goods (COGS)
            PlMetricCard(
                title = "Cost of Goods (खरीद लागत)",
                amount = profitLoss.cogsTotal,
                subtitle = "Supplier Purchase Cost of Sold Items",
                icon = Lucide.CircleDollarSign,
                color = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )

            // Card 3: Total Discounts Given
            PlMetricCard(
                title = "Total Discounts Given",
                amount = profitLoss.discountTotal,
                subtitle = "Customer Bill Discounts",
                icon = Lucide.Tag,
                color = Color(0xFFF43F5E),
                modifier = Modifier.weight(1f)
            )
        }

        // 3. P&L Financial Statement Calculation Sheet
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PROFIT & LOSS FINANCIAL STATEMENT CALCULATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Line A: Gross Sales Revenue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "A. सकल बिक्री (Gross Sales Revenue)" else "A. Gross Sales Revenue (सकल बिक्री)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "₹${"%,.2f".format(profitLoss.totalRevenue)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // Line B: Cost of Goods Sold (COGS)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "B. घटाएं: खरीद लागत (Less: Cost of Goods Sold)" else "B. Less: Cost of Goods Sold (बेचे गए सामान की खरीद लागत)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "-₹${"%,.2f".format(profitLoss.cogsTotal)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFF59E0B)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // Line C: Total Discounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "C. घटाएं: कुल छूट (Less: Total Discounts)" else "C. Less: Total Discounts (दी गई छूट)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "-₹${"%,.2f".format(profitLoss.discountTotal)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFF43F5E)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Line D: Net Operating Profit Highlight Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "D. Net Operating Profit = (A - B - C)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "₹${"%,.2f".format(profitLoss.netProfit)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = profitColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlMetricCard(
    title: String,
    amount: Double,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(12.dp))
                }
            }

            Text(
                text = "₹${"%,.2f".format(amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (color == Color(0xFFF43F5E)) color else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 10.sp
            )
        }
    }
}
