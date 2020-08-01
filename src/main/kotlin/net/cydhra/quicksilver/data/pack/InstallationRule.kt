package net.cydhra.quicksilver.data.pack

import kotlinx.serialization.Serializable

/**
 * A rule set defining how to install the game
 */
@Serializable
class InstallationRule(
    val steps: List<InstallationStrategy>
)