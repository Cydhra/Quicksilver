package net.cydhra.quicksilver.data.pack

import kotlinx.serialization.Serializable

@Serializable
class InstallationStep(
    val type: InstallationStepType
)

@Serializable
enum class InstallationStepType {
    EXECUTE,
    REGISTRY
}