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

data class ProgressState(
    val weights: List<WeightLog> = emptyList(),
    val heartRates: List<HeartRateLog> = emptyList()
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
            _state.value = ProgressState(weights = weights, heartRates = heartRates)
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
}
