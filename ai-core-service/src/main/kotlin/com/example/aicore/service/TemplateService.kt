package com.example.aicore.service

import com.example.aicore.templates.TemplateModel
import com.example.aicore.templates.TemplateRegistry
import org.springframework.stereotype.Service

@Service
class TemplateService(
    private val templateRegistry: TemplateRegistry
) {
    
    fun getTemplate(name: String): TemplateModel {
        return templateRegistry.getTemplate(name)
    }
    
    fun getAllTemplateNames(): List<String> {
        return templateRegistry.getAllTemplateNames()
    }
    
    fun overrideTemplate(name: String, content: String) {
        templateRegistry.overrideTemplate(name, content)
    }
    
    fun clearOverride(name: String) {
        templateRegistry.clearOverride(name)
    }
    
    fun hasOverride(name: String): Boolean {
        return templateRegistry.hasOverride(name)
    }
}
