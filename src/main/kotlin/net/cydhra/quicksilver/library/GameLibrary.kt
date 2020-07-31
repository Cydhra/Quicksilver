package net.cydhra.quicksilver.library

import net.cydhra.quicksilver.data.pack.GamePackDefinition
import java.io.File

class GameLibrary(val path: String) {

    private val libraryDirectory = File(path)

    private val installedGames: HashMap<String, GamePackDefinition> = hashMapOf()

    fun initLibrary() {

    }

    /**
     * Install a game into the library
     */
    fun installGamePack(packFile: File) {
        val gameDefinition = GamePackDeserializer(this.libraryDirectory, packFile).unpack()
        val gameDirectory = File(this.libraryDirectory, gameDefinition.info.id)

        gameDefinition.installation.steps.forEach { step ->
            step.install(gameDirectory)
        }

        installedGames[gameDefinition.info.id] = gameDefinition
    }

    fun startGame(id: String) {

    }
}