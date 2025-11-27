package com.example.backend.controller

import com.example.backend.model.Article
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles")
class ArticleController {

    @GetMapping
    fun getArticles(): List<Article> {
        return listOf(
            Article(1, "Laptop", "A1001", 999.99),
            Article(2, "Mouse", "A1002", 25.50),
            Article(3, "Keyboard", "A1003", 45.00),
            Article(4, "Monitor", "A1004", 150.00)
        )
    }
}
