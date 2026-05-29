package com.homepantry.data.repository

import com.homepantry.db.PantryDatabase
import com.homepantry.domain.model.HeartRateContext
import com.homepantry.domain.model.HeartRateLog
import com.homepantry.domain.model.WeightLog
import kotlinx.datetime.Clock

class HealthRepository(private val database: PantryDatabase) {

    private val weightQueries = database.weightLogQueries
    private val heartRateQueries = database.heartRateLogQueries

    fun getAllWeights(): List<WeightLog> =
        weightQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getRecentWeights(limit: Long): List<WeightLog> =
        weightQueries.selectRecent(limit).executeAsList().map { it.toDomain() }

    fun getWeightsByDateRange(from: String, to: String): List<WeightLog> =
        weightQueries.selectByDateRange(from, to).executeAsList().map { it.toDomain() }

    fun getLatestWeight(): WeightLog? =
        weightQueries.selectLatest().executeAsOneOrNull()?.toDomain()

    fun getFirstWeight(): WeightLog? =
        weightQueries.selectFirst().executeAsOneOrNull()?.toDomain()

    fun logWeight(date: String, weightLbs: Double, notes: String?) {
        val now = Clock.System.now().toString()
        weightQueries.insert(
            date = date,
            weightLbs = weightLbs,
            notes = notes,
            createdAt = now
        )
    }

    fun deleteWeight(id: Long) {
        weightQueries.deleteById(id)
    }

    fun getWeightCount(): Long =
        weightQueries.countAll().executeAsOne()

    fun getAllHeartRates(): List<HeartRateLog> =
        heartRateQueries.selectAll().executeAsList().map { it.toDomain() }

    fun getRecentHeartRates(limit: Long): List<HeartRateLog> =
        heartRateQueries.selectRecent(limit).executeAsList().map { it.toDomain() }

    fun getHeartRatesByDateRange(from: String, to: String): List<HeartRateLog> =
        heartRateQueries.selectByDateRange(from, to).executeAsList().map { it.toDomain() }

    fun logHeartRate(date: String, time: String, bpm: Int, context: String, notes: String?) {
        val now = Clock.System.now().toString()
        heartRateQueries.insert(
            date = date,
            time = time,
            bpm = bpm.toLong(),
            context = context,
            notes = notes,
            createdAt = now
        )
    }

    fun deleteHeartRate(id: Long) {
        heartRateQueries.deleteById(id)
    }

    private fun com.homepantry.db.WeightLog.toDomain(): WeightLog =
        WeightLog(
            id = id,
            date = date,
            weightLbs = weightLbs,
            notes = notes,
            createdAt = createdAt
        )

    private fun com.homepantry.db.HeartRateLog.toDomain(): HeartRateLog =
        HeartRateLog(
            id = id,
            date = date,
            time = time,
            bpm = bpm.toInt(),
            context = HeartRateContext.fromString(context),
            notes = notes,
            createdAt = createdAt
        )
}
