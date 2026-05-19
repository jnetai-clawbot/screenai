# ScreenAI - Screenshot Explainer

Intelligent screenshot analysis app that runs entirely offline on your Android device. Take a screenshot or import an image and ScreenAI will analyze it, detecting errors, code, UI elements, math equations, and form fields.

## Features

- **Error Detection**: Identifies crash logs, exceptions, and error messages with suggestions
- **Code Analysis**: Parses code blocks, identifies structure, functions, and language patterns
- **UI Recognition**: Detects buttons, text fields, navigation elements, tabs, and more
- **Math Interpreter**: Recognizes equations, formulas, calculus operations, and expressions
- **Form Analyzer**: Identifies form fields like name, email, password, phone, and address
- **Text Extraction**: Extracts readable text from screenshots (OCR simulation)
- **Searchable History**: Browse and search all past analyses
- **Export & Share**: Copy to clipboard, share as text, or export results
- **100% Offline**: All processing happens on-device, no cloud dependency

## Tech Stack

- Kotlin 1.9.22, AGP 8.2.2, Gradle 8.5
- Jetpack Compose with Material Design 3
- Room Database for local storage
- KSP annotation processing
- Min SDK 29, Target SDK 34, ARM64 only

## Building

```bash
./gradlew assembleDebug
```

## License

Proprietary. Made by jnetaol.com.
