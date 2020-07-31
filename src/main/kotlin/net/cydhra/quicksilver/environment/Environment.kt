package net.cydhra.quicksilver.environment

import com.profesorfalken.jpowershell.PowerShell
import com.profesorfalken.jpowershell.PowerShellResponseHandler
import java.io.File

/**
 * A wrapper for system-dependent calls
 */
object Environment {

    /**
     * Start a process using means of the operating system.
     *
     * @param workingDirectory the working directory of the process
     * @param executable the executable to start a process of
     * @param arguments programm arguments
     * @param elevated whether the process should be started with elevated permissions
     */
    fun startProcess(workingDirectory: File, executable: File, arguments: String, elevated: Boolean) {
        // TODO operating system dependent stuff

        val powerShell = PowerShell.openSession()
            .executeCommandAndChain("\$newProcess = new-object System.Diagnostics.ProcessStartInfo \"${executable.absolutePath}\"")
            .executeCommandAndChain("\$newProcess.Arguments = \"$arguments\"")
            .executeCommandAndChain("\$newProcess.WorkingDirectory = \"${workingDirectory.absolutePath}\"")

        if (elevated) {
            powerShell.executeCommandAndChain("\$newProcess.Verb = \"runas\"")
        }

        powerShell
            .executeCommandAndChain("[System.Diagnostics.Process]::Start(\$newProcess)",
                PowerShellResponseHandler { response -> println(response.commandOutput) })
            .close()

        // TODO instead of blocking, use future
    }
}