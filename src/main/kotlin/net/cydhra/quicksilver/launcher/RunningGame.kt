package net.cydhra.quicksilver.launcher

import net.cydhra.quicksilver.data.pack.GamePackDefinition
import java.util.concurrent.Future

/**
 * A wrapper for a future that waits for a game to end. This stored in a list to keep track of running games.
 *
 * @param game the definition of the game that is running
 * @param future a future that waits for the game to end. It returns the game's exit code
 */
class RunningGame(game: GamePackDefinition, future: Future<Int>)