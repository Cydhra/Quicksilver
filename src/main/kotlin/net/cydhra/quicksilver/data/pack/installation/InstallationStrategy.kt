package net.cydhra.quicksilver.data.pack.installation

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
sealed class InstallationStrategy {

    @Serializable
    class ExecuteStrategy(
        val elevated: Boolean,
        val path: String,
        val arguments: String
    ) : InstallationStrategy() {

        override fun install(basePath: File) {
            TODO("not implemented")
        }
    }

    @Serializable
    class RegistryStrategy(val file: String) : InstallationStrategy() {
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