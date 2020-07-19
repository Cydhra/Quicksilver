package net.cydhra.quicksilver.library

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.quicksilver.data.pack.GamePackDefinition
import java.io.File

class GamePack(val definitionFile: File) {

    val gamePackDefinition: GamePackDefinition by lazy {
        Json(JsonConfiguration.Stable).parse(GamePackDefinition.serializer(), definitionFile.readText())
    }

    fun install() {
        gamePackDefinition.installation.steps.forEach { step ->
            Installer.execute(step)
        }
    }
}