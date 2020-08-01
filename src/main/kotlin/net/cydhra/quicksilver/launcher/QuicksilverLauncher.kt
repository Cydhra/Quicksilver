package net.cydhra.quicksilver.launcher

import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.quicksilver.environment.Environment
import net.cydhra.quicksilver.library.GameLibrary
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.concurrent.Executors

object QuicksilverLauncher {

    private val logger = LogManager.getLogger()

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

    /**
     * An executor for callbacks on futures
     */
    private val executor = Executors.newCachedThreadPool()

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
        val executingLibrary = this.libraries
            .firstOrNull { it.containsGame(gameId) }
            ?: throw IllegalStateException("a game with id \"$gameId\" is not installed")

        val (gameDirectory, definition) = executingLibrary.loadGameDefinition(gameId)
        val workingDirectory = File(gameDirectory, definition.execution.workingDirectory)
        val executable = File(gameDirectory, definition.execution.path)

        // execute all prerequisites
        // TODO this should be added to a future as well
        definition.execution.prerequisites.forEach { step ->
            // TODO load execution pre requisites
        }

        // start the game and retrieve a future that waits for it to end
        val gameProcessFuture =
            Environment.startProcess(workingDirectory, executable, definition.execution.arguments, false)

        // add cleanup callback to the future
        Futures.addCallback(gameProcessFuture, object : FutureCallback<Int> {
            override fun onSuccess(result: Int?) {
                definition.execution.prerequisites.forEach { step ->
                    // TODO unload execution pre requisites
                }

                this@QuicksilverLauncher.finishRunningGame(gameId, result!!)
            }

            override fun onFailure(t: Throwable) {
                logger.error("error while executing game $gameId", t)
            }
        }, executor)

        // add the running game future to the list
        runningGames.add(RunningGame(definition, gameProcessFuture))
    }

    private fun finishRunningGame(gameId: String, exitCode: Int) {
        synchronized(this.runningGames) {
            this.runningGames.removeIf { it.game.info.id == gameId }
            logger.info("game \"$gameId\" stopped with exit code $exitCode")
        }
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

    /**
     * Install a game from a game pack file located at the given url. It will be installed into the specified library.
     * If no library is specified, the default library (the first in the config file) is used.
     *
     * @param url where the game pack file is located
     * @param libraryIndex the index of the library into which the file is installed
     */
    fun installGame(url: String, libraryIndex: Int?) {
        val gameLibrary = if (libraryIndex != null) {
            this.libraries[libraryIndex]
        } else this.libraries.first()
    }
}