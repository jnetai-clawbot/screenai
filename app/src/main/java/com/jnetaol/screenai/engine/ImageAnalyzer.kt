package com.jnetaol.screenai.engine

import com.jnetaol.screenai.data.model.AnalysisResult
import com.jnetaol.screenai.data.model.AnalysisType
import com.jnetaol.screenai.logger.DebugLogger

object ImageAnalyzer {
    private const val TAG = "ImageAnalyzer"

    fun analyze(imagePath: String): AnalysisResult {
        DebugLogger.i(TAG, "Analyzing image: $imagePath")

        val extractedText = TextExtractor.extractText(imagePath)
        val categories = TextExtractor.detectTextCategories(extractedText)
        val type = determinePrimaryType(categories)

        val explanation = buildExplanation(type, extractedText, imagePath)
        val detectedElements = detectElements(type, extractedText)
        val tags = generateTags(type, extractedText, imagePath)

        DebugLogger.i(TAG, "Analysis complete. Type: ${type.displayName}, Elements: ${detectedElements.size}, Tags: ${tags.size}")

        return AnalysisResult(
            type = type,
            extractedText = extractedText,
            explanation = explanation,
            detectedElements = detectedElements,
            tags = tags,
            rawText = extractedText
        )
    }

    private fun determinePrimaryType(categories: List<String>): AnalysisType {
        return when {
            categories.contains("Error") -> AnalysisType.ERROR
            categories.contains("Code") -> AnalysisType.CODE
            categories.contains("Math") -> AnalysisType.MATH
            categories.contains("Form") -> AnalysisType.FORM
            categories.contains("UI") -> AnalysisType.UI_ELEMENT
            else -> AnalysisType.GENERAL
        }
    }

