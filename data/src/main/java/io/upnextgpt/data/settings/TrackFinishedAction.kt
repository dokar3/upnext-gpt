package io.upnextgpt.data.settings

enum class TrackFinishedAction(
    val key: String,
) {
    None("none"),
    Pause("pause"),
    PauseAndOpenApp("pause_and_open_app"),
    OpenPlayerToPlayNext("open_player_to_play_next"),
}