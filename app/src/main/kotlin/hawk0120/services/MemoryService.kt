package hawk0120.services

import hawk0120.entities.ArchivalMemory
import hawk0120.entities.WorkingMemory
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

        fun recallWorkingMemory(): MutableList<WorkingMemory> = workingMemoryRepository.findAll()

        fun saveWorkingMemory(workingMemory: String, personaId: String, id: Int) {

                workingMemoryRepository.save(
                        WorkingMemory(personaId = personaId, id = id, memory = workingMemory)
                )
        }

        fun forgetWorkingMemory() = workingMemoryRepository.deleteAll()

        fun saveArchivalMemory(archivalMemory: ArchivalMemory) =
                archivalMemoryRepository.save(archivalMemory)

        fun recallPriorConversationsWithPersonaId(personaId: String): List<ArchivalMemory> =
                archivalMemoryRepository.findByPersonaId(personaId)

        fun getArchivalMemory(): List<ArchivalMemory> = archivalMemoryRepository.findAll()

        fun getAllMemory(personaId: String): Pair<List<WorkingMemory>, List<ArchivalMemory>> {
                val working = workingMemoryRepository.findByPersonaId(personaId)
                val archival = archivalMemoryRepository.findByPersonaId(personaId)
                return working to archival
        }

        fun checkMemoryExists(
                memories: MutableList<WorkingMemory>,
                memory: String,
                personaId: String,
                id: Int
        ) {
                if (memories.none { it.memory == memory }) {
                        saveWorkingMemory(memory, personaId, id)
                }
        }
}
