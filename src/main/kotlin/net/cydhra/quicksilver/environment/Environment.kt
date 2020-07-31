package net.cydhra.quicksilver.environment

import com.profesorfalken.jpowershell.PowerShell
import com.profesorfalken.jpowershell.PowerShellResponseHandler
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * A wrapper for system-dependent calls
 */
object Environment {

    /**
     * A cached thread pool to run system tasks and wait for their completion
     */
    private val threadPool = Executors.newCachedThreadPool()

    /**
     * Start a process using means of the operating system.
     *
     * @param workingDirectory the working directory of the process
     * @param executable the executable to start a process of
     * @param arguments programm arguments
     * @param elevated whether the process should be started with elevated permissions
     *
     * @return a future on the exit code of the process
     */
    fun startProcess(workingDirectory: File, executable: File, arguments: String, elevated: Boolean): Future<Int> {
        // TODO operating system dependent stuff

        return this.threadPool.submit(Callable {
            val powerShell = PowerShell.openSession()
                .executeCommandAndChain("\$newProcess = new-object System.Diagnostics.Process")
                .executeCommandAndChain("\$newProcess.StartInfo.FileName = \"${executable.absolutePath}\"")
                .executeCommandAndChain("\$newProcess.StartInfo.Arguments = \"$arguments\"")
                .executeCommandAndChain("\$newProcess.StartInfo.WorkingDirectory = \"${workingDirectory.absolutePath}\"")

            if (elevated) {
                powerShell.executeCommandAndChain("\$newProcess.StartInfo.Verb = \"runas\"")
            }

            var exitCode = 0
            powerShell
                .executeCommandAndChain("\$newProcess.Start()",
                    PowerShellResponseHandler { response -> println(response.commandOutput) })
                .executeCommandAndChain("\$newProcess.WaitForExit()",
                    PowerShellResponseHandler { response -> exitCode = response.commandOutput.toInt() })
                .close()

            return@Callable exitCode
        })
    }
}