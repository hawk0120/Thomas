package hawk0120

import hawk0120.entities.ArchivalMemory
import hawk0120.entities.WorkingMemory
import hawk0120.services.MemoryService
import hawk0120.services.PromptBuilder
import hawk0120.tools.DeleteMemoryStrategy
import hawk0120.tools.GetMemoryStrategy
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

    fun reflect(input: String, personaId: String): String {

        memoryService.checkMemoryExists(memories, input, personaId, 1)

        val prompt =
            promptBuilder
                .setSysPrompt()
                .setPersona(personaId)
                .setTimeAwareness()
                .setMemories()
                .setInteraction(input)
                .build()

        val output = llm.query(prompt)

        memoryService.checkMemoryExists(memories, output, "Thomas", 2)
        val response = StringBuilder().append("Thomas: ")
        response.append(output)

        return response.toString()
    }
}

@Component
class ReflectorRunner(
    @Autowired private val promptBuilder: PromptBuilder,
    @Autowired private val memoryService: MemoryService,
    @Autowired private val reflector: Reflector,
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        val toolCommands = listOf("SAVE_MEMORY", "DELETE_MEMORY", "GET_MEMORY", "POST_OUTPUT")

        while (true) {
            print("User:>>> ")
            val input = readLine() ?: break

            if (input.equals(".exit")) {
                break
            }

            val response = reflector.reflect(input, "Brady")
            println(response)
            val foundTool = toolCommands.find { response.contains(it) }
            if (foundTool != null) {
                when (foundTool) {
                    "POST_OUTPUT" -> PostOutputStrategy().execute(response)
                    "SAVE_MEMORY" -> println(SaveMemoryStrategy(memoryService).execute(response))
                    "DELETE_MEMORY" -> println(DeleteMemoryStrategy(memoryService).execute(response))
                    "GET_MEMORY" -> println(GetMemoryStrategy(memoryService).execute(response))
                }
            }



        }

        memoryService.saveArchivalMemory(
            ArchivalMemory(
                personaId = "Brady",
                memory = Json.encodeToString(memoryService.recallWorkingMemory())
            )
        )

        memoryService.forgetWorkingMemory()

        val exitPrompt = promptBuilder.setExitPrompt().build()

        println(reflector.reflect(exitPrompt, "Brady"))
    }
}
