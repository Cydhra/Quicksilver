package net.cydhra.quicksilver.data.pack

import kotlinx.serialization.Serializable
import net.cydhra.quicksilver.data.pack.installation.InstallationStep

/**
 * A rule set defining how to install the game
 */
@Serializable
class InstallationRule(
    val steps: List<InstallationStep>
)