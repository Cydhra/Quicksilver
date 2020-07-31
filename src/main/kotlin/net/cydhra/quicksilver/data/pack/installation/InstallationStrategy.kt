package net.cydhra.quicksilver.data.pack.installation

import com.profesorfalken.jpowershell.PowerShell
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
            val powerShell = PowerShell.openSession()
                .executeCommandAndChain("\$newProcess = new-object System.Diagnostics.ProcessStartInfo \"$path\"")
                .executeCommandAndChain("\$newProcess.Arguments = \"$arguments\"")

            if (elevated) {
                powerShell.executeCommandAndChain("\$newProcess.Verb = \"runas\"")
            }

            powerShell
                .executeCommandAndChain("[System.Diagnostics.Process]::Start(\$newProcess)")
                .close()
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