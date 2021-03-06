package io.graphqlfederation.planetservice.service

import io.graphqlfederation.planetservice.model.Planet
import io.graphqlfederation.planetservice.repository.PlanetRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Singleton
class PlanetService(
    private val repository: PlanetRepository,
    private val detailsService: DetailsService
) {

    private val publishSubject = PublishSubject.create<Planet>()

    fun getAll(): Iterable<Planet> = repository.findAll()

    fun getById(id: Long): Planet? = repository.findById(id).orElse(null)

    fun getByName(name: String): Planet? = repository.findByName(name)

    fun create(
        name: String,
        type: Planet.Type,
        meanRadius: Double,
        massNumber: Double,
        massTenPower: Int,
        population: Double? = null
    ): Planet {
        val mass = massNumber.toBigDecimal().scaleByPowerOfTen(massTenPower)
        val details = detailsService.create(meanRadius, mass, population)

        return Planet(name = name, type = type, detailsId = details.id).also {
            repository.save(it)
            publishSubject.onNext(it)
        }
    }

    fun getLatestPlanet(): Flowable<Planet> = publishSubject.toFlowable(BackpressureStrategy.BUFFER)
}
