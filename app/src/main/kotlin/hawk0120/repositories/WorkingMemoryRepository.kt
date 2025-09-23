package hawk0120.repositories

import hawk0120.WorkingMemory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface WorkingMemoryRepository : JpaRepository<WorkingMemory, Long> {
    fun findByPersonaId(personaId: String): List<WorkingMemory>
//    fun findByTime(personaId: String): List<WorkingMemory>
}