package net.cydhra.quicksilver

import net.cydhra.quicksilver.launcher.QuicksilverLauncher
import net.cydhra.quicksilver.ui.UIController

fun main() {
    // load game library
    QuicksilverLauncher.init()

    // install application
    val installer = Installer()
    installer.installApplication()
    installer.loadApplication()

    UIController.setupWindow()
}