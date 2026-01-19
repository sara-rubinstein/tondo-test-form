package com.example.tondo.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tondo.dynamicform.model.FormField
import com.tondo.dynamicform.viewmodel.FormViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(viewModel: FormViewModel = viewModel()) {
    val fields by viewModel.fields
    var showSuccess by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (fields.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Dynamic Form",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(fields, key = { it.key }) { field ->
                    FormFieldItem(field = field, viewModel = viewModel)
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val valid = viewModel.validate()
                        if (valid) {
                            val json = viewModel.getResultJson()
                            Log.d("FORM_RESULT", json)
                            showSuccess = true
                        } else {
                            showSuccess = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }

                if (showSuccess) {
                    val jsonResult = remember { viewModel.getResultJson() }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "✅ Form submitted successfully!",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = jsonResult,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFieldItem(field: FormField, viewModel: FormViewModel) {
    when (field) {
        is FormField.Text -> {
            Column {
                OutlinedTextField(
                    value = field.value,
                    onValueChange = { viewModel.onValueChange(field.key, it) },
                    label = { Text(field.key.replaceFirstChar { it.uppercase() }) },
                    isError = field.error != null,
                    modifier = Modifier.fillMaxWidth()
                )
                field.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        is FormField.Number -> {
            Column {
                OutlinedTextField(
                    value = field.value,
                    onValueChange = {
                        if (it.isEmpty() || it.all { ch -> ch.isDigit() }) {
                            viewModel.onValueChange(field.key, it)
                        }
                    },
                    label = { Text(field.key.replaceFirstChar { it.uppercase() }) },
                    isError = field.error != null,
                    modifier = Modifier.fillMaxWidth()
                )
                field.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        is FormField.BooleanField -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = field.value,
                    onCheckedChange = { viewModel.onValueChange(field.key, it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(field.key.replaceFirstChar { it.uppercase() })
            }
        }

        is FormField.Dropdown -> {
            var expanded by remember { mutableStateOf(false) }

            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = field.selected,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(field.key.replaceFirstChar { it.uppercase() }) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        isError = field.error != null,
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        field.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.onValueChange(field.key, option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                field.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}