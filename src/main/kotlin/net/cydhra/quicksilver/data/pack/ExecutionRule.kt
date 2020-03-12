package net.cydhra.quicksilver.data.pack

import kotlinx.serialization.Serializable

/**
 * A rule set defining how to execute the game
 */
@Serializable
class ExecutionRule(
    val prerequisites: List<ExecutionPrerequisite>,
    val path: String,
    val workingDirectory: String = ".",
    val arguments: String = ""
)