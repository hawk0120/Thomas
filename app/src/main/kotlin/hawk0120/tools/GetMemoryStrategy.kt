package hawk0120.tools

import hawk0120.services.MemoryService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired

class GetMemoryStrategy @Autowired constructor(
    private val memoryService: MemoryService
) : ToolStrategy<String, String> {
    override fun execute(input: String): String {
        val memories = memoryService.recallWorkingMemory()
        return "Current working memories: ${Json.encodeToString(memories)}"
    }
}
