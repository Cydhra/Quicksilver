package net.cydhra.quicksilver.data.pack.installation

import com.profesorfalken.jpowershell.PowerShell
import kotlinx.serialization.Serializable
import net.cydhra.quicksilver.environment.Environment
import java.io.File

@Serializable
sealed class InstallationStrategy {

    @Serializable
    class ExecuteStrategy(
        val elevated: Boolean,
        val path: String,
        val workingDir: String,
        val arguments: String
    ) : InstallationStrategy() {

        override fun install(basePath: File) {
            val executable = File(basePath, path)
            val workingDirectory = File(basePath, workingDir)
            Environment.startProcess(workingDirectory, executable, arguments, elevated)
        }
    }

    @Serializable
    class RegistryStrategy(val file: String) : InstallationStrategy() {
        override fun install(basePath: File) {
            PowerShell.executeSingleCommand("REG IMPORT $file")
        }
    }

    /**
     * Execute the installation step with a strategy depending on the implementing subclass
     *
     * @param basePath the folder in which the installation takes place
     */
    abstract fun install(basePath: File)
}