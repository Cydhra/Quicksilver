package net.cydhra.quicksilver.data.pack.installation

import kotlinx.serialization.Serializable

@Serializable
class InstallationStep(
    val tree: Array<String>,
    val keep: Boolean,
    val step: InstallationStepType
)

@Serializable
enum class InstallationStepType {
    UNPACK,
    EXECUTE,
    REGISTRY
}