package net.cydhra.quicksilver.library

import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.builtins.set
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.quicksilver.data.pack.GamePackDefinition
import net.cydhra.quicksilver.environment.Environment
import java.io.File

class GameLibrary(val path: String) {

    companion object {
        private val json = Json(JsonConfiguration.Stable)
    }

    private val libraryDirectory = File(path)
    private val configFile = File(libraryDirectory, ".library")

    private val installedGames: HashMap<String, GamePackDefinition> = hashMapOf()

    fun initLibrary() {
        if (configFile.exists()) {
            val games = json.parse(String.serializer().list, configFile.readText())
            installedGames.putAll(games.map { game ->
                val definitionFile = File(File(libraryDirectory, game), "$game.json")
                val definition = json.parse(GamePackDefinition.serializer(), definitionFile.readText())
                game to definition
            })
        } else {
            configFile.createNewFile()
            storeState()
        }
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
        storeState()
    }

    fun startGame(id: String) {
        val definition = this.installedGames[id] ?: throw IllegalArgumentException("unknown game id")
        val gameDirectory = File(this.libraryDirectory, definition.info.id)
        val workingDirectory = File(gameDirectory, definition.execution.workingDirectory)
        val executable = File(gameDirectory, definition.execution.path)

        definition.execution.prerequisites.forEach { step ->
            // TODO load execution pre requisites
        }

        Environment.startProcess(workingDirectory, executable, definition.execution.arguments, false)

        definition.execution.prerequisites.forEach { step ->
            // TODO unload execution pre requisites
        }
    }

    private fun storeState() {
        configFile.writeText(
            Json(JsonConfiguration.Stable).stringify(
                String.serializer().set,
                this.installedGames.keys
            )
        )
    }
}