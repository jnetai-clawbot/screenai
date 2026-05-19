package com.jnetaol.screenai.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.screenai.data.model.AnalysisType
import com.jnetaol.screenai.ui.components.*
import com.jnetaol.screenai.ui.screens.AppViewModel
import com.jnetaol.screenai.ui.theme.*

@Composable
fun ResultScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onExport: () -> Unit
) {
    val result by viewModel.currentResult.collectAsState()
    val analysisId by viewModel.currentAnalysisId.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
            }
            Text(
                text = "Analysis Result",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (analysisId != null) {
                IconButton(onClick = { viewModel.deleteAnalysis(analysisId!!); onBack() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed.copy(alpha = 0.7f))
                }
            }
        }

        result?.let { analysis ->
            val typeColor = when (analysis.type) {
                AnalysisType.ERROR -> ErrorColor
                AnalysisType.CODE -> CodeColor
                AnalysisType.UI_ELEMENT -> UiElementColor
                AnalysisType.MATH -> MathColor
                AnalysisType.FORM -> FormColor
                AnalysisType.GENERAL -> GeneralColor
                AnalysisType.OCR -> OcrColor
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                NeonCard(borderColor = typeColor) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnalysisTypeBadge(type = analysis.type)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = analysis.type.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = typeColor
                        )
                    }
                    if (analysis.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            analysis.tags.take(5).forEach { tag ->
                                StatusBadge(text = tag, color = typeColor)
                            }
                        }
                    }
                }

                if (analysis.detectedElements.isNotEmpty() && analysis.detectedElements.first() != "No specific elements detected") {
                    Spacer(modifier = Modifier.height(16.dp))
                    NeonCard {
                        SectionHeader(
                            title = "Detected Elements",
                            accentColor = AccentCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        analysis.detectedElements.forEach { element ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(AccentCyan)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = element, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                NeonCard {
                    SectionHeader(
                        title = "Analysis Explanation",
                        accentColor = NeonOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysis.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                NeonCard {
                    SectionHeader(
                        title = "Extracted Text",
                        accentColor = AccentYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = DarkBackground
                    ) {
                        Text(
                            text = analysis.extractedText,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlowButton(
                        text = "Export",
                        icon = Icons.Default.Share,
                        onClick = onExport,
                        modifier = Modifier.weight(1f),
                        containerColor = NeonPink
                    )
                    GlowButton(
                        text = "New Scan",
                        icon = Icons.Default.AddAPhoto,
                        onClick = {
                            viewModel.resetCurrent()
                            onBack()
                        },
                        modifier = Modifier.weight(1f),
                        containerColor = NeonOrange
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.Warning,
                    title = "No analysis loaded",
                    subtitle = "Analyze a screenshot to see results here",
                    actionText = "Analyze",
                    onAction = onBack
                )
            }
        }
    }
}
