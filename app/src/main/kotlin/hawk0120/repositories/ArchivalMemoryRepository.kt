package hawk0120.repositories


import hawk0120.ArchivalMemory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArchivalMemoryRepository : JpaRepository<ArchivalMemory, Long> {
    fun findByPersonaId(personaId: String): List<ArchivalMemory>
}


