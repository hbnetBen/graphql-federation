package io.graphqlfederation.satelliteservice.model

import java.time.LocalDate
import javax.persistence.*

@Entity
class Satellite(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String,

    @Column
    val lifeExists: LifeExists,

    @Column
    val firstSpacecraftLandingDate: LocalDate?,

    @Column
    val planetId: Long
) {

    enum class LifeExists {
        YES,
        OPEN_QUESTION,
        NO_DATA
    }
}
