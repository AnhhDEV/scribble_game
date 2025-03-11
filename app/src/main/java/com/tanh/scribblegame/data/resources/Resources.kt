package com.tanh.scribblegame.data.resources

sealed interface Resources<out T, out E> {
    data class Success<out D>(val data: D): Resources<D, Nothing>
    data class Error<out E>(val error: E): Resources<Nothing, E>
}

fun <T, E> Resources<T, E>.onSuccess(action: (T) -> Unit): Resources<T, E> {
    return when(this) {
        is Resources.Error -> this
        is Resources.Success -> {
            action(data)
            this
        }
    }
}

fun <T, E> Resources<T, E>.onError(action: (E) -> Unit): Resources<T, E> {
    return when(this) {
        is Resources.Success -> this
        is Resources.Error -> {
            action(error)
            this
        }
    }
}