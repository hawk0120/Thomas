package hawk0120.entities

import jakarta.persistence.*
import kotlinx.serialization.Serializable


@Serializable
@Entity
@Table(name = "WorkingMemory")
data class WorkingMemory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(columnDefinition = "TEXT")
    var memory: String,
    var personaId: String? = null,
) {
    constructor() : this(0, "", null)
}


@Serializable
@Entity
@Table(name = "ArchivalMemory")
data class ArchivalMemory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    @Column(columnDefinition = "TEXT")
    var memory: String,
    var personaId: String? = null,
) {
    constructor() : this(0, "", null)
}


// Add a bluesky message

