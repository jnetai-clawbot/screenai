package com.jnetaol.screenai.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.screenai.data.model.AnalysisType
import com.jnetaol.screenai.data.model.ScreenAnalysis
import com.jnetaol.screenai.ui.components.*
import com.jnetaol.screenai.ui.screens.AppViewModel
import com.jnetaol.screenai.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onAnalyze: () -> Unit,
    onViewAnalysis: (Long) -> Unit,
    onViewHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val totalCount by viewModel.analysisCount.collectAsState()
    val typeCounts by viewModel.typeCounts.collectAsState()
    val recentAnalyses by viewModel.allAnalyses.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ScreenAI",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonOrange
                    )
                    Text(
                        text = "Intelligent Screenshot Analysis",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        item {
            GlowButton(
                text = "Analyze Screenshot",
                icon = Icons.Default.AddAPhoto,
                onClick = onAnalyze,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            NeonCard {
                SectionHeader(
                    title = "Dashboard",
                    accentColor = NeonOrange
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardStatCard(
                        title = "Total",
                        count = totalCount,
                        color = NeonOrange,
                        icon = Icons.Default.Analytics,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Errors",
                        count = typeCounts["Error Detection"] ?: 0,
                        color = ErrorColor,
                        icon = Icons.Default.BugReport,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        title = "Code",
                        count = typeCounts["Code Analysis"] ?: 0,
                        color = CodeColor,
                        icon = Icons.Default.Code,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            NeonCard {
                SectionHeader(
                    title = "Analysis Types",
                    accentColor = NeonPink
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeChip("Error", ErrorColor, Icons.Default.BugReport, typeCounts["Error Detection"] ?: 0, Modifier.weight(1f))
                    TypeChip("Code", CodeColor, Icons.Default.Code, typeCounts["Code Analysis"] ?: 0, Modifier.weight(1f))
                    TypeChip("UI", UiElementColor, Icons.Default.Widgets, typeCounts["UI Elements"] ?: 0, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeChip("Math", MathColor, Icons.Default.Calculate, typeCounts["Math Equations"] ?: 0, Modifier.weight(1f))
                    TypeChip("Form", FormColor, Icons.Default.EditNote, typeCounts["Form Fields"] ?: 0, Modifier.weight(1f))
                    TypeChip("General", GeneralColor, Icons.Default.ImageSearch, typeCounts["General"] ?: 0, Modifier.weight(1f))
                }
            }
        }

        item {
            SectionHeader(
                title = "Recent Analyses",
                trailing = {
                    TextButton(onClick = onViewHistory) {
                        Text("View All", color = NeonOrange, fontSize = 13.sp)
                    }
                },
                accentColor = AccentCyan
            )
        }

        if (recentAnalyses.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Screenshot,
                    title = "No analyses yet",
                    subtitle = "Take or import a screenshot to get started with AI-powered analysis",
                    actionText = "Analyze Screenshot",
                    onAction = onAnalyze
                )
            }
        } else {
            items(recentAnalyses.size.coerceAtMost(5)) { index ->
                AnalysisListItem(
                    analysis = recentAnalyses[index],
                    onClick = { onViewAnalysis(recentAnalyses[index].id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DashboardStatCard(
    title: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Text(text = title, fontSize = 11.sp, color = color.copy(alpha = 0.7f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TypeChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Text(text = "$count", fontSize = 14.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnalysisListItem(
    analysis: ScreenAnalysis,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
    val type = try {
        AnalysisType.valueOf(analysis.analysisType.uppercase().replace(" ", "_"))
    } catch (_: Exception) {
        AnalysisType.GENERAL
    }

    NeonCard(
        modifier = Modifier.clickable(onClick = onClick),
        borderColor = when (type) {
            AnalysisType.ERROR -> ErrorColor
            AnalysisType.CODE -> CodeColor
            AnalysisType.UI_ELEMENT -> UiElementColor
            AnalysisType.MATH -> MathColor
            AnalysisType.FORM -> FormColor
            else -> ClearColor
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            AnalysisTypeBadge(type = type)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = analysis.extractedText.lines().firstOrNull() ?: "Screenshot Analysis",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(Date(analysis.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                if (analysis.tags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        analysis.tags.split(",").take(3).forEach { tag ->
                            StatusBadge(text = tag.trim(), color = TextTertiary)
                        }
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
