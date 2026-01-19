package com.tondo.dynamicform.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.tondo.dynamicform.model.FormField
import org.json.JSONObject

class FormViewModel(application: Application) : AndroidViewModel(application) {

    var fields = mutableStateOf<List<FormField>>(emptyList())
        private set

    private var requiredFields = setOf<String>()

    init {
        loadSchemaFromAssets()
    }

    private fun loadUiOrder(): List<String> {
        val context = getApplication<Application>()
        val json = context.assets.open("ui_schema.json")
            .bufferedReader()
            .use { it.readText() }

        val uiSchema = JSONObject(json)
        val elements = uiSchema.getJSONArray("elements")

        return List(elements.length()) {
            elements.getJSONObject(it)
                .getString("scope")
                .substringAfterLast("/")
        }
    }

    private fun loadSchemaFromAssets() {
        val context = getApplication<Application>()

        val json = context.assets
            .open("form_schema.json")
            .bufferedReader()
            .use { it.readText() }

        val schema = JSONObject(json)

        // Load required fields
        if (schema.has("required")) {
            val requiredArray = schema.getJSONArray("required")
            requiredFields = (0 until requiredArray.length())
                .map { requiredArray.getString(it) }
                .toSet()
        }

        val properties = schema.getJSONObject("properties")
        val result = mutableListOf<FormField>()

        properties.keys().forEach { key ->
            val field = properties.getJSONObject(key)

            when (field.getString("type")) {
                "string" -> {
                    if (field.has("enum")) {
                        val enumArray = field.getJSONArray("enum")
                        val options = List(enumArray.length()) {
                            enumArray.getString(it)
                        }
                        result.add(FormField.Dropdown(key, options))
                    } else {
                        result.add(FormField.Text(key))
                    }
                }
                "number" -> result.add(FormField.Number(key))
                "boolean" -> result.add(FormField.BooleanField(key))
            }
        }

        val uiOrder = loadUiOrder()

        fields.value = result.sortedBy {
            val index = uiOrder.indexOf(it.key)
            if (index == -1) Int.MAX_VALUE else index
        }
    }

    fun onValueChange(key: String, value: Any) {
        fields.value = fields.value.map {
            when (it) {
                is FormField.Text ->
                    if (it.key == key) it.copy(value = value as String, error = null) else it

                is FormField.Number ->
                    if (it.key == key) it.copy(value = value as String, error = null) else it

                is FormField.BooleanField ->
                    if (it.key == key) it.copy(value = value as Boolean) else it

                is FormField.Dropdown ->
                    if (it.key == key) it.copy(selected = value as String, error = null) else it
            }
        }
    }

    fun validate(): Boolean {
        var valid = true

        fields.value = fields.value.map {
            when (it) {
                is FormField.Text -> {
                    val error = when {
                        requiredFields.contains(it.key) && it.value.isEmpty() -> "This field is required"
                        it.value.isNotEmpty() && it.value.length < 3 -> "Minimum 3 characters"
                        else -> null
                    }
                    if (error != null) valid = false
                    it.copy(error = error)
                }

                is FormField.Number -> {
                    val age = it.value.toIntOrNull()
                    val error = when {
                        requiredFields.contains(it.key) && it.value.isEmpty() -> "This field is required"
                        age == null && it.value.isNotEmpty() -> "Must be a valid number"
                        age != null && age < 18 -> "Age must be 18 or older"
                        else -> null
                    }
                    if (error != null) valid = false
                    it.copy(error = error)
                }

                is FormField.Dropdown -> {
                    val error = when {
                        requiredFields.contains(it.key) && it.selected.isEmpty() -> "This field is required"
                        else -> null
                    }
                    if (error != null) valid = false
                    it.copy(error = error)
                }

                else -> it
            }
        }

        return valid
    }

    fun getResultJson(): String {
        val map = mutableMapOf<String, Any>()
        fields.value.forEach {
            when (it) {
                is FormField.Text -> map[it.key] = it.value
                is FormField.Number -> map[it.key] = it.value.toIntOrNull() ?: 0
                is FormField.BooleanField -> map[it.key] = it.value
                is FormField.Dropdown -> map[it.key] = it.selected
            }
        }
        return JSONObject(map as Map<*, *>).toString()
    }

    fun resetForm() {
        loadSchemaFromAssets()
    }
}