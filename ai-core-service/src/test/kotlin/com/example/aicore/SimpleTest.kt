package com.example.aicore

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class SimpleTest {
    
    @Test
    fun `simple test should pass`() {
        assertThat(1 + 1).isEqualTo(2)
    }
}
