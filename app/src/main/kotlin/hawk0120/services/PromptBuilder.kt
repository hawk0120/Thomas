package hawk0120.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PromptBuilder(
    @Autowired private val memoryService: MemoryService,
    @Autowired private val promptService: PromptService,
) {
    private var prompt = StringBuilder()

    fun setSysPrompt(): PromptBuilder {
        prompt.append(promptService.getSystemPrompt())
        return this
    }

    fun setTimeAwareness(): PromptBuilder {
        prompt.append("The Current time is: \n")
        prompt.append(LocalDateTime.now())
        prompt.append("\n")
        return this
    }

    fun setPersona(personaId: String): PromptBuilder {
        prompt.append("This input came from: $personaId\n")
        return this
    }


    fun setExitPrompt(): PromptBuilder {
        prompt.append(promptService.getExitPrompt())
        return this
    }

    fun setMemories(): PromptBuilder {
        prompt.append("Your working memory is found here: \n")
        prompt.append(memoryService.recallWorkingMemory())
        return this
    }

    fun setBlueskyInteraction(input: String): PromptBuilder {
        prompt.append("You previously posted this to bluesky")
        return this
    }

    fun setInteraction(input: String): PromptBuilder {
        prompt.append("\nInstructions:\n")
        prompt.append(input)
        return this
    }

    fun setSpecialMessage(input: String): PromptBuilder {
        prompt.append(input)
        return this
    }

    fun build(): String = prompt.toString()
}
