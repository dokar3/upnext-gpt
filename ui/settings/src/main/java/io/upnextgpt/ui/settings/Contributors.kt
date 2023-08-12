package io.upnextgpt.ui.settings

internal data class Contributor(
    val id: Long,
    val name: String,
    val avatar: String,
    val url: String,
    val contributions: Long,
)

internal val GH_CONTRIBUTORS = listOf(
    Contributor(
        id = 68095777,
        name = "dokar3",
        avatar = "https://avatars.githubusercontent.com/u/68095777?v=4",
        url = "https://github.com/dokar3",
        contributions = 117,
    ),
    Contributor(
        id = 29139614,
        name = "renovate[bot]",
        avatar = "https://avatars.githubusercontent.com/in/2740?v=4",
        url = "https://github.com/apps/renovate",
        contributions = 12,
    ),
    Contributor(
        id = 108683123,
        name = "GitGitro",
        avatar = "https://avatars.githubusercontent.com/u/108683123?v=4",
        url = "https://github.com/GitGitro",
        contributions = 4,
    ),
)