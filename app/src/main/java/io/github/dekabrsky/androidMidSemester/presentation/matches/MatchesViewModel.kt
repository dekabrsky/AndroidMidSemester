package io.github.dekabrsky.androidMidSemester.presentation.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dekabrsky.androidMidSemester.SettingsRepository
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.domain.usecase.GetMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(val date: LocalDate, val matches: List<Match>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}

class MatchesViewModel(
    private val getMatches: GetMatchesUseCase,
    private val settingsRepository: SettingsRepository, // <-- Инжектим сюда, а не в UseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()

    private var currentDate = LocalDate.now()
    private var favoriteTeamIds: Set<Long> = emptySet()

    init {
        // Слушаем изменения избранных команд
        viewModelScope.launch {
            settingsRepository.favoriteTeamIds.collect { ids ->
                favoriteTeamIds = ids
                // При изменении настроек перезагружаем список с фильтром
                load(currentDate)
            }
        }
        // Первичная загрузка
        load(currentDate)
    }

    fun previousDay() {
        currentDate = currentDate.minusDays(1)
        load(currentDate)
    }

    fun nextDay() {
        currentDate = currentDate.plusDays(1)
        load(currentDate)
    }

    private fun load(date: LocalDate) {
        _state.value = MatchesUiState.Loading
        viewModelScope.launch {
            runCatching { getMatches(date) } // Вызываем UseCase как раньше (1 аргумент)
                .onSuccess { allMatches ->
                    // ФИЛЬТРАЦИЯ происходит здесь, на уровне ViewModel
                    val filteredMatches = if (favoriteTeamIds.isEmpty()) {
                        allMatches // Если ничего не выбрано, показываем все матчи
                    } else {
                        allMatches.filter { match ->
                            favoriteTeamIds.contains(match.home.id) || favoriteTeamIds.contains(match.away.id)
                        }
                    }
                    _state.value = MatchesUiState.Content(date, filteredMatches)
                }
                .onFailure { e ->
                    _state.value = MatchesUiState.Error(e.message ?: "Не удалось загрузить матчи")
                }
        }
    }
}
