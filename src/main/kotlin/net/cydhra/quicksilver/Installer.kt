package net.cydhra.quicksilver

import com.labymedia.ultralight.UltralightJava
import java.io.File
import java.nio.file.Paths

/**
 * A utility that correctly sets up all resources for Quicksilver
 */
class Installer() {

    /**
     * Prepare the directories and resources that are used by Quicksilver
     */
    fun installApplication() {
        extractResource("web/index.html", "web")
        extractResource("web/frontend.js", "web")

        UltralightJava.extractNativeLibrary(Paths.get("."))

        // TODO extract the SDK libraries
    }

    /**
     * Load all native libraries that are required during runtime
     */
    fun loadApplication() {
        UltralightJava.load(Paths.get("."));
    }

    /**
     * Extract a resource into the target folder if its not present yet
     */
    private fun extractResource(path: String, targetFolderPath: String) {
        val targetFolder = File(targetFolderPath)
        val outputFile = File(targetFolder, File(path).name)

        targetFolder.mkdirs()
        outputFile.createNewFile()
        outputFile.writeBytes(javaClass.classLoader.getResourceAsStream(path)!!.readBytes())
    }
}