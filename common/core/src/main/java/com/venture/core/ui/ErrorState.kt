package com.venture.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venture.core.domain.results.BaseError

@Composable
fun ErrorState(
    error: BaseError?,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error: ${error?.javaClass?.simpleName}",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
        if (onBack != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack) {
                Text(text = "Back")
            }
        }
    }
}