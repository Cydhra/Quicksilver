package net.cydhra.quicksilver.data.pack.installation

import com.profesorfalken.jpowershell.PowerShell
import kotlinx.serialization.Serializable
import net.cydhra.quicksilver.environment.Environment
import java.io.File

@Serializable
sealed class InstallationStrategy {

    /**
     * An installation step that executes a file, optionally with elevated permissions. Can be used to run installers.
     */
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
            val future = Environment.startProcess(workingDirectory, executable, arguments, elevated)
            println("process \"$path\" exited with code ${future.get()}")
        }
    }

    /**
     * An installation step that imports a registry file into registry. Can be used to add or alter registry entries.
     */
    @Serializable
    class RegistryStrategy(val file: String) : InstallationStrategy() {
        override fun install(basePath: File) {
            PowerShell.executeSingleCommand("REG IMPORT $file")
        }
    }

    /**
     * An installation step that deletes a given set of files. Can be used to cleanup installation resources that are no
     * longer needed after installation
     */
    @Serializable
    class DeleteFilesStrategy(val files: Array<String>) : InstallationStrategy() {
        override fun install(basePath: File) {
            this.files.forEach { file ->
                File(basePath, file).delete()
            }
        }
    }

    /**
     * Execute the installation step with a strategy depending on the implementing subclass
     *
     * @param basePath the folder in which the installation takes place
     */
    abstract fun install(basePath: File)
}