package hawk0120.tools

import org.springframework.stereotype.Component

@Component
class GetUserInputCLIStrategy : ToolStrategy<String, String> {
    override fun execute(input: String): String {
        println("GetUserInputStrategy executed")
        val prompt = if (input.isBlank()) "Brady: " else input
        print(prompt)
        val line = readLine()
        return line ?: ""
    }
}