package io.github.dekabrsky.androidMidSemester.presentation.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.dekabrsky.androidMidSemester.R

@Composable
fun TeamLogo(
    logoUrl: String?,
    teamName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    AsyncImage(
        model = logoUrl,
        contentDescription = teamName,
        modifier = modifier.size(size),
        placeholder = painterResource(R.drawable.ic_team_placeholder),
        error = painterResource(R.drawable.ic_team_placeholder),
    )
}
