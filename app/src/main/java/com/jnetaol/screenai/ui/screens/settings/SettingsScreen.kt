package com.jnetaol.screenai.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.screenai.ui.components.*
import com.jnetaol.screenai.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val versionName = "1.0.1"

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            NeonCard(borderColor = NeonOrange) {
                SectionHeader(title = "Application", accentColor = NeonOrange)
                Spacer(modifier = Modifier.height(8.dp))

                SettingsInfoRow(
                    icon = Icons.Default.Info,
                    label = "Version",
                    value = versionName
                )

                SettingsActionRow(
                    icon = Icons.Default.SystemUpdate,
                    label = "Check For Updates",
                    subtitle = "You're running the latest version",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jnetai.com"))
                        context.startActivity(intent)
                    }
                )

                SettingsActionRow(
                    icon = Icons.Default.Share,
                    label = "Share ScreenAI",
                    subtitle = "Tell others about this app",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "ScreenAI - Screenshot Explainer")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out ScreenAI - an offline screenshot analyzer that explains errors, code, UI elements, math, and forms!\n\nMade by jnetai.com"
                            )
                        }
                        context.startActivity(Intent.createChooser(intent, "Share ScreenAI"))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeonCard {
                SectionHeader(title = "Features", accentColor = AccentCyan)
                Spacer(modifier = Modifier.height(8.dp))
                FeatureRow(Icons.Default.BugReport, "Error Detection", "Identifies crash logs and error messages")
                FeatureRow(Icons.Default.Code, "Code Analysis", "Parses code blocks and explains structure")
                FeatureRow(Icons.Default.Widgets, "UI Recognition", "Detects buttons, inputs, navigation")
                FeatureRow(Icons.Default.Calculate, "Math Solver", "Interprets equations and formulas")
                FeatureRow(Icons.Default.EditNote, "Form Analyzer", "Identifies form fields and inputs")
                FeatureRow(Icons.Default.TextFields, "Text Extraction", "Extracts readable text from images")
                FeatureRow(Icons.Default.Search, "Search History", "Searchable analysis history")
                FeatureRow(Icons.Default.Share, "Export & Share", "Copy, share, and export results")
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeonCard {
                SectionHeader(title = "Privacy", accentColor = NeonPink)
                Spacer(modifier = Modifier.height(8.dp))
                FeatureRow(
                    icon = Icons.Default.CloudOff,
                    label = "100% Offline",
                    subtitle = "All processing happens on your device"
                )
                FeatureRow(
                    icon = Icons.Default.Storage,
                    label = "Local Storage",
                    subtitle = "Analysis history stored only on this device"
                )
                FeatureRow(
                    icon = Icons.Default.Security,
                    label = "No Data Collection",
                    subtitle = "No analytics, no tracking, no cloud"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NeonOrange.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ScreenAI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NeonOrange
                    )
                    Text(
                        text = "Screenshot Explainer v$versionName",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jnetai.com"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = NeonOrange.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Made By jnetai.com",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = NeonOrange,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Intelligent screenshot analysis\nRun entirely on-device",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SettingsInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = NeonOrange,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = AccentCyan.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
        }
    }
}
