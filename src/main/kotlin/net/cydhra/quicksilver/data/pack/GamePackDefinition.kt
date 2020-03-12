package net.cydhra.quicksilver.data.pack

import kotlinx.serialization.Serializable

/**
 * The meta data file of a game. This class is a complete model of the definition key inside the json of definition
 * files version 1.
 */
@Serializable
class GamePackDefinition(val info: GameInfo, val installation: InstallationRule, val execution: ExecutionRule)