package hawk0120.services

import hawk0120.entities.ArchivalMemory
import hawk0120.entities.WorkingMemory
import hawk0120.repositories.ArchivalMemoryRepository
import hawk0120.repositories.WorkingMemoryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*

class MemoryServiceTest {
    class InMemoryWorkingMemoryRepository : WorkingMemoryRepository {
        private val items = mutableListOf<WorkingMemory>()
        private var nextId = 1L

        override fun findByPersonaId(personaId: String): List<WorkingMemory> = items.filter { it.personaId == personaId }
        override fun findAll(): MutableList<WorkingMemory> = items

        override fun <S : WorkingMemory> save(entity: S): S {
            val toSave = if (entity.id == 0) entity.copy(id = nextId.toInt()) else entity
            nextId++
            items.add(toSave)
            @Suppress("UNCHECKED_CAST")
            return toSave as S
        }

        override fun deleteAll() {
            items.clear()
        }

        // --- Unused methods from JpaRepository ---
        override fun <S : WorkingMemory> saveAll(entities: MutableIterable<S>): MutableList<S> = entities.toMutableList()
        override fun findById(id: Long): Optional<WorkingMemory> = Optional.ofNullable(items.find { it.id.toLong() == id })
        override fun existsById(id: Long): Boolean = items.any { it.id.toLong() == id }
        override fun count(): Long = items.size.toLong()
        override fun deleteById(id: Long) { items.removeIf { it.id.toLong() == id } }
        override fun delete(entity: WorkingMemory) { items.remove(entity) }
        override fun deleteAll(entities: MutableIterable<WorkingMemory>) { entities.forEach { items.remove(it) } }
        override fun findAllById(ids: MutableIterable<Long>): MutableList<WorkingMemory> = items.filter { ids.contains(it.id.toLong()) }.toMutableList()

        // The following methods are JpaRepository/Paging and are not used in tests; provide simple stubs
        override fun flush() { /* no-op */ }
        override fun <S : WorkingMemory> saveAndFlush(entity: S): S = save(entity)
        override fun deleteAllInBatch() { items.clear() }
        override fun deleteAllByIdInBatch(ids: MutableIterable<Long>) { ids.forEach { deleteById(it) } }
        override fun deleteInBatch(entities: MutableIterable<WorkingMemory>) { entities.forEach { items.remove(it) } }
        override fun getOne(id: Long): WorkingMemory = findById(id).orElseThrow()
        override fun getById(id: Long): WorkingMemory = getOne(id)
        override fun findAll(sort: Sort?): MutableList<WorkingMemory> = items
        override fun findAll(pageable: Pageable?): Page<WorkingMemory> = throw UnsupportedOperationException("Paging not supported in test repo")
    }

    // Simple in-memory ArchivalMemoryRepository implementation (test-only)
    class InMemoryArchivalMemoryRepository : ArchivalMemoryRepository {
        private val items = mutableListOf<ArchivalMemory>()
        private var nextId = 1L

        override fun findByPersonaId(personaId: String): List<ArchivalMemory> = items.filter { it.personaId == personaId }
        override fun findAll(): MutableList<ArchivalMemory> = items

        override fun <S : ArchivalMemory> save(entity: S): S {
            val toSave = if (entity.id == 0) entity.copy(id = nextId.toInt()) else entity
            nextId++
            items.add(toSave)
            @Suppress("UNCHECKED_CAST")
            return toSave as S
        }

        // --- Unused JpaRepository methods ---
        override fun <S : ArchivalMemory> saveAll(entities: MutableIterable<S>): MutableList<S> = entities.toMutableList()
        override fun findById(id: Long): Optional<ArchivalMemory> = Optional.ofNullable(items.find { it.id.toLong() == id })
        override fun existsById(id: Long): Boolean = items.any { it.id.toLong() == id }
        override fun findAllById(ids: MutableIterable<Long>): MutableList<ArchivalMemory> = items.filter { ids.contains(it.id.toLong()) }.toMutableList()
        override fun count(): Long = items.size.toLong()
        override fun deleteById(id: Long) { items.removeIf { it.id.toLong() == id } }
        override fun delete(entity: ArchivalMemory) { items.remove(entity) }
        override fun deleteAll(entities: MutableIterable<ArchivalMemory>) { entities.forEach { items.remove(it) } }
        override fun deleteAll() { items.clear() }
        override fun flush() { }
        override fun <S : ArchivalMemory> saveAndFlush(entity: S): S = save(entity)
        override fun deleteAllInBatch() { items.clear() }
        override fun deleteAllByIdInBatch(ids: MutableIterable<Long>) { ids.forEach { deleteById(it) } }
        override fun deleteInBatch(entities: MutableIterable<ArchivalMemory>) { entities.forEach { items.remove(it) } }
        override fun getOne(id: Long): ArchivalMemory = findById(id).orElseThrow()
        override fun getById(id: Long): ArchivalMemory = getOne(id)
        override fun findAll(sort: Sort?): MutableList<ArchivalMemory> = items
        override fun findAll(pageable: Pageable?): Page<ArchivalMemory> = throw UnsupportedOperationException("Paging not supported in test repo")
    }

    @Test
    fun `saveWorkingMemory and recallWorkingMemory`() {
        val workingRepo = InMemoryWorkingMemoryRepository()
        val archivalRepo = InMemoryArchivalMemoryRepository()
        val service = MemoryService(workingRepo, archivalRepo)

        service.saveWorkingMemory("m1", "personaA")
        service.saveWorkingMemory("m2", "personaA")

        val all = service.recallWorkingMemory()
        assertEquals(2, all.size)
        assertTrue(all.any { it.memory == "m1" })
        assertTrue(all.any { it.memory == "m2" })
    }

    @Test
    fun `checkMemoryExists should only save new memory`() {
        val workingRepo = InMemoryWorkingMemoryRepository()
        val archivalRepo = InMemoryArchivalMemoryRepository()
        val service = MemoryService(workingRepo, archivalRepo)

        // start with empty memories list
        val memories = mutableListOf<WorkingMemory>()
        service.checkMemoryExists(memories, "unique", "p1")
        // repository should now contain the saved memory
        val all = service.recallWorkingMemory()
        assertEquals(1, all.size)
        assertEquals("unique", all.first().memory)

        // calling again with same memory should not add
        val existing = service.recallWorkingMemory()
        service.checkMemoryExists(existing, "unique", "p1")
        assertEquals(1, service.recallWorkingMemory().size)
    }

    @Test
    fun `getAllMemory returns both working and archival for persona`() {
        val workingRepo = InMemoryWorkingMemoryRepository()
        val archivalRepo = InMemoryArchivalMemoryRepository()
        val service = MemoryService(workingRepo, archivalRepo)

        service.saveWorkingMemory("w1", "pX")
        archivalRepo.save(ArchivalMemory(memory = "a1", personaId = "pX"))

        val (working, archival) = service.getAllMemory("pX")
        assertEquals(1, working.size)
        assertEquals(1, archival.size)
        assertEquals("w1", working[0].memory)
        assertEquals("a1", archival[0].memory)
    }
}

