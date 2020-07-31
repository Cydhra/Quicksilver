package net.cydhra.quicksilver.launcher

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.quicksilver.library.GameLibrary
import java.io.File

object QuicksilverLauncher {

    /**
     * Quicksilver configuration file
     */
    private val configFile = File(".config")

    /**
     * Quicksilver configuration
     */
    private lateinit var configuration: Configuration

    /**
     * A list of game libraries.
     */
    private val libraries = mutableListOf<GameLibrary>()

    /**
     * All games currently running
     */
    private val runningGames = mutableListOf<RunningGame>()

    init {
        this.loadConfig()
        this.loadLibraries()
    }

    /**
     * Run a game with the specified id from any available library.
     *
     * @param gameId the unique identifier for the game
     */
    fun runGame(gameId: String) {
        TODO("not implemented")
    }

    /**
     * Parse the configuration from the config file
     */
    private fun loadConfig() {
        if (configFile.exists()) {
            this.configuration = Json(JsonConfiguration.Stable).parse(Configuration.serializer(), configFile.readText())
        } else {
            val defaultLibrary = File(".libdef")
            if (!defaultLibrary.exists())
                defaultLibrary.mkdir()

            this.configuration = Configuration(
                libraries = mutableListOf(defaultLibrary.path)
            )
            this.saveConfig()
        }
    }

    /**
     * Load all libraries defined in the configuration and initialize them
     */
    private fun loadLibraries() {
        this.configuration.libraries.forEach { libPath ->
            val library = GameLibrary(libPath)
            library.initLibrary()
            this.libraries.add(library)
            println("loaded library at \"$libPath\"")
        }
    }

    /**
     * Save the configuration to the config file
     */
    private fun saveConfig() {
        if (!this.configFile.exists()) {
            this.configFile.createNewFile()
        }

        this.configFile.writeText(
            Json(JsonConfiguration.Stable).stringify(Configuration.serializer(), this.configuration)
        )
    }
}