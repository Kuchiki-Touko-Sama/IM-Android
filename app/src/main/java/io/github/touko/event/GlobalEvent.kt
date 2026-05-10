package io.github.touko.event

import kotlinx.coroutines.flow.MutableSharedFlow


object GlobalEvent {
    val unauthorized = MutableSharedFlow<Unit>(
        replay = 1
    )
}