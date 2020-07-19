package net.cydhra.quicksilver.data.pack.installation

import kotlinx.serialization.Serializable

@Serializable
open class InstallationStep(
    val type: InstallationStepType
)

@Serializable
enum class InstallationStepType {
    EXECUTE,
    REGISTRY
}