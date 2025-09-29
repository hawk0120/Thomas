package hawk0120

import hawk0120.entities.ArchivalMemory
import hawk0120.entities.WorkingMemory
import hawk0120.services.MemoryService
import hawk0120.services.PromptBuilder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hibernate.type.descriptor.DateTimeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class Reflector(
    private val llm: LLMClient,
    private val promptBuilder: PromptBuilder,
    private val memoryService: MemoryService
) {
    val memories: MutableList<WorkingMemory> = memoryService.recallWorkingMemory()

    fun reflect(input: String, personaId: String): String {

        memoryService.checkMemoryExists(memories, input, personaId, 1)

        val prompt = promptBuilder
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
        while (true) {
            print("User:>>> ")
            val input = readLine() ?: break
            if (input.equals(".exit")) {
                break
            }

            val response = reflector.reflect(input, "Brady")
            println(response)
        }

        memoryService.saveArchivalMemory(
            ArchivalMemory(
                id = Integer.valueOf(LocalDate.now().toString()),
                personaId = "Brady",
                memory = Json.encodeToString(memoryService.recallWorkingMemory())
            )
        )

        memoryService.forgetWorkingMemory()

        val exitPrompt = promptBuilder
            .setExitPrompt()
            .build()

        println(reflector.reflect(exitPrompt, "Brady"))
    }
}
