package net.cydhra.quicksilver.environment

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.profesorfalken.jpowershell.PowerShell
import com.profesorfalken.jpowershell.PowerShellResponseHandler
import java.io.File
import java.util.concurrent.Executors

/**
 * A wrapper for system-dependent calls
 */
object Environment {

    /**
     * A cached thread pool to run system tasks and wait for their completion
     */
    private val threadPool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool())

    // TODO this is a dirty hack that is used to elevate execution of a process. However, we cannot wait until the
    //  process exits, because the powershell session is ended before that
    /**
     * Start a process using means of the operating system using elevated permissions.
     *
     * @param workingDirectory the working directory of the process
     * @param executable the executable to start a process of
     * @param arguments programm arguments
     *
     * @return a future on the start of the process. The end of process cannot be detected
     */
    fun startProcessElevated(
        workingDirectory: File,
        executable: File,
        arguments: String
    ): ListenableFuture<*> {
        // TODO operating system dependent stuff

        return this.threadPool.submit {
            PowerShell.openSession()
                .executeCommandAndChain("\$newProcess = new-object System.Diagnostics.Process")
                .executeCommandAndChain("\$newProcess.StartInfo.FileName = \"${executable.absolutePath}\"")
                .executeCommandAndChain("\$newProcess.StartInfo.Arguments = \"$arguments\"")
                .executeCommandAndChain("\$newProcess.StartInfo.WorkingDirectory = \"${workingDirectory.absolutePath}\"")
                .executeCommandAndChain("\$newProcess.StartInfo.Verb = \"runas\"")
                .executeCommandAndChain("\$newProcess.Start()",
                    PowerShellResponseHandler { response -> println(response.commandOutput) })
                .close()
        }
    }

    /**
     * Start a process using means of the operating system and generate a future that waits for the process to exit
     *
     * @param workingDirectory the working directory of the process
     * @param executable the executable to start a process of
     * @param arguments programm arguments
     *
     * @return a future that finishes, when the process exits
     */
    fun startProcess(
        workingDirectory: File,
        executable: File,
        arguments: String
    ): ListenableFuture<*> {
        return this.threadPool.submit {
            ProcessBuilder()
                .directory(workingDirectory)
                .command(executable.absolutePath, *arguments.split(" ").toTypedArray())
                .start()
                .waitFor()
        }
    }
}