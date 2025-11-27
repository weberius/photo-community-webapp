package com.example.aicore.exception

/**
 * Base exception for AI provider errors
 */
open class AiProviderException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * Exception thrown when AI provider rate limit is exceeded
 */
class RateLimitException(
    message: String
) : AiProviderException(message)

/**
 * Exception thrown when AI model request times out
 */
class ModelTimeoutException(
    message: String,
    cause: Throwable? = null
) : AiProviderException(message, cause)

/**
 * Exception thrown when template is not found
 */
class TemplateNotFoundException(
    message: String
) : RuntimeException(message)

/**
 * Exception thrown when pipeline is invalid or not found
 */
class InvalidPipelineException(
    message: String
) : RuntimeException(message)
