package io.upnextgpt.remote.palyer

interface PlayerActions {
    fun play()

    fun pause()

    fun prev()

    fun next()

    fun seek(position: Long)
}