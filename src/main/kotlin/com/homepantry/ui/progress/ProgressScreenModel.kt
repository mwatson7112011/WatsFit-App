package com.homepantry.ui.progress

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.homepantry.data.repository.HealthRepository
import com.homepantry.domain.model.HeartRateContext
import com.homepantry.domain.model.HeartRateLog
import com.homepantry.domain.model.WeightLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

data class ProgressState(
    val weights: List<WeightLog> = emptyList(),
    val heartRates: List<HeartRateLog> = emptyList(),
    val latestWeight: WeightLog? = null,
    val firstWeight: WeightLog? = null,
    val avgRestingHeartRate: Double? = null,
    val targetWeight: Double = 180.0
)

class ProgressScreenModel(
    private val healthRepository: HealthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        screenModelScope.launch {
            val weights = healthRepository.getAllWeights().sortedBy { it.date }
            val heartRates = healthRepository.getAllHeartRates().sortedByDescending { "${it.date}T${it.time}" }
            val latestWeight = healthRepository.getLatestWeight()
            val firstWeight = healthRepository.getFirstWeight()
            
            val thirtyDaysAgo = Clock.System.todayIn(TimeZone.currentSystemDefault())
                .minus(30, DateTimeUnit.DAY).toString()
            val avgResting = healthRepository.getRestingAverage(thirtyDaysAgo)

            _state.value = ProgressState(
                weights = weights,
                heartRates = heartRates,
                latestWeight = latestWeight,
                firstWeight = firstWeight,
                avgRestingHeartRate = avgResting
            )
        }
    }

    fun logWeight(date: String, weightLbs: Double, notes: String?) {
        screenModelScope.launch {
            healthRepository.logWeight(date, weightLbs, notes)
            loadData()
        }
    }

    fun logHeartRate(date: String, time: String, bpm: Int, context: HeartRateContext, notes: String?) {
        screenModelScope.launch {
            healthRepository.logHeartRate(date, time, bpm, context.name, notes)
            loadData()
        }
    }

    fun deleteWeight(id: Long) {
        screenModelScope.launch {
            healthRepository.deleteWeight(id)
            loadData()
        }
    }

    fun deleteHeartRate(id: Long) {
        screenModelScope.launch {
            healthRepository.deleteHeartRate(id)
            loadData()
        }
    }
}
