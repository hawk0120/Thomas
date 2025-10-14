package hawk0120.tools

import hawk0120.entities.ArchivalMemory
import hawk0120.services.MemoryService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired

class SaveMemoryStrategy @Autowired constructor(
    private val memoryService: MemoryService
) : ToolStrategy<String, String> {
    override fun execute(input: String): String {
        memoryService.saveArchivalMemory(
            ArchivalMemory(
                personaId = "Brady",
                memory = Json.encodeToString(memoryService.recallWorkingMemory())
            )
        )
        return "Memory saved."
    }
}