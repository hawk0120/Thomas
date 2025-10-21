package hawk0120

import hawk0120.entities.ArchivalMemory
import hawk0120.entities.WorkingMemory
import hawk0120.services.MemoryService
import hawk0120.services.PromptBuilder
import hawk0120.services.PromptService
import hawk0120.tools.GetMemoryStrategy
import hawk0120.tools.GetUserInputCLIStrategy
import hawk0120.tools.PostOutputStrategy
import hawk0120.tools.SaveMemoryStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Reflector(
    private val llm: LLMClient,
    private val promptBuilder: PromptBuilder,
    private val memoryService: MemoryService
) {
    val memories: MutableList<WorkingMemory> = memoryService.recallWorkingMemory()
    var personaId: String = PERSONA_THOMAS

    fun reflect(memory: String, author: String?): String {
        if (author != null) {
            personaId = author
        }
        memoryService.checkMemoryExists(memories, memory, personaId)

        val prompt =
            promptBuilder
                .setPersona(personaId)
                .setTimeAwareness()
                .setMemories()
                .setInteraction(memory)
                .build()

        val output = llm.query(prompt)

        memoryService.checkMemoryExists(memories, output, PERSONA_THOMAS)
        val response = StringBuilder().append("$PERSONA_THOMAS:")
        response.append(output)

        return response.toString()
    }


}

@Component
class ReflectorRunner(
    @Autowired private val promptBuilder: PromptBuilder,
    @Autowired private val memoryService: MemoryService,
    @Autowired private val reflector: Reflector,
    @Autowired private val blueSkyService: hawk0120.services.BlueSkyService,
    @Autowired private val postOutputStrategy: PostOutputStrategy,
    @Autowired private val getUserInputCLIStrategy: GetUserInputCLIStrategy,
    @Autowired private val promptService: PromptService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val toolCommands = listOf("SAVE_MEMORY", "GET_USER_INPUT_CLI", "GET_MEMORY", "POST_OUTPUT")


        while (true) {
            println("Thinking Loop running")
            if (blueSkyService.login()) {
                println("Getting bluesky mentions")
                blueSkyService.getMentions(reflector)
            }
            val response = reflector.reflect(
                memoryService.recallWorkingMemory().lastOrNull()?.memory ?: promptService.getSystemPrompt(), null
            )
            println(response)

            val foundTool = toolCommands.find { response.contains(it) }
            if (foundTool != null) {
                when (foundTool) {
                    "CALL_FOR_HELP_CLI" -> println(getUserInputCLIStrategy.execute(response))
                    "POST_OUTPUT" -> println(postOutputStrategy.execute(response))
                    "SAVE_MEMORY" -> println(SaveMemoryStrategy(memoryService).execute(response))
                    "GET_MEMORY" -> println(GetMemoryStrategy(memoryService).execute(response))
                }
            }
        }

        memoryService.saveArchivalMemory(
            ArchivalMemory(
                personaId = ADMINISTRATOR,
                memory = Json.encodeToString(memoryService.recallWorkingMemory())
            )
        )

        memoryService.forgetWorkingMemory()

        val exitPrompt = promptBuilder.setExitPrompt().build()

        println(reflector.reflect(exitPrompt, null))
    }
}
