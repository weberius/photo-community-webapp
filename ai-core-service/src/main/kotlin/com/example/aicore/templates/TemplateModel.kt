package com.example.aicore.templates

data class TemplateModel(
    val name: String,
    val version: String,
    val description: String? = null,
    val parameters: List<String> = emptyList(),
    val content: String
)

data class TemplateOverride(
    val name: String,
    val content: String,
    val parameters: Map<String, String> = emptyMap()
)
