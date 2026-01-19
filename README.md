# Dynamic Form – JSON Schema (Jetpack Compose)

## Overview
This Android application demonstrates a dynamic form generated from a JSON Schema with validation, built using Kotlin, Jetpack Compose, and MVVM architecture.

The schema is treated as a server-driven contract (hardcoded locally for this assignment).

## Features
- ✅ Dynamic form rendering from JSON Schema
- ✅ 4 field types: Text, Number, Boolean, Dropdown
- ✅ JSON Schema-based validation rules (required, minLength, minimum)
- ✅ Clear validation error messages
- ✅ Success state with JSON payload logging and display
- ✅ Clean MVVM architecture with immutable state
- ✅ Material 3 design

## Screenshots
<!-- Add screenshots here after running the app -->
*Screenshots will be added*

## Architecture
- **MVVM Pattern**: ViewModel manages state, UI observes changes
- **Immutable UI State**: All state changes create new instances
- **Single Source of Truth**: ViewModel holds the canonical state
- **Compose-friendly**: Proper state management with `mutableStateOf`

## Project Structure
```
app/src/main/
├── assets/
│   ├── form_schema.json       # JSON Schema definition
│   └── ui_schema.json          # UI rendering order
├── java/com/example/tondo/
│   ├── model/
│   │   └── FormField.kt        # Sealed class for form fields
│   ├── viewmodel/
│   │   └── FormViewModel.kt    # Business logic & state management
│   ├── ui/theme/
│   │   └── FormScreen.kt       # Compose UI components
│   └── MainActivity.kt         # Entry point
```

## JSON Schema
**Location**: `app/src/main/assets/form_schema.json`

Defines:
- **Required fields**: `["name", "age", "gender"]`
- **Validation constraints**:
  - `name`: minLength 3
  - `age`: minimum 18
  - `gender`: enum ["Male", "Female", "Other"]
- **Field types**: string, number, boolean

**Example Schema**:
```json
{
  "type": "object",
  "required": ["name", "age", "gender"],
  "properties": {
    "name": {
      "type": "string",
      "minLength": 3
    },
    "age": {
      "type": "number",
      "minimum": 18
    },
    "subscribe": {
      "type": "boolean"
    },
    "gender": {
      "type": "string",
      "enum": ["Male", "Female", "Other"]
    }
  }
}
```

## UI Schema
**Location**: `app/src/main/assets/ui_schema.json`

Defines field rendering order following JSON Forms convention.
```json
{
  "type": "VerticalLayout",
  "elements": [
    { "type": "Control", "scope": "#/properties/name" },
    { "type": "Control", "scope": "#/properties/age" },
    { "type": "Control", "scope": "#/properties/subscribe" },
    { "type": "Control", "scope": "#/properties/gender" }
  ]
}
```

## How to Run

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK API 26+
- Kotlin 1.9.0+

### Steps
1. **Clone the repository**:
```bash
   git clone https://github.com/sara-rubinstein/tondo-test-form.git
   cd tondo-test-form
```

2. **Open in Android Studio**:
   - File → Open → Select the project folder

3. **Sync Gradle**:
   - File → Sync Project with Gradle Files
   - Wait for dependencies to download

4. **Run the app**:
   - Connect an Android device or start an emulator (API 26+)
   - Click the Run button ▶️ or press Shift+F10

5. **Test the form**:
   - Fill in the form fields
   - Try different validation scenarios
   - Check Logcat for `FORM_RESULT` tag to see JSON output

## Validation Rules

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| Name | Text | Yes | Minimum 3 characters |
| Age | Number | Yes | Must be 18 or older |
| Subscribe | Boolean | No | - |
| Gender | Dropdown | Yes | Must select from options |

## Testing Scenarios

### Valid Submission
- Name: "John Doe" (3+ characters)
- Age: 25 (18+)
- Subscribe: true/false
- Gender: "Male" (selected)

**Expected**: ✅ Success message + JSON logged

### Invalid Submissions
1. **Empty required fields**: Shows "This field is required"
2. **Short name** (< 3 chars): Shows "Minimum 3 characters"
3. **Underage** (< 18): Shows "Age must be 18 or older"
4. **No gender selected**: Shows "This field is required"

**Expected**: ❌ Red error messages under invalid fields

## Logs
On successful submission, check Logcat with filter `FORM_RESULT`:
```
D/FORM_RESULT: {"name":"John Doe","age":25,"subscribe":true,"gender":"Male"}
```

## What I Implemented

### Level 1: JSON Schema ✅
- [x] JSON Schema parsing from assets
- [x] Dynamic field generation based on schema types
- [x] Required field validation
- [x] Constraint validation (minLength, minimum)
- [x] Enum/dropdown support
- [x] Clear error messaging

### Architecture & Best Practices ✅
- [x] MVVM architecture
- [x] Jetpack Compose
- [x] Material 3 Design
- [x] Immutable state management
- [x] Sealed classes for type safety
- [x] Proper separation of concerns

## Technologies Used
- **Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Architecture**: MVVM
- **State Management**: Android ViewModel + Compose State
- **JSON Parsing**: org.json (Android built-in)
- **Min SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 34

## What I Would Improve Next

### Short-term Improvements
- 🔄 Implement a proper JSON Schema validation library (e.g., `everit-org/json-schema`)
- 📝 Add more field types: email, date picker, password, multi-select
- 💾 Add form state persistence (remember values on rotation)
- 🧪 Add unit tests for ViewModel validation logic
- 🧪 Add UI tests with Compose Testing

### Medium-term Improvements
- 🌐 Remote schema fetching from REST API
- 🎨 JSON Forms Level 2: Full UI schema support with layout types
- 🎭 Custom theming based on schema metadata
- 📱 Better accessibility (content descriptions, TalkBack support)
- 🌍 Internationalization support

### Long-term Improvements
- 🔐 Add authentication and authorization
- 💾 Backend integration for form submission
- 📊 Form analytics and tracking
- 🔄 Conditional field visibility based on other field values
- 📋 Multi-page/wizard forms support
- 💬 Real-time field validation as user types

## Known Limitations
- Schema is hardcoded in assets (not fetched from server)
- Limited to 4 basic field types
- No conditional field rendering
- No cross-field validation
- No file upload support

## Dependencies
```kotlin
// Compose
androidx.compose.material3
androidx.compose.ui
androidx.activity:activity-compose

// ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1
androidx.lifecycle:lifecycle-runtime-ktx

// Core
androidx.core:core-ktx
```

## Assignment Requirements Checklist
- [x] Render dynamic form from schema
- [x] At least 4 different field types (Text, Number, Boolean, Dropdown)
- [x] Support required fields
- [x] Basic validation (min, max, minLength, maxLength)
- [x] Submit button with validation feedback
- [x] Show success message on valid submission
- [x] Display/log resulting JSON payload
- [x] Show clear validation errors
- [x] Kotlin
- [x] Jetpack Compose
- [x] MVVM architecture
- [x] Clean state management
- [x] Schema in local JSON file

## Developer
**Sara Rubinstein**  

Developed as a take-home assignment for Tondo IoT

## License
This project is for assignment purposes only.

---

## Contact
For questions or feedback:
- GitHub: [@sara-rubinstein](https://github.com/sara-rubinstein)
- Repository: [tondo-dynamic-form](https://github.com/sara-rubinstein/tondo-test-form)
