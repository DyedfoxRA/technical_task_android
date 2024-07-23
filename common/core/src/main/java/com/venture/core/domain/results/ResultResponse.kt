package com.venture.core.domain.results

sealed interface ResultResponse<out D, out E : BaseError> {

    data object Idle : ResultResponse<Nothing, Nothing>

    data object Loading : ResultResponse<Nothing, Nothing>

    @JvmInline
    value class Success<out D>(val data: D) : ResultResponse<D, Nothing>

    @JvmInline
    value class Error<out E : BaseError>(val error: E) : ResultResponse<Nothing, E>


    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error
}