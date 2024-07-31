package com.example.wetter_app.Logic

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UnitSystem {
    private val _isMetric = MutableStateFlow(true)
    val isMetric: StateFlow<Boolean> get() = _isMetric

    fun toggleUnitSystem() {
        _isMetric.value = !_isMetric.value
    }
}