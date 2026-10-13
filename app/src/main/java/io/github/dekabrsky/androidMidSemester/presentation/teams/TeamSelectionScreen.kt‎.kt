package io.github.dekabrsky.androidMidSemester.presentation.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R
import io.github.dekabrsky.androidMidSemester.domain.model.Team
import io.github.dekabrsky.androidMidSemester.presentation.ui.common.LoadingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamSelectionScreen(
    onBack: () -> Unit,
    onApplied: () -> Unit,
    viewModel: TeamSelectionViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val content = state as? TeamSelectionUiState.Content

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.ic_arrow_left), stringResource(R.string.teams_back))
                }
                Text(
                    text = stringResource(R.string.teams_title),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                TextButton(onClick = viewModel::clearSelection, enabled = content != null) {
                    Text(stringResource(R.string.teams_reset))
                }
            }
        },
        bottomBar = {
            Surface(shadowElevation = 3.dp) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.applySelection()
                        onApplied()
                    },
                    enabled = content != null,
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(16.dp),
                ) {
                    Text(stringResource(R.string.teams_apply))
                }
            }
        },
    ) { padding ->
        when (val current = state) {
            TeamSelectionUiState.Loading -> LoadingState(Modifier.padding(padding))
            is TeamSelectionUiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(current.message, style = MaterialTheme.typography.bodyLarge)
                Button(onClick = viewModel::loadTeams, modifier = Modifier.padding(top = 16.dp)) {
                    Text(stringResource(R.string.teams_retry))
                }
            }
            is TeamSelectionUiState.Content -> Column(
                modifier = Modifier.fillMaxSize().padding(padding),
            ) {
                OutlinedTextField(
                    value = current.query,
                    onValueChange = viewModel::search,
                    label = { Text(stringResource(R.string.teams_search)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                )
                Text(
                    text = stringResource(R.string.teams_selection_count, current.selectedIds.size),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(R.string.teams_empty_selection_hint),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val visibleTeams = current.visibleTeams
                if (visibleTeams.isEmpty()) {
                    Text(
                        stringResource(R.string.teams_no_results),
                        modifier = Modifier.padding(24.dp),
                    )
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(visibleTeams, key = { it.id }) { team ->
                            TeamSelectionItem(
                                team = team,
                                selected = team.id in current.selectedIds,
                                onToggle = {
                                    focusManager.clearFocus()
                                    viewModel.toggleTeam(team.id)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamSelectionItem(team: Team, selected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = team.logoUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_team_placeholder),
            error = painterResource(R.drawable.ic_team_placeholder),
            fallback = painterResource(R.drawable.ic_team_placeholder),
            modifier = Modifier.size(40.dp),
        )
        Text(team.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Checkbox(checked = selected, onCheckedChange = null)
    }
}