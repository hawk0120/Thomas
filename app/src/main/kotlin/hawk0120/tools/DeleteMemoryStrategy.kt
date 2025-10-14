package hawk0120.tools

import hawk0120.services.MemoryService
import org.springframework.beans.factory.annotation.Autowired

class DeleteMemoryStrategy @Autowired constructor(
    private val memoryService: MemoryService
) : ToolStrategy<String, String> {
    override fun execute(input: String): String {
        memoryService.forgetWorkingMemory()
        return "Working memory deleted."
    }
}
