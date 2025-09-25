package hawk0120

import hawk0120.repositories.ArchivalMemoryRepository
import hawk0120.repositories.WorkingMemoryRepository
import hawk0120.services.MemoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

class Reflector(private val llm: LLMClient, private val memoryService: MemoryService) {

    fun reflect(input: String): String {
        val prompt = "Reflect on the following interactions:\n$input"
        val reflection = llm.query(prompt)
        return reflection
    }

}

@Component
class ReflectorRunner(
    @Autowired private val workingMemoryRepository: WorkingMemoryRepository,
    @Autowired private val archivalMemoryRepository: ArchivalMemoryRepository,
    @Autowired private val llmClient: LLMClient
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        println("Reflector initiated")
        val memoryService = MemoryService(workingMemoryRepository, archivalMemoryRepository)
        val reflector = Reflector(llmClient, memoryService)

        while (true) {
            print("> ")
            val input = readLine() ?: break
            val response = reflector.reflect(input)
            println(response)
        }
    }


}
