package hawk0120.services
import hawk0120.ArchivalMemory
import hawk0120.WorkingMemory
import hawk0120.repositories.ArchivalMemoryRepository
import hawk0120.repositories.WorkingMemoryRepository
import org.springframework.stereotype.Service

@Service
class MemoryService(
    private val workingMemoryRepository: WorkingMemoryRepository,
    private val archivalMemoryRepository: ArchivalMemoryRepository
) {
    fun getWorkingMemoryById(personaId: String): List<WorkingMemory> =
        workingMemoryRepository.findByPersonaId(personaId)

/*    fun getWorkingMemoryByTime(timestamp: Timestamp, personaId: String): List<WorkingMemory> =
        workingMemoryRepository.findByTime(timestamp, personaId);*/

    fun getArchivalMemory(personaId: String): List<ArchivalMemory> =
        archivalMemoryRepository.findByPersonaId(personaId)

    fun getAllMemory(personaId: String): Pair<List<WorkingMemory>, List<ArchivalMemory>> {
        val working = workingMemoryRepository.findByPersonaId(personaId)
        val archival = archivalMemoryRepository.findByPersonaId(personaId)
        return working to archival
    }
}
