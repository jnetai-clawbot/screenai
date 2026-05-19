package com.jnetaol.screenai.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_analyses")
data class ScreenAnalysis(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val analysisType: String,
    val extractedText: String,
    val analysis: String,
    val tags: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AnalysisType(val displayName: String, val icon: String) {
    ERROR("Error Detection", "bug_report"),
    CODE("Code Analysis", "code"),
    UI_ELEMENT("UI Elements", "widgets"),
    MATH("Math Equations", "calculate"),
    FORM("Form Fields", "edit_note"),
    GENERAL("General", "image_search"),
    OCR("Text Extraction", "text_fields");
}

data class AnalysisResult(
    val type: AnalysisType,
    val extractedText: String,
    val explanation: String,
    val detectedElements: List<String>,
    val tags: List<String>,
    val rawText: String
)
