package net.cydhra.quicksilver.data.pack.installation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
class InstallationStep(
    val tree: Array<String>,
    val keep: Boolean,
    @SerialName("parameters") val strategy: StepStrategy = StepStrategy.NoStrategy
) {
    /**
     * Execute this installation step
     *
     * @param basePath the folder in which the installation takes place
     */
    fun install(basePath: File) {
        strategy.install(basePath)
    }
}

@Serializable
sealed class StepStrategy {

    @Serializable
    object NoStrategy : StepStrategy() {
        override fun install(basePath: File) {}
    }

    @Serializable
    class ExecuteStepStrategy(
        val elevated: Boolean,
        val path: String,
        val arguments: String
    ) : StepStrategy() {

        override fun install(basePath: File) {
            TODO("not implemented")
        }
    }

    @Serializable
    class RegistryStrategy(val file: String) : StepStrategy() {
        override fun install(basePath: File) {
            TODO("not implemented")
        }
    }

    /**
     * Execute the installation step with a strategy depending on the implementing subclass
     *
     * @param basePath the folder in which the installation takes place
     */
    abstract fun install(basePath: File)
}