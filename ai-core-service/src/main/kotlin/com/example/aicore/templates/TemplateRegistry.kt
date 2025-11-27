package com.example.aicore.templates

import com.example.aicore.exception.TemplateNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class TemplateRegistry {
    
    private val logger = LoggerFactory.getLogger(TemplateRegistry::class.java)
    private val yamlMapper = ObjectMapper(YAMLFactory()).findAndRegisterModules()
    private val templates = ConcurrentHashMap<String, TemplateModel>()
    private val overrides = ConcurrentHashMap<String, String>()
    
    @PostConstruct
    fun loadTemplates() {
        logger.info("Loading prompt templates from classpath...")
        
        val resolver = PathMatchingResourcePatternResolver()
        val resources: Array<Resource> = resolver.getResources("classpath:prompts/*.yaml")
        
        resources.forEach { resource ->
            try {
                val template: TemplateModel = yamlMapper.readValue(resource.inputStream)
                templates[template.name] = template
                logger.info("Loaded template: ${template.name} (version ${template.version})")
            } catch (e: Exception) {
                logger.error("Failed to load template from ${resource.filename}", e)
            }
        }
        
        logger.info("Loaded ${templates.size} templates")
    }
    
    fun getTemplate(name: String): TemplateModel {
        return templates[name] 
            ?: throw TemplateNotFoundException("Template not found: $name")
    }
    
    fun getAllTemplateNames(): List<String> {
        return templates.keys.toList()
    }
    
    fun renderTemplate(name: String, parameters: Map<String, Any>): String {
        val template = getTemplate(name)
        
        // Use override if exists
        var content = overrides[name] ?: template.content
        
        // Simple parameter substitution using {{paramName}} syntax
        parameters.forEach { (key, value) ->
            content = content.replace("{{$key}}", value.toString())
        }
        
        return content
    }
    
    fun overrideTemplate(name: String, newContent: String) {
        // Verify template exists
        getTemplate(name)
        overrides[name] = newContent
        logger.info("Template override set for: $name")
    }
    
    fun clearOverride(name: String) {
        overrides.remove(name)
        logger.info("Template override cleared for: $name")
    }
    
    fun hasOverride(name: String): Boolean {
        return overrides.containsKey(name)
    }
}
