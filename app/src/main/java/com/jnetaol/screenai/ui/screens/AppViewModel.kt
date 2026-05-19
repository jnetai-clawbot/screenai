package com.jnetaol.screenai.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jnetaol.screenai.data.model.AnalysisResult
import com.jnetaol.screenai.data.model.AnalysisType
import com.jnetaol.screenai.data.model.ScreenAnalysis
import com.jnetaol.screenai.engine.ImageAnalyzer
import com.jnetaol.screenai.logger.DebugLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AppViewModel(private val app: com.jnetaol.screenai.ScreenAIApp) : ViewModel() {
    private val dao = app.database.analysisDao()
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _currentAnalysisId = MutableStateFlow<Long?>(null)
    val currentAnalysisId: StateFlow<Long?> = _currentAnalysisId.asStateFlow()

    private val _currentResult = MutableStateFlow<AnalysisResult?>(null)
    val currentResult: StateFlow<AnalysisResult?> = _currentResult.asStateFlow()

    val allAnalyses: StateFlow<List<ScreenAnalysis>> = dao.getAllAnalyses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<ScreenAnalysis>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) dao.getAllAnalyses()
            else dao.searchAnalyses(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analysisCount: StateFlow<Int> = dao.getAnalysisCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val typeCounts: StateFlow<Map<String, Int>> = dao.getAllAnalyses().map { list ->
        list.groupBy { it.analysisType }.mapValues { it.value.size }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun selectImage(uri: Uri) {
        DebugLogger.d("ViewModel", "Image selected: $uri")
        _selectedImageUri.value = uri
        _currentResult.value = null
        _currentAnalysisId.value = null
    }

    fun analyzeImage(context: Context) {
        val uri = _selectedImageUri.value ?: run {
            DebugLogger.w("ViewModel", "No image selected for analysis", "002")
            return
        }

        _isAnalyzing.value = true
        DebugLogger.i("ViewModel", "Starting analysis")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imagePath = copyToLocal(uri, context)
                if (imagePath == null) {
                    DebugLogger.e("ViewModel", "Failed to copy image", "003")
                    _isAnalyzing.value = false
                    return@launch
                }

                val result = ImageAnalyzer.analyze(imagePath)

                val entity = ScreenAnalysis(
                    imagePath = imagePath,
                    analysisType = result.type.displayName,
                    extractedText = result.extractedText,
                    analysis = result.explanation,
                    tags = result.tags.joinToString(",")
                )

                val id = dao.insertAnalysis(entity)
                _currentAnalysisId.value = id
                _currentResult.value = result
                DebugLogger.i("ViewModel", "Analysis saved with id=$id, type=${result.type.displayName}")
            } catch (e: Exception) {
                DebugLogger.e("ViewModel", "Analysis failed", "004", e)
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun loadAnalysis(id: Long) {
        DebugLogger.d("ViewModel", "Loading analysis: $id")
        viewModelScope.launch {
            val entity = dao.getAnalysisById(id)
            if (entity != null) {
                _currentAnalysisId.value = entity.id
                _currentResult.value = AnalysisResult(
                    type = try { AnalysisType.valueOf(entity.analysisType.uppercase().replace(" ", "_")) } catch (_: Exception) { AnalysisType.GENERAL },
                    extractedText = entity.extractedText,
                    explanation = entity.analysis,
                    detectedElements = listOf(),
                    tags = entity.tags.split(",").filter { it.isNotBlank() },
                    rawText = entity.extractedText
                )
            }
        }
    }

    fun deleteAnalysis(id: Long) {
        viewModelScope.launch {
            val entity = dao.getAnalysisById(id)
            if (entity != null) {
                dao.deleteAnalysis(entity)
                if (_currentAnalysisId.value == id) {
                    _currentAnalysisId.value = null
                    _currentResult.value = null
                }
            }
        }
    }

    fun deleteAllAnalyses() {
        viewModelScope.launch {
            dao.deleteAll()
            _currentAnalysisId.value = null
            _currentResult.value = null
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun resetCurrent() {
        _currentResult.value = null
        _currentAnalysisId.value = null
        _selectedImageUri.value = null
    }

    private fun copyToLocal(uri: Uri, context: Context): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val dir = File(context.filesDir, "screenshots")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "screen_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            DebugLogger.e("ViewModel", "Failed to copy image file", "005", e)
            null
        }
    }

    class Factory(private val app: com.jnetaol.screenai.ScreenAIApp) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(app) as T
        }
    }
}
