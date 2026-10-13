package io.github.dekabrsky.androidMidSemester.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.dekabrsky.androidMidSemester.R

@Composable
fun CountryPickerDialog(
    state: CountriesUiState,
    onSelect: (String) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    onClear: (() -> Unit)? = null,
) {
    var search by rememberSaveable { mutableStateOf("") }
    val visibleCountries = state.countries.filter { it.contains(search.trim(), ignoreCase = true) }
    val error = state.error
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.teams_country)) },
        dismissButton = {
            onClear?.let { clear ->
                TextButton(onClick = clear) { Text(stringResource(R.string.matches_country_clear)) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.match_details_close)) }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text(stringResource(R.string.teams_country_search)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                when {
                    state.loading -> Box(modifier = Modifier.heightIn(min = 100.dp, max = 200.dp)) {
                        LoadingState()
                    }
                    error != null -> {
                        Text(errorMessage(error))
                        TextButton(onClick = onRetry) { Text(stringResource(R.string.teams_retry)) }
                    }
                    visibleCountries.isEmpty() -> Text(stringResource(R.string.teams_countries_empty))
                    else -> LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                        items(visibleCountries, key = { it }) { country ->
                            TextButton(onClick = { onSelect(country) }, modifier = Modifier.fillMaxWidth()) {
                                Text(country)
                            }
                        }
                    }
                }
            }
        },
    )
}


