package com.example.aicore.controller

import com.example.aicore.controller.dto.TemplateDto
import com.example.aicore.controller.dto.TemplateOverrideRequest
import com.example.aicore.service.TemplateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/templates")
@Tag(name = "Template Management", description = "Manage prompt templates")
class TemplateController(
    private val templateService: TemplateService
) {
    
    @GetMapping
    @Operation(summary = "List all templates", description = "Get names of all available templates")
    fun listTemplates(): ResponseEntity<List<String>> {
        val names = templateService.getAllTemplateNames()
        return ResponseEntity.ok(names)
    }
    
    @GetMapping("/{name}")
    @Operation(summary = "Get template", description = "Get template by name")
    fun getTemplate(@PathVariable name: String): ResponseEntity<TemplateDto> {
        val template = templateService.getTemplate(name)
        val hasOverride = templateService.hasOverride(name)
        
        val dto = TemplateDto(
            name = template.name,
            version = template.version,
            description = template.description,
            parameters = template.parameters,
            content = template.content,
            hasOverride = hasOverride
        )
        
        return ResponseEntity.ok(dto)
    }
    
    @PutMapping("/{name}")
    @Operation(
        summary = "Override template",
        description = "Override template content at runtime (does not persist to file)"
    )
    fun overrideTemplate(
        @PathVariable name: String,
        @RequestBody request: TemplateOverrideRequest
    ): ResponseEntity<Map<String, String>> {
        templateService.overrideTemplate(name, request.content)
        return ResponseEntity.ok(mapOf("message" to "Template override set for: $name"))
    }
    
    @DeleteMapping("/{name}/override")
    @Operation(summary = "Clear template override", description = "Remove runtime template override")
    fun clearOverride(@PathVariable name: String): ResponseEntity<Map<String, String>> {
        templateService.clearOverride(name)
        return ResponseEntity.ok(mapOf("message" to "Template override cleared for: $name"))
    }
}
