package net.cydhra.quicksilver.library

import kotlinx.serialization.json.Json
import net.cydhra.quicksilver.data.pack.ExecutionRule
import net.cydhra.quicksilver.data.pack.GameInfo
import net.cydhra.quicksilver.data.pack.GamePackDefinition
import net.cydhra.quicksilver.data.pack.InstallationRule
import net.cydhra.quicksilver.data.pack.installation.InstallationStrategy
import org.junit.jupiter.api.Test
import java.io.File
import java.time.ZonedDateTime

internal class GamePackDeserializerTest {

    @Test
    fun unpack() {
        val definition = GamePackDefinition(
            info = GameInfo(
                id = "relateddesigns.anno1701.2006",
                display = "Anno 1701",
                splash = "example_splash.png",
                studio = "Related Designs",
                publisher = "Koch Media/Aspyr Media",
                release = ZonedDateTime.parse("2006-10-26T00:00+01:00[Europe/London]"),
                version = "1.04",
                license = "proprietary"
            ),
            installation = InstallationRule(
                listOf(
                    InstallationStrategy.ExecuteStrategy(
                        elevated = true,
                        path = "DirectX/DXSETUP.exe",
                        arguments = "/silent"
                    ),
                    InstallationStrategy.RegistryStrategy(
                        file = "version.reg"
                    )
                )
            ),
            execution = ExecutionRule(
                prerequisites = emptyList(),
                workingDirectory = "Anno 1701",
                path = "Anno1701.exe"
            )
        )

        val json = Json(kotlinx.serialization.json.JsonConfiguration.Stable.copy(prettyPrint = true))
            .stringify(GamePackDefinition.serializer(), definition)
        File(definition.info.id).apply { createNewFile() }.writeText(json)
        val packFile = File("anno1701.game").apply { createNewFile() }

        GamePackSerializer(
            definition,
            File("C:\\Users\\Johannes\\IdeaProjects\\Quicksilver\\run\\anno1701").listFiles()!!
        )
            .pack(packFile.outputStream())
    }
}