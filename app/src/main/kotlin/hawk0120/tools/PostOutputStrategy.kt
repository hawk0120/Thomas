package hawk0120.tools

import hawk0120.services.BlueSkyService
import org.springframework.stereotype.Component
import io.github.cdimascio.dotenv.dotenv

@Component
class PostOutputStrategy : ToolStrategy<String, String> {
    override fun execute(input: String): String {
        val dotenv = dotenv()

        val username = dotenv.get("BLUESKY_USERNAME") ?: return "Bluesky username not set"
        val password = dotenv.get("BLUESKY_PASSWORD") ?: return "Bluesky password not set"
        val service = BlueSkyService(username, password)
        if (!service.login()) {
            return "Bluesky login failed"
        }
        val success = service.postToBluesky(input)
        return if (success) "Posted to Bluesky successfully" else "Failed to post to Bluesky"
    }
}