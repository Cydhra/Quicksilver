package net.cydhra.quicksilver.data.pack

import com.profesorfalken.jpowershell.PowerShell
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
sealed class ExecutionPrerequisite {

    /**
     * Mount an iso image as a virtual disk before starting a game. Used for old games relying on physical copies to be
     * present
     *
     * @param imagePath relative path to the disk image
     */
    @Serializable
    class MountIsoPrerequisite(val imagePath: String) : ExecutionPrerequisite() {

        override fun enable(basePath: File) {
            val path = File(basePath, imagePath).absolutePath
            PowerShell.executeSingleCommand("Mount-DiskImage \"$path\"")
        }

        override fun disable(basePath: File) {
            val path = File(basePath, imagePath).absolutePath
            PowerShell.executeSingleCommand("Dismount-DiskImage \"$path\"")
        }
    }

    /**
     * Enable the prerequisite before the execution of a game
     *
     * @param basePath the base directory of the game where relative paths are originated
     */
    abstract fun enable(basePath: File)

    /**
     * Disable the prerequisite after a game has finished
     *
     * @param basePath the base directory of the game where relative paths are originated
     */
    abstract fun disable(basePath: File)
}