package net.cydhra.quicksilver

import com.labymedia.ultralight.UltralightJava
import java.nio.file.Paths

/**
 * A utility that correctly sets up all resources for Quicksilver
 */
class Installer() {

    /**
     * Prepare the directories and resources that are used by Quicksilver
     */
    fun installApplication() {
        UltralightJava.extractNativeLibrary(Paths.get("."))

        // TODO extract the SDK libraries
    }

    /**
     * Load all native libraries that are required during runtime
     */
    fun loadApplication() {
        UltralightJava.load(Paths.get("."));
    }
}