    private fun buildExplanation(type: AnalysisType, text: String, imagePath: String): String {
        val file = java.io.File(imagePath)
        val sb = StringBuilder()

        sb.appendLine("=== ScreenAI Analysis Report ===")
        sb.appendLine("Category: ${type.displayName}")
        sb.appendLine("Timestamp: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}")
        sb.appendLine("Image: ${file.name}")
        sb.appendLine()

        when (type) {
            AnalysisType.ERROR -> {
                sb.appendLine("🔍 Error Analysis")
                sb.appendLine("─".repeat(40))
                val errorLines = text.lines().filter { line ->
                    line.contains("error", ignoreCase = true) ||
                    line.contains("exception", ignoreCase = true) ||
                    line.contains("failed", ignoreCase = true) ||
                    line.contains("fatal", ignoreCase = true) ||
                    line.contains("crash", ignoreCase = true) ||
                    line.contains("at ", ignoreCase = true) ||
                    line.contains("Caused by", ignoreCase = true)
                }
                if (errorLines.isNotEmpty()) {
                    sb.appendLine("Detected ${errorLines.size} error-related entries:")
                    errorLines.forEach { sb.appendLine("  • $it") }
                }
                sb.appendLine()
                sb.appendLine("Possible Causes:")
                sb.appendLine("  1. Network connectivity issues")
                sb.appendLine("  2. Null reference or uninitialized variables")
                sb.appendLine("  3. Database connection failures")
                sb.appendLine("  4. Invalid input or data format")
                sb.appendLine()
                sb.appendLine("Suggested Actions:")
                sb.appendLine("  • Check network connection and retry")
                sb.appendLine("  • Verify input data is valid")
                sb.appendLine("  • Review stack trace for root cause file/line")
                sb.appendLine("  • Add null checks and error handling")
            }
            AnalysisType.CODE -> {
                sb.appendLine("💻 Code Analysis")
                sb.appendLine("─".repeat(40))
                val codeLines = text.lines().filter { it.isNotBlank() }
                sb.appendLine("Detected ${codeLines.size} lines of code-like content:")
                val functions = codeLines.filter { it.contains(Regex("\\b(func|function|fn|def)\\s+\\w+")) }.size
                val loops = codeLines.filter { it.contains(Regex("\\b(for|while)\\s*[({]")) }.size
                val conditionals = codeLines.filter { it.contains(Regex("\\b(if|else|switch|case)\\b")) }.size
                sb.appendLine("  Functions: $functions")
                sb.appendLine("  Loops: $loops")
                sb.appendLine("  Conditionals: $conditionals")
                sb.appendLine()
                sb.appendLine("Code Structure:")
                codeLines.forEachIndexed { i, line ->
                    if (i < 15) sb.appendLine("  $line")
                }
                if (codeLines.size > 15) sb.appendLine("  ... (${codeLines.size - 15} more lines)")
            }
            AnalysisType.UI_ELEMENT -> {
                sb.appendLine("🎨 UI Element Analysis")
                sb.appendLine("─".repeat(40))
                sb.appendLine("Detected UI components:")
                val buttons = text.lines().filter { it.contains("button", ignoreCase = true) }.size
                val inputs = text.lines().filter { it.contains("text", ignoreCase = true) || it.contains("input", ignoreCase = true) }.size
                val nav = text.lines().filter { it.contains("navigation", ignoreCase = true) || it.contains("menu", ignoreCase = true) || it.contains("tab", ignoreCase = true) }.size
                sb.appendLine("  Buttons: $buttons")
                sb.appendLine("  Text Fields: $inputs")
                sb.appendLine("  Navigation Elements: $nav")
                sb.appendLine()
                sb.appendLine("Element Details:")
                text.lines().take(10).forEach { sb.appendLine("  • $it") }
                sb.appendLine()
                sb.appendLine("Accessibility Notes:")
                sb.appendLine("  • Ensure all interactive elements have labels")
                sb.appendLine("  • Verify sufficient color contrast ratios")
                sb.appendLine("  • Check touch target sizes (min 48dp)")
            }
            AnalysisType.MATH -> {
                sb.appendLine("📐 Math Analysis")
                sb.appendLine("─".repeat(40))
                val mathLines = text.lines().filter { line ->
                    "[+\\-*/=<>!]".toRegex().containsMatchIn(line) ||
                    "sin|cos|tan|log|sqrt|sum|integral".toRegex(RegexOption.IGNORE_CASE).containsMatchIn(line)
                }
                sb.appendLine("Detected ${mathLines.size} mathematical expressions:")
                mathLines.forEach { sb.appendLine("  • $it") }
                sb.appendLine()
                sb.appendLine("Expression Breakdown:")
                mathLines.forEach { expr ->
                    val simplified = expr.trim()
                    sb.appendLine("  Original: $simplified")
                    if (simplified.contains("=")) {
                        sb.appendLine("  → This is an equation - solutions may exist")
                    }
                    if (simplified.contains("d/dx") || simplified.contains("∫")) {
                        sb.appendLine("  → Calculus operation detected")
                    }
                    sb.appendLine()
                }
            }
            AnalysisType.FORM -> {
                sb.appendLine("📝 Form Analysis")
                sb.appendLine("─".repeat(40))
                sb.appendLine("Detected form fields:")
                val fields = listOf("name", "email", "password", "phone", "address", "date", "username", "zip")
                fields.forEach { field ->
                    val count = text.lines().count { it.contains(field, ignoreCase = true) }
                    if (count > 0) sb.appendLine("  • ${field.replaceFirstChar { it.uppercase() }} field: $count instance(s)")
                }
                sb.appendLine()
                sb.appendLine("Form Assessment:")
                sb.appendLine("  • Required fields identified: ${fields.filter { text.contains(it, ignoreCase = true) }.size}")
                sb.appendLine("  • Submit/Cancel buttons: ${if (text.contains("submit", ignoreCase = true) || text.contains("cancel", ignoreCase = true)) "Present" else "Not detected"}")
                sb.appendLine("  • Validation indicators: Check for visual cues")
                sb.appendLine()
                sb.appendLine("Recommendations:")
                sb.appendLine("  • Add input validation messages")
                sb.appendLine("  • Ensure accessible labels for screen readers")
                sb.appendLine("  • Consider adding progress indicators")
            }
            AnalysisType.GENERAL -> {
                sb.appendLine("📋 General Content Analysis")
                sb.appendLine("─".repeat(40))
                if (text != "No readable text detected in this image.") {
                    sb.appendLine("The image contains general content.")
                    sb.appendLine("Key observations:")
                    text.lines().take(8).forEach { sb.appendLine("  • $it") }
                } else {
                    sb.appendLine("No text was detected in this image.")
                    sb.appendLine("For better results:")
                    sb.appendLine("  • Ensure the image contains readable text")
                    sb.appendLine("  • Capture clear, well-lit screenshots")
                    sb.appendLine("  • Avoid screenshots that are primarily images/graphics")
                }
            }
            AnalysisType.OCR -> {
                sb.appendLine("📄 Text Extraction (OCR)")
                sb.appendLine("─".repeat(40))
                sb.appendLine("Extracted ${text.lines().size} lines of text.")
                sb.appendLine()
                text.lines().take(20).forEach { sb.appendLine("  $it") }
                if (text.lines().size > 20) sb.appendLine("  ... (${text.lines().size - 20} more lines)")
            }
        }

        return sb.toString()
    }

    private fun detectElements(type: AnalysisType, text: String): List<String> {
        val elements = mutableListOf<String>()

        when (type) {
            AnalysisType.ERROR -> {
                text.lines().forEach { line ->
                    when {
                        line.contains("Exception", ignoreCase = true) -> elements.add("Exception: ${extractAfter(line, ":")}")
                        line.contains("Error", ignoreCase = true) -> elements.add("Error: ${extractAfter(line, ":")}")
                        line.contains("at ", ignoreCase = true) -> elements.add("Stack Frame: ${extractAfter(line, "at ")}")
                        line.contains("Caused by", ignoreCase = true) -> elements.add("Root Cause: ${extractAfter(line, "Caused by:")}")
                    }
                }
            }
            AnalysisType.CODE -> {
                if (text.contains("function", ignoreCase = true) || text.contains("func", ignoreCase = true)) elements.add("Function Definitions")
                if (text.contains("class", ignoreCase = true)) elements.add("Class Definitions")
                if (text.contains("import", ignoreCase = true) || text.contains("require", ignoreCase = true)) elements.add("Imports/Dependencies")
                if (text.contains("return", ignoreCase = true)) elements.add("Return Statements")
                if (text.contains("for ", ignoreCase = true) || text.contains("while ", ignoreCase = true)) elements.add("Loop Structures")
                if (text.contains("if ", ignoreCase = true) || text.contains("else ", ignoreCase = true)) elements.add("Conditional Logic")
            }
            AnalysisType.UI_ELEMENT -> {
                if (text.contains("button", ignoreCase = true)) elements.add("Buttons")
                if (text.contains("text", ignoreCase = true) || text.contains("input", ignoreCase = true)) elements.add("Text Fields")
                if (text.contains("navigation", ignoreCase = true) || text.contains("menu", ignoreCase = true)) elements.add("Navigation")
                if (text.contains("tab", ignoreCase = true)) elements.add("Tabs")
                if (text.contains("search", ignoreCase = true)) elements.add("Search Bar")
                if (text.contains("progress", ignoreCase = true) || text.contains("slider", ignoreCase = true)) elements.add("Progress Indicators")
            }
            AnalysisType.MATH -> {
                if (text.contains("=")) elements.add("Equations")
                if (text.contains("+") || text.contains("-")) elements.add("Arithmetic Operations")
                if (text.contains("sin") || text.contains("cos") || text.contains("tan")) elements.add("Trigonometric Functions")
                if (text.contains("∫") || text.contains("d/dx")) elements.add("Calculus")
                if (text.contains("√") || text.contains("sqrt")) elements.add("Roots")
            }
            AnalysisType.FORM -> {
                if (text.contains("name", ignoreCase = true)) elements.add("Name Fields")
                if (text.contains("email", ignoreCase = true)) elements.add("Email Fields")
                if (text.contains("password", ignoreCase = true)) elements.add("Password Fields")
                if (text.contains("phone", ignoreCase = true)) elements.add("Phone Fields")
                if (text.contains("submit", ignoreCase = true)) elements.add("Submit Button")
                if (text.contains("checkbox", ignoreCase = true) || text.contains("[ ]")) elements.add("Checkboxes")
            }
            AnalysisType.GENERAL, AnalysisType.OCR -> {
                elements.add("Text Content")
                if (text.length > 500) elements.add("Long-form Content")
                if (text.lines().size > 20) elements.add("Multi-line Text")
            }
        }

        if (elements.isEmpty()) elements.add("No specific elements detected")
        return elements
    }

    private fun generateTags(type: AnalysisType, text: String, imagePath: String): List<String> {
        val tags = mutableListOf<String>()
        tags.add(type.displayName.lowercase().replace(" ", "-"))

        when (type) {
            AnalysisType.ERROR -> {
                if (text.contains("runtime", ignoreCase = true)) tags.add("runtime-error")
                if (text.contains("null", ignoreCase = true)) tags.add("null-pointer")
                if (text.contains("connection", ignoreCase = true)) tags.add("connection-error")
                if (text.contains("timeout", ignoreCase = true)) tags.add("timeout")
                tags.add("debugging")
            }
            AnalysisType.CODE -> {
                if (text.contains("function", ignoreCase = true) || text.contains("def ", ignoreCase = true)) tags.add("functions")
                if (text.contains("class", ignoreCase = true)) tags.add("classes")
                if (text.contains("javascript", ignoreCase = true)) tags.add("javascript")
                else if (text.contains("python", ignoreCase = true)) tags.add("python")
                else if (text.contains("java", ignoreCase = true)) tags.add("java")
                tags.add("source-code")
            }
            AnalysisType.UI_ELEMENT -> {
                tags.add("design")
                tags.add("layout")
                if (text.contains("button", ignoreCase = true)) tags.add("interactive")
                tags.add("interface")
            }
            AnalysisType.MATH -> {
                tags.add("formula")
                if (text.contains("=")) tags.add("equation")
                if (text.contains("∫") || text.contains("d/dx")) tags.add("calculus")
                tags.add("computation")
            }
            AnalysisType.FORM -> {
                tags.add("input-fields")
                if (text.contains("login", ignoreCase = true)) tags.add("login")
                if (text.contains("register", ignoreCase = true)) tags.add("registration")
                if (text.contains("password", ignoreCase = true)) tags.add("authentication")
                tags.add("data-entry")
            }
            AnalysisType.GENERAL -> {
                tags.add("general")
            }
            AnalysisType.OCR -> {
                tags.add("ocr")
                tags.add("text-extraction")
            }
        }

        val filename = java.io.File(imagePath).name
        tags.add("screenshot-${java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())}")

        return tags.distinct().take(8)
    }

    private fun extractAfter(line: String, delimiter: String): String {
        val idx = line.indexOf(delimiter)
        return if (idx >= 0) line.substring(idx + delimiter.length).trim().take(50) else line.take(50)
    }
}
