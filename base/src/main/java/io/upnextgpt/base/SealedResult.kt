package io.upnextgpt.base

sealed interface SealedResult<out T : Any, out E> {
    class Ok<T : Any>(val data: T) : SealedResult<T, Nothing>

    class Err<E>(val error: E) : SealedResult<Nothing, E>
}