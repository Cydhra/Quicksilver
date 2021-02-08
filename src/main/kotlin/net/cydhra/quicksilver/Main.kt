package net.cydhra.quicksilver

import net.cydhra.quicksilver.ui.UIController
import java.io.File

fun main() {
    // find path for natives:
    val nativesDir = System.getProperty("java.library.path")?.let { File(it).toPath() } ?: File(".").toPath()

    // install application
    val installer = Installer(nativesDir)
    installer.installApplication()
    installer.loadApplication()

    UIController.setupWindow()

    while (true) {}
}