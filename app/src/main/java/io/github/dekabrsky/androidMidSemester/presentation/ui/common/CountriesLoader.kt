package io.github.dekabrsky.androidMidSemester.presentation.ui.common

import io.github.dekabrsky.androidMidSemester.domain.repository.CountriesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CountriesUiState(
    val countries: List<String> = emptyList(),
    val loading: Boolean = false,
    val error: Throwable? = null,
)

class CountriesLoader(
    private val repository: CountriesRepository,
    private val scope: CoroutineScope,
) {
    private val mutableState = MutableStateFlow(CountriesUiState())
    val state = mutableState.asStateFlow()
    private var job: Job? = null

    fun load() {
        if (job?.isActive == true || mutableState.value.countries.isNotEmpty()) return
        mutableState.value = CountriesUiState(loading = true)
        job = scope.launch {
            try {
                mutableState.value = CountriesUiState(countries = repository.countries())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.value = CountriesUiState(error = e)
            }
        }
    }
}
