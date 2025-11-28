package hawk0120.services

import org.springframework.stereotype.Component

@Component
class PromptService {
    private fun loadResource(path: String): String =
        {}
            .javaClass
            .getResourceAsStream(path)
            ?.bufferedReader()
            ?.readText()
            ?: throw IllegalStateException("Resource $path not found")

    fun getExitPrompt(): String = loadResource("/exitPrompt.txt")

    fun getSystemPrompt(): String = loadResource("/initialPrompt.txt")
}
