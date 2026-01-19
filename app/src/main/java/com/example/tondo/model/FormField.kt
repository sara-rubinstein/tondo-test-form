package com.tondo.dynamicform.model

sealed class FormField(open val key: String) {

    data class Text(
        override val key: String,
        val value: String = "",
        val error: String? = null
    ) : FormField(key)

    data class Number(
        override val key: String,
        val value: String = "",
        val error: String? = null
    ) : FormField(key)

    data class BooleanField(
        override val key: String,
        val value: Boolean = false
    ) : FormField(key)

    data class Dropdown(
        override val key: String,
        val options: List<String>,
        val selected: String = "",
        val error: String? = null
    ) : FormField(key)
}