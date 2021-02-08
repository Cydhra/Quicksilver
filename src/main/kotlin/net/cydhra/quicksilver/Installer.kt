package net.cydhra.quicksilver

import com.labymedia.ultralight.UltralightJava
import java.nio.file.Path

/**
 * A utility that correctly sets up all resources for Quicksilver
 */
class Installer(private val nativesDir: Path) {

    /**
     * Prepare the directories and resources that are used by Quicksilver
     */
    fun installApplication() {
        UltralightJava.extractNativeLibrary(nativesDir)

        // TODO extract the SDK libraries
    }

    /**
     * Load all native libraries that are required during runtime
     */
    fun loadApplication() {
        UltralightJava.load(nativesDir);
    }
}