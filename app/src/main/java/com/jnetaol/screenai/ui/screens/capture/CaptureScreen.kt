package com.jnetaol.screenai.ui.screens.capture

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jnetaol.screenai.ui.components.*
import com.jnetaol.screenai.ui.screens.AppViewModel
import com.jnetaol.screenai.ui.theme.*

@Composable
fun CaptureScreen(
    viewModel: AppViewModel,
    onAnalyze: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedUri by viewModel.selectedImageUri.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.selectImage(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
            }
            Text(
                text = "Import Screenshot",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (selectedUri != null) {
                IconButton(onClick = { viewModel.resetCurrent() }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedUri == null) {
            EmptyState(
                icon = Icons.Default.Screenshot,
                title = "No image selected",
                subtitle = "Choose a screenshot from your gallery or import an image file",
                actionText = "Select Image",
                onAction = { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            NeonCard {
                Text(
                    text = "How to use",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonOrange
                )
                Spacer(modifier = Modifier.height(12.dp))
                InstructionStep("1", "Take a screenshot of anything - errors, code, UI, forms, or math")
                InstructionStep("2", "Import the screenshot here or capture directly")
                InstructionStep("3", "ScreenAI analyzes the content automatically")
                InstructionStep("4", "View detailed analysis results and extract text")
            }

            Spacer(modifier = Modifier.height(16.dp))

            NeonCard(borderColor = NeonPinkDim) {
                Text(
                    text = "Supported Content",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonPink
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SupportChip("Error\nMessages", ErrorColor, Icons.Default.BugReport)
                        Spacer(modifier = Modifier.height(8.dp))
                        SupportChip("Code\nBlocks", CodeColor, Icons.Default.Code)
                        Spacer(modifier = Modifier.height(8.dp))
                        SupportChip("UI\nElements", UiElementColor, Icons.Default.Widgets)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        SupportChip("Math\nEquations", MathColor, Icons.Default.Calculate)
                        Spacer(modifier = Modifier.height(8.dp))
                        SupportChip("Form\nFields", FormColor, Icons.Default.EditNote)
                        Spacer(modifier = Modifier.height(8.dp))
                        SupportChip("General\nContent", GeneralColor, Icons.Default.ImageSearch)
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f),
                shape = RoundedCornerShape(16.dp)
            ) {
                AsyncImage(
                    model = selectedUri,
                    contentDescription = "Selected screenshot",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change", fontSize = 14.sp)
                }

                GlowButton(
                    text = if (isAnalyzing) "Analyzing..." else "Analyze",
                    icon = Icons.Default.PlayArrow,
                    onClick = onAnalyze,
                    enabled = !isAnalyzing && selectedUri != null,
                    modifier = Modifier.weight(1f)
                )
            }

            if (isAnalyzing) {
                Spacer(modifier = Modifier.height(24.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = NeonOrange,
                    trackColor = NeonOrangeDim
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Analyzing screenshot content...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun InstructionStep(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(NeonOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = NeonOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SupportChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, fontSize = 11.sp, color = TextPrimary, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}
