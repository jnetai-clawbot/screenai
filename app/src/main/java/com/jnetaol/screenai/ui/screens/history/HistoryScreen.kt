package com.jnetaol.screenai.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jnetaol.screenai.data.model.AnalysisType
import com.jnetaol.screenai.ui.components.*
import com.jnetaol.screenai.ui.screens.AppViewModel
import com.jnetaol.screenai.ui.screens.home.AnalysisListItem
import com.jnetaol.screenai.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: AppViewModel,
    onViewAnalysis: (Long) -> Unit,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val allAnalyses by viewModel.allAnalyses.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
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
                text = "History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (allAnalyses.isNotEmpty()) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = AccentRed.copy(alpha = 0.7f))
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text("Search analyses...", color = TextTertiary)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearSearch() }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonOrange.copy(alpha = 0.5f),
                unfocusedBorderColor = DarkSurfaceVariant,
                focusedContainerColor = DarkCard,
                unfocusedContainerColor = DarkCard
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.History,
                    title = if (searchQuery.isNotEmpty()) "No results found" else "No history yet",
                    subtitle = if (searchQuery.isNotEmpty())
                        "Try a different search term"
                    else
                        "Analyzed screenshots will appear here"
                )
            }
        } else {
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "${searchResults.size} result${if (searchResults.size != 1) "s" else ""} for \"$searchQuery\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(searchResults.size) { index ->
                    AnalysisListItem(
                        analysis = searchResults[index],
                        onClick = { onViewAnalysis(searchResults[index].id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Clear All History", fontWeight = FontWeight.SemiBold) },
            text = { Text("Are you sure you want to delete all analysis history? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllAnalyses()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Clear All", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = DarkSurface
        )
    }
}
