package com.jnetaol.screenai.ui.screens.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.screenai.ui.components.*
import com.jnetaol.screenai.ui.screens.AppViewModel
import com.jnetaol.screenai.ui.theme.*

@Composable
fun ExportScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val result by viewModel.currentResult.collectAsState()
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

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
                text = "Export Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        result?.let { analysis ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                NeonCard(borderColor = NeonOrange) {
                    SectionHeader(title = "Copy to Clipboard", accentColor = NeonOrange)
                    Spacer(modifier = Modifier.height(12.dp))

                    ExportActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "Copy Full Analysis",
                        subtitle = "Complete analysis report",
                        color = NeonOrange,
                        onClick = {
                            clipboard.setPrimaryClip(ClipData.newPlainText("Analysis", analysis.explanation))
                            Toast.makeText(context, "Analysis copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )

                    ExportActionButton(
                        icon = Icons.Default.TextFields,
                        label = "Copy Extracted Text",
                        subtitle = "OCR extracted text only",
                        color = AccentCyan,
                        onClick = {
                            clipboard.setPrimaryClip(ClipData.newPlainText("Extracted Text", analysis.extractedText))
                            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )

                    ExportActionButton(
                        icon = Icons.Default.LabelOutline,
                        label = "Copy Tags",
                        subtitle = analysis.tags.joinToString(", "),
                        color = NeonPink,
                        onClick = {
                            clipboard.setPrimaryClip(ClipData.newPlainText("Tags", analysis.tags.joinToString(", ")))
                            Toast.makeText(context, "Tags copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )

                    ExportActionButton(
                        icon = Icons.Default.Code,
                        label = "Copy Raw Text",
                        subtitle = "Raw extracted text without formatting",
                        color = CodeColor,
                        onClick = {
                            clipboard.setPrimaryClip(ClipData.newPlainText("Raw Text", analysis.rawText))
                            Toast.makeText(context, "Raw text copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                NeonCard(borderColor = NeonPink) {
                    SectionHeader(title = "Share", accentColor = NeonPink)
                    Spacer(modifier = Modifier.height(12.dp))

                    ExportActionButton(
                        icon = Icons.Default.Share,
                        label = "Share Analysis",
                        subtitle = "Share as text",
                        color = NeonPink,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "ScreenAI Analysis: ${analysis.type.displayName}")
                                putExtra(Intent.EXTRA_TEXT, buildShareText(analysis))
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Analysis"))
                        }
                    )

                    ExportActionButton(
                        icon = Icons.Default.Description,
                        label = "Share Extracted Text",
                        subtitle = "Share OCR text only",
                        color = AccentCyan,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "ScreenAI Extracted Text")
                                putExtra(Intent.EXTRA_TEXT, analysis.extractedText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Text"))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                NeonCard {
                    SectionHeader(title = "Preview", accentColor = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = DarkBackground
                    ) {
                        Text(
                            text = buildShareText(analysis).take(500),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Default.FileOpen,
                    title = "Nothing to export",
                    subtitle = "Analyze a screenshot first, then export the results",
                    actionText = "Go Back",
                    onAction = onBack
                )
            }
        }
    }
}

@Composable
private fun ExportActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 15.sp)
                Text(text = subtitle, color = TextTertiary, fontSize = 12.sp, maxLines = 1)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = color.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
    }
}

private fun buildShareText(result: com.jnetaol.screenai.data.model.AnalysisResult): String {
    val sb = StringBuilder()
    sb.appendLine("ScreenAI Analysis Report")
    sb.appendLine("═══════════════════════════════")
    sb.appendLine("Category: ${result.type.displayName}")
    sb.appendLine("Tags: ${result.tags.joinToString(", ")}")
    sb.appendLine()
    sb.appendLine("Detected Elements:")
    result.detectedElements.forEach { sb.appendLine("  • $it") }
    sb.appendLine()
    sb.appendLine("Analysis:")
    sb.appendLine(result.explanation)
    sb.appendLine()
    sb.appendLine("Made with ScreenAI by jnetaol.com")
    return sb.toString()
}
