package hawk0120;

import jakarta.persistence.*
import org.hibernate.type.descriptor.DateTimeUtils
import java.sql.Timestamp
import java.time.Instant

@Entity
@Table(name = "WorkingMemory")
data class WorkingMemory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    var memory: String,
    var personaId: String? = null,
) {
    constructor() : this(0, "", null)
}


@Entity
@Table(name = "ArchivalMemory")
data class ArchivalMemory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    var memory: String,
    var personaId: String? = null,
) {
    constructor() : this(0, "", null)
}



