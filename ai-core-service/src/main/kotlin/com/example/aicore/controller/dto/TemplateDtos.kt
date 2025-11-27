package com.example.aicore.controller.dto

data class TemplateDto(
    val name: String,
    val version: String,
    val description: String? = null,
    val parameters: List<String> = emptyList(),
    val content: String,
    val hasOverride: Boolean = false
)

data class TemplateOverrideRequest(
    val content: String
)
