package com.jnetaol.screenai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jnetaol.screenai.ui.screens.AppViewModel
import com.jnetaol.screenai.ui.screens.capture.CaptureScreen
import com.jnetaol.screenai.ui.screens.export.ExportScreen
import com.jnetaol.screenai.ui.screens.history.HistoryScreen
import com.jnetaol.screenai.ui.screens.home.HomeScreen
import com.jnetaol.screenai.ui.screens.result.ResultScreen
import com.jnetaol.screenai.ui.screens.settings.SettingsScreen
import com.jnetaol.screenai.ui.theme.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Capture : Screen("capture", "Analyze", Icons.Default.AddAPhoto)
    object Result : Screen("result", "Result", Icons.Default.Description)
    object History : Screen("history", "History", Icons.Default.History)
    object Export : Screen("export", "Export", Icons.Default.Share)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreenAITheme {
                val app = applicationContext as ScreenAIApp
                val viewModel: AppViewModel = viewModel(factory = AppViewModel.Factory(app))

                val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
                val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                val bottomNavItems = listOf(Screen.Home, Screen.Capture, Screen.History, Screen.Settings)
                val showBottomBar = currentScreen !in listOf(Screen.Capture, Screen.Result, Screen.Export)

                LaunchedEffect(isAnalyzing, currentResult) {
                    if (!isAnalyzing && currentResult != null && currentScreen == Screen.Capture) {
                        currentScreen = Screen.Result
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBackground,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = DarkSurface,
                                contentColor = TextPrimary,
                                tonalElevation = 8.dp
                            ) {
                                bottomNavItems.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                                        selected = currentScreen == screen,
                                        onClick = { currentScreen = screen },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = NeonOrange,
                                            selectedTextColor = NeonOrange,
                                            unselectedIconColor = TextTertiary,
                                            unselectedTextColor = TextTertiary,
                                            indicatorColor = NeonOrange.copy(alpha = 0.15f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        color = DarkBackground
                    ) {
                        when (currentScreen) {
                            Screen.Home -> HomeScreen(
                                viewModel = viewModel,
                                onAnalyze = { currentScreen = Screen.Capture },
                                onViewAnalysis = { id ->
                                    viewModel.loadAnalysis(id)
                                    currentScreen = Screen.Result
                                },
                                onViewHistory = { currentScreen = Screen.History },
                                onNavigateToSettings = { currentScreen = Screen.Settings }
                            )

                            Screen.Capture -> CaptureScreen(
                                viewModel = viewModel,
                                onAnalyze = {
                                    viewModel.analyzeImage(app)
                                },
                                onBack = { currentScreen = Screen.Home }
                            )

                            Screen.Result -> ResultScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Home },
                                onExport = { currentScreen = Screen.Export }
                            )

                            Screen.History -> HistoryScreen(
                                viewModel = viewModel,
                                onViewAnalysis = { id ->
                                    viewModel.loadAnalysis(id)
                                    currentScreen = Screen.Result
                                },
                                onBack = { currentScreen = Screen.Home }
                            )

                            Screen.Export -> ExportScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.Result }
                            )

                            Screen.Settings -> SettingsScreen(
                                onBack = { currentScreen = Screen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
