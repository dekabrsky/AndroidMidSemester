package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.domain.repository.TeamSelectionRepository
import io.github.dekabrsky.androidMidSemester.domain.repository.CountriesRepository
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetTeamsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.CountriesLoader

data class TeamsUiState(
    val loading: Boolean = true,
    val error: Throwable? = null,
    val teams: List<Team> = emptyList(),
    val query: String = "",
    val selectedTeamIds: Set<Long> = emptySet(),
    val country: String = "Russia",
    val showSelected: Boolean = false,
)

private data class TeamsSourceState(
    val loading: Boolean = true,
    val error: Throwable? = null,
    val teams: List<Team> = emptyList(),
    val country: String = "Russia",
    val showSelected: Boolean = false,
)

class TeamsViewModel(
    private val getTeams: GetTeamsUseCase,
    private val selectionRepository: TeamSelectionRepository,
    countriesRepository: CountriesRepository,
) : ViewModel() {
    private val loadedState = MutableStateFlow(TeamsSourceState())
    private val query = MutableStateFlow("")
    private var loadJob: Job? = null
    private val countriesLoader = CountriesLoader(countriesRepository, viewModelScope)
    val countries = countriesLoader.state
    private var loadVersion = 0L

    val state = combine(loadedState, query, selectionRepository.selectedTeams) { loaded, search, selected ->
        val teams = if (loaded.showSelected) {
            selected.values.sortedBy { it.name.lowercase(Locale.ROOT) }
        } else loaded.teams
        TeamsUiState(
            loading = !loaded.showSelected && loaded.loading,
            error = loaded.error.takeUnless { loaded.showSelected },
            teams = teams.filter { it.name.contains(search.trim(), ignoreCase = true) },
            query = search,
            selectedTeamIds = selected.keys,
            country = loaded.country,
            showSelected = loaded.showSelected,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TeamsUiState())

    init {
        retryCountries()
        retry()
    }

    fun search(value: String) {
        query.value = value
    }

    fun setSelected(teamId: Long, selected: Boolean) {
        val team = loadedState.value.teams.find { it.id == teamId }
            ?: selectionRepository.selectedTeams.value[teamId]
            ?: return
        selectionRepository.setSelected(team, selected)
    }

    fun selectCountry(country: String) {
        if (country !in countries.value.countries) return
        val changed = country != loadedState.value.country
        loadedState.update { it.copy(country = country, showSelected = false) }
        query.value = ""
        if (changed) loadTeams()
    }

    fun showSelected(value: Boolean) {
        loadedState.update { it.copy(showSelected = value) }
        query.value = ""
    }

    fun clearSelection() {
        selectionRepository.clear()
    }

    fun retry() {
        if (loadJob?.isActive != true) loadTeams()
    }

    private fun loadTeams() {
        loadJob?.cancel()
        val version = ++loadVersion
        val country = loadedState.value.country
        loadedState.update { it.copy(loading = true, error = null, teams = emptyList()) }
        loadJob = viewModelScope.launch {
            try {
                val teams = getTeams(country)
                if (version == loadVersion) {
                    loadedState.update { it.copy(loading = false, teams = teams) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (version == loadVersion) {
                    loadedState.update {
                        it.copy(loading = false, error = e)
                    }
                }
            }
        }
    }

    fun retryCountries() {
        countriesLoader.load()
    }
}
