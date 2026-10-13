package io.github.dekabrsky.androidMidSemester.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.dekabrsky.androidMidSemester.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Composable
fun errorMessage(error: Throwable): String = when (error) {
    is UnknownHostException -> stringResource(R.string.error_connection)
    is SocketTimeoutException -> stringResource(R.string.error_timeout)
    is IOException -> stringResource(R.string.error_network)
    else -> stringResource(R.string.error_loading)
}