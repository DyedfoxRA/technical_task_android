package com.venture.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import com.venture.core.domain.results.BaseError
import com.venture.core.domain.results.ResultResponse


@Composable
fun <T, E : BaseError> ResultResponse<T, E>.DisplayResult(
    onIdle: (@Composable () -> Unit)? = null,
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable (E) -> Unit,
) {
    AnimatedContent(
        targetState = this,
        transitionSpec = {
            fadeIn(tween(durationMillis = 300)) togetherWith
                    fadeOut(tween(durationMillis = 300))
        },
        label = "Content Animation"
    ) { state ->
        when (state) {
            is ResultResponse.Idle -> {
                onIdle?.invoke()
            }
            is ResultResponse.Loading -> {
                onLoading()
            }
            is ResultResponse.Success -> {
                onSuccess(state.data)
            }
            is ResultResponse.Error -> {
                onError(state.error)
            }
        }
    }
}