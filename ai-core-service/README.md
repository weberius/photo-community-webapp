# AI Core Service

A production-ready Spring Boot microservice in Kotlin that serves as a central AI orchestration service for microservice architectures. It provides a unified interface to AI providers (Google Gemini and OpenAI GPT) for other services.

## 🎯 Features

- **AI Provider Abstraction**: Unified interface supporting both Google Gemini and OpenAI GPT
- **Prompt Template Management**: YAML-based templates with runtime override capability
- **Pipeline Architecture**: Extensible pipeline system for different AI tasks
- **Cost Tracking**: Token usage and cost monitoring per provider and task type
- **Comprehensive Logging**: Request/response logging with privacy protection
- **Multi-Environment**: H2 for development, PostgreSQL for production
- **OpenAPI Documentation**: Interactive API documentation via Swagger UI

## 📋 Table of Contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Extending the Service](#extending-the-service)

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         REST Controllers                │
│  (AiController, TemplateController,     │
│   CostController)                       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Service Layer                   │
│  (AiOrchestrationService,               │
│   TemplateService, CostTrackingService) │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Pipeline Layer                  │
│  (PhotoAnalysis, PortfolioReview,       │
│   Scouting)                             │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         AI Adapter Layer                │
│  (GeminiClient, OpenAiClient)           │
└─────────────────────────────────────────┘
```

### Components

- **Controllers**: REST API endpoints
- **Services**: Business logic and orchestration
- **Pipelines**: Task-specific AI workflows
- **Adapters**: AI provider integrations (Gemini, OpenAI)
- **Templates**: Prompt template management
- **Persistence**: Database entities and repositories

## 📦 Prerequisites

- **Java 17** or higher
- **Gradle 8.x** (or use included wrapper)
- **PostgreSQL 14+** (for production)
- **API Keys**:
  - Google Gemini API key (optional)
  - OpenAI API key (optional)

## 🚀 Setup

### 1. Clone and Build

```bash
cd ai-core-service
./gradlew build
```

### 2. Environment Variables

Create a `.env` file or set environment variables:

```bash
# AI Provider API Keys
export GEMINI_API_KEY=your_gemini_api_key_here
export OPENAI_API_KEY=your_openai_api_key_here

# Database (Production only)
export DATABASE_URL=jdbc:postgresql://localhost:5432/aicore
export DATABASE_USERNAME=aicore
export DATABASE_PASSWORD=your_password
```

> **Security Note**: API keys are loaded from environment variables and never logged. Keep your `.env` file out of version control.

## ⚙️ Configuration

### Application Profiles

The service supports two profiles:

#### Development Profile (`dev`)
- Uses H2 in-memory database
- H2 console available at `/h2-console`
- Verbose logging enabled
- Auto-activated when no profile specified

#### Production Profile (`prod`)
- Uses PostgreSQL database
- JSON structured logging
- Optimized connection pooling
- Reduced log verbosity

### AI Provider Configuration

Edit `src/main/resources/application.yml`:

```yaml
ai:
  default-provider: gemini  # or "openai"
  gemini:
    api-key: ${GEMINI_API_KEY:}
    api-url: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
    timeout-seconds: 60
  openai:
    api-key: ${OPENAI_API_KEY:}
    api-url: https://api.openai.com/v1/chat/completions
    model: gpt-4
    timeout-seconds: 60
```

## 🏃 Running the Application

### Development Mode (H2 Database)

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Or using Gradle:

```bash
./gradlew bootRun
```

The service will start on `http://localhost:8080`

**Access H2 Console**: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:aicore`
- Username: `sa`
- Password: (leave empty)

### Production Mode (PostgreSQL)

1. **Set up PostgreSQL database**:

```sql
CREATE DATABASE aicore;
CREATE USER aicore WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE aicore TO aicore;
```

2. **Run with production profile**:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Or build and run the JAR:

```bash
./gradlew build
java -jar build/libs/ai-core-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📚 API Documentation

### Swagger UI

Access interactive API documentation at:
- **Development**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Main Endpoints

#### Execute AI Pipeline

```http
POST /api/v1/ai/execute
Content-Type: application/json

{
  "taskType": "PHOTO_ANALYSIS",
  "promptParams": {
    "photographerName": "Alice",
    "genre": "Cityscape"
  },
  "binaryInput": "BASE64_ENCODED_IMAGE",
  "provider": "gemini"
}
```

**Response**:
```json
{
  "success": true,
  "content": "{\"overallScore\": 85, ...}",
  "tokenCount": 1234,
  "provider": "gemini"
}
```

#### Get Template

```http
GET /api/v1/templates/photo-analysis
```

#### Override Template

```http
PUT /api/v1/templates/photo-analysis
Content-Type: application/json

{
  "content": "Your custom prompt template here..."
}
```

#### Get Cost Summary

```http
GET /api/v1/ai/costs?period=month
```

**Response**:
```json
{
  "period": "month",
  "startDate": "2024-10-24",
  "endDate": "2024-11-24",
  "totalTokens": 125000,
  "totalCost": 3.75,
  "requestCount": 42,
  "breakdown": [
    {
      "provider": "gemini",
      "taskType": "PHOTO_ANALYSIS",
      "tokens": 50000,
      "cost": 0.25,
      "requests": 20
    }
  ]
}
```

### Available Task Types

- `PHOTO_ANALYSIS`: Analyze individual photos for quality and composition
- `PORTFOLIO_REVIEW`: Review entire photography portfolios
- `SCOUTING`: Analyze locations for photography potential

## 🔧 Extending the Service

### Adding a New Pipeline

1. **Create a new template** in `src/main/resources/prompts/`:

```yaml
name: my-new-task
version: "1.0"
description: "Description of the task"
parameters:
  - param1
  - param2
content: |
  Your prompt template here with {{param1}} and {{param2}}
```

2. **Add TaskType enum** in `PipelineModels.kt`:

```kotlin
enum class TaskType {
    PHOTO_ANALYSIS,
    PORTFOLIO_REVIEW,
    SCOUTING,
    MY_NEW_TASK  // Add this
}
```

3. **Create Pipeline implementation**:

```kotlin
@Component
class MyNewTaskPipeline(
    private val templateRegistry: TemplateRegistry,
    private val aiClientFactory: AiClientFactory
) : AiPipeline {
    
    override suspend fun execute(context: AiExecutionContext): AiExecutionResult {
        // Implementation similar to PhotoAnalysisPipeline
    }
    
    override fun getTemplateName(): String = "my-new-task"
}
```

4. **Register in PipelineRegistry**:

```kotlin
@Component
class PipelineRegistry(
    // ... existing pipelines
    private val myNewTaskPipeline: MyNewTaskPipeline
) {
    private val pipelines: Map<TaskType, AiPipeline> = mapOf(
        // ... existing mappings
        TaskType.MY_NEW_TASK to myNewTaskPipeline
    )
}
```

### Adding a New AI Provider

1. **Create client implementation**:

```kotlin
@Component
class MyAiProviderClient(
    private val aiProperties: AiProperties,
    private val objectMapper: ObjectMapper
) : AiModelClient {
    
    override suspend fun generate(request: AiRequest): AiResponse {
        // Implement API integration
    }
    
    override fun getProviderName(): String = "my-provider"
}
```

2. **Update configuration** in `application.yml`:

```yaml
ai:
  my-provider:
    api-key: ${MY_PROVIDER_API_KEY:}
    api-url: https://api.myprovider.com/v1/generate
```

3. **Register in AiClientFactory**:

```kotlin
fun getClient(provider: String? = null): AiModelClient {
    return when (selectedProvider.lowercase()) {
        "gemini" -> geminiClient
        "openai" -> openAiClient
        "my-provider" -> myProviderClient  // Add this
        else -> throw AiProviderException("Unknown AI provider: $selectedProvider")
    }
}
```

## 🔒 Security Considerations

- **API Keys**: Never commit API keys to version control. Use environment variables.
- **Logging**: API keys are never logged. Request/response logging excludes sensitive data.
- **CORS**: Currently configured permissively for development. Restrict in production.
- **Authentication**: JWT/JWS authentication not implemented but architecture supports easy addition.

## 📊 Database Schema

### Tables

- **ai_logs**: Request/response logging
  - Tracks all AI requests with timestamps, token counts, success status
  
- **ai_costs**: Cost tracking and aggregation
  - Daily aggregation by provider and task type
  
- **prompt_templates**: Optional database storage for templates
  - Templates primarily loaded from YAML files

### Migrations

Flyway manages database migrations automatically. Migration scripts are in:
`src/main/resources/db/migration/`

## 🧪 Testing

Run tests with:

```bash
./gradlew test
```

## 📝 Logging

### Development
- Console output with readable formatting
- SQL queries logged
- Debug level for application code

### Production
- JSON structured logging (Logstash format)
- Suitable for log aggregation systems (ELK, Splunk)
- Warn level for general logs, Info for application code

## 🐳 Docker Support (Optional)

Create a `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/ai-core-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
./gradlew build
docker build -t ai-core-service .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e GEMINI_API_KEY=your_key \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/aicore \
  ai-core-service
```

## 📞 Support

For issues or questions, contact the AI Core Service team at support@example.com

## 📄 License

Copyright © 2024 Example Company. All rights reserved.
