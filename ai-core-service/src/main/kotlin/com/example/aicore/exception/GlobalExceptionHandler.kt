package com.example.aicore.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    @ExceptionHandler(TemplateNotFoundException::class)
    fun handleTemplateNotFound(ex: TemplateNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Template not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = "Template Not Found",
                message = ex.message ?: "Template not found"
            ))
    }
    
    @ExceptionHandler(InvalidPipelineException::class)
    fun handleInvalidPipeline(ex: InvalidPipelineException): ResponseEntity<ErrorResponse> {
        logger.warn("Invalid pipeline: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Invalid Pipeline",
                message = ex.message ?: "Invalid pipeline"
            ))
    }
    
    @ExceptionHandler(RateLimitException::class)
    fun handleRateLimit(ex: RateLimitException): ResponseEntity<ErrorResponse> {
        logger.warn("Rate limit exceeded: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ErrorResponse(
                status = HttpStatus.TOO_MANY_REQUESTS.value(),
                error = "Rate Limit Exceeded",
                message = ex.message ?: "Rate limit exceeded"
            ))
    }
    
    @ExceptionHandler(ModelTimeoutException::class)
    fun handleModelTimeout(ex: ModelTimeoutException): ResponseEntity<ErrorResponse> {
        logger.error("Model timeout: ${ex.message}", ex)
        return ResponseEntity
            .status(HttpStatus.GATEWAY_TIMEOUT)
            .body(ErrorResponse(
                status = HttpStatus.GATEWAY_TIMEOUT.value(),
                error = "Model Timeout",
                message = ex.message ?: "AI model request timed out"
            ))
    }
    
    @ExceptionHandler(AiProviderException::class)
    fun handleAiProvider(ex: AiProviderException): ResponseEntity<ErrorResponse> {
        logger.error("AI provider error: ${ex.message}", ex)
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ErrorResponse(
                status = HttpStatus.BAD_GATEWAY.value(),
                error = "AI Provider Error",
                message = ex.message ?: "Error communicating with AI provider"
            ))
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Internal Server Error",
                message = "An unexpected error occurred"
            ))
    }
}
