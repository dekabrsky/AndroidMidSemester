package io.github.dekabrsky.androidMidSemester.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.dekabrsky.androidMidSemester.domain.model.Match
import io.github.dekabrsky.androidMidSemester.presentation.details.MatchDetailsDialog
import io.github.dekabrsky.androidMidSemester.presentation.matches.MatchesScreen
import io.github.dekabrsky.androidMidSemester.presentation.teams.TeamsScreen

/** Маркер-интерфейс для всех узлов навигации приложения. */
interface Route

/** Список матчей — стартовый узел. */
data object MatchesList : Route

/** Детали матча — показывается в диалоговом окне (DialogSceneStrategy). */
data class MatchDetails(val match: Match) : Route

/** Выбор команд, матчи которых показывать в списке. Обычный полноэкранный узел. */
data object TeamsSelection : Route

class Navigator(startDestination: Route) {
    val backStack: SnapshotStateList<Route> = mutableStateListOf(startDestination)

    fun goTo(destination: Route) {
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}

@Composable
fun MatchesNavRoot(navigator: Navigator = remember { Navigator(MatchesList) }) {
    val dialogSceneStrategy = remember { DialogSceneStrategy<Route>() }

    NavDisplay(
        backStack = navigator.backStack,
        onBack = navigator::goBack,
        sceneStrategy = dialogSceneStrategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<MatchesList> {
                MatchesScreen(
                    onMatchClick = { match -> navigator.goTo(MatchDetails(match)) },
                    onTeamsClick = { navigator.goTo(TeamsSelection) },
                )
            }
            entry<TeamsSelection> {
                TeamsScreen(onBack = navigator::goBack)
            }
            entry<MatchDetails>(
                metadata = DialogSceneStrategy.dialog()
            ) { route ->
                MatchDetailsDialog(
                    match = route.match,
                    onDismiss = navigator::goBack,
                )
            }
        },
    )
}
