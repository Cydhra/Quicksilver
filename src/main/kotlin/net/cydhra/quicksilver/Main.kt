package net.cydhra.quicksilver

import net.cydhra.quicksilver.ui.UIController

fun main() {
    // install application
    val installer = Installer()
    installer.installApplication()
    installer.loadApplication()

    UIController.setupWindow()
}