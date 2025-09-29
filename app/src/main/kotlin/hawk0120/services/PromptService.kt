package hawk0120.services

import org.springframework.stereotype.Component

@Component
class PromptService {

    private fun loadResource(path: String): String =
        {}.javaClass.getResourceAsStream(path)
            ?.bufferedReader()
            ?.readText()
            ?: throw IllegalStateException("Resource $path not found")

    fun getExitPrompt(): String {
        return loadResource("/exitPrompt.txt")
    }

    fun getSystemPrompt():String {
        return loadResource("/initialPrompt.txt")
    }
}