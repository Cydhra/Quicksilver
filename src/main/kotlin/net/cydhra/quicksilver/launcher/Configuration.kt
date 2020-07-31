package net.cydhra.quicksilver.launcher

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val libraries: MutableList<String>
)