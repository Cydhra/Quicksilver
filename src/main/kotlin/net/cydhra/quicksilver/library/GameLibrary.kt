package net.cydhra.quicksilver.library

import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.builtins.set
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.quicksilver.data.pack.GameInfo
import net.cydhra.quicksilver.data.pack.GamePackDefinition
import org.apache.logging.log4j.LogManager
import java.io.File

class GameLibrary(val path: String) {

    companion object {
        private val logger = LogManager.getLogger()
        private val json = Json(JsonConfiguration.Stable)
    }

    private val libraryDirectory = File(path)
    private val configFile = File(libraryDirectory, ".library")

    private val installedGames: HashMap<String, File> = hashMapOf()
    private val gameDetails: HashMap<String, GameInfo> = hashMapOf()

    fun initLibrary() {
        if (configFile.exists()) {
            logger.info("loading library index for \"${libraryDirectory.path}\"")
            val games = json.parse(String.serializer().list, configFile.readText())
            installedGames.putAll(games.map { game ->
                val definitionFile = File(File(libraryDirectory, game), "$game.json")
                game to definitionFile
            })
        } else {
            logger.info("creating library index for \"${libraryDirectory.path}\"")
            configFile.createNewFile()
            storeState()
        }

        logger.info("loading games of \"${libraryDirectory.path}\"...")
        this.installedGames.keys.forEach { game ->
            gameDetails[game] = loadGameDefinition(game).second.info
        }
        logger.info("loaded ${installedGames.size} games")
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

        installedGames[gameDefinition.info.id] = File(gameDirectory, gameDefinition.info.id + ".json")
        storeState()
    }

    /**
     * @return true, if the specified game id is installed within this library
     */
    fun containsGame(gameId: String): Boolean {
        return this.installedGames[gameId] != null
    }

    /**
     * Load the game definition of the specified game
     *
     * @param id a game id
     *
     * @return a [Pair] of the game's directory [File] instance and the [GamePackDefinition]
     *
     * @throws IllegalArgumentException if the game does not exist in this library
     */
    fun loadGameDefinition(id: String): Pair<File, GamePackDefinition> {
        val definitionFile = this.installedGames[id] ?: throw IllegalArgumentException("unknown game id")
        val definition = json.parse(GamePackDefinition.serializer(), definitionFile.readText())
        return Pair(File(definitionFile.parent), definition)
    }

    private fun storeState() {
        configFile.writeText(
            Json(JsonConfiguration.Stable).stringify(
                String.serializer().set,
                this.installedGames.keys
            )
        )
    }

    /**
     * List the [GameInfo] instances of all installed games.
     */
    fun listGames(): List<GameInfo> {
        return this.gameDetails.values.toList()
    }
}