package hawk0120.tools

import hawk0120.services.BlueSkyService
import hawk0120.services.MemoryService
import hawk0120.services.PromptBuilder
import hawk0120.PERSONA_THOMAS
import org.springframework.stereotype.Component

@Component
class PostOutputStrategy(private val blueSkyService: BlueSkyService,
                        private val memoryService: MemoryService,
                        private val promptBuilder: PromptBuilder) : ToolStrategy<String, String> {
    override fun execute(input: String): String {
        println("PostOutputStrategy execute - Started")
        if (!blueSkyService.login()) {
            return "Bluesky login failed"
        }
        val message = input.removePrefix("Thomas: POST_OUTPUT\n\n").trimStart()

        val success = blueSkyService.postToBluesky(message)
        promptBuilder.setBlueskyInteraction(input)
        if (success) {
            promptBuilder.setSpecialMessage("This post was successful")
            memoryService.saveWorkingMemory(promptBuilder.build(), PERSONA_THOMAS)
            return "Posted to Bluesky Sucessesfully"
        } else {
            promptBuilder.setSpecialMessage("This post failed")
            memoryService.saveWorkingMemory(promptBuilder.build(), PERSONA_THOMAS)
            return "Failed to post to bluesky"
        }
    }
}