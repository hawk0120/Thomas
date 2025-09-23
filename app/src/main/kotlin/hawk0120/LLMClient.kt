package hawk0120

import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.io.*
import java.net.URL

@Service
class LLMClient {
    fun query(prompt: String): String {
        val url = URL("http://localhost:11434/api/generate")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val requestBody = """
            {
              "model": "gemma2:2b",
              "prompt": "$prompt",
              "stream": false
            }
        """.trimIndent()

        OutputStreamWriter(connection.outputStream).use { it.write(requestBody) }

        val raw = connection.inputStream.bufferedReader().use { it.readText() }
        return extractResponse(raw)
    }

    private fun extractResponse(rawJson: String): String {
				val marker = "\"response\":\""
        val start = rawJson.indexOf(marker)
        val end = rawJson.indexOf("\"", start + marker.length)
        return if (start != -1 && end != -1) {
            rawJson.substring(start + marker.length, end)
        } else rawJson
    }



}

