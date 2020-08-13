package net.cydhra.quicksilver.server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

val serverJsonSerializer = Json(JsonConfiguration.Stable)

@Suppress("unused")
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }
    install(Routing) {
        post("game/install", installGameInterceptor)
        post("game/start", startGameInterceptor)
        get("game/list", listGameInterceptor)
        get("game/info", {})

        get("library/list", {})
        get("library/add", {})
    }
}

//private fun handleFrame(frame: Frame.Text) {
//    val frameContent = String(frame.data)
//}

fun main() {
//    val file = File("C:\\Users\\Johannes\\IdeaProjects\\Quicksilver\\run\\railroad\\poptop.railroad2.1998.json")
//    val def = Json(JsonConfiguration.Stable).parse(GamePackDefinition.serializer(), file.readText())
//    val out = File(file.parentFile.parentFile, "railroad2.game").outputStream()
//
//    GamePackSerializer(def, arrayOf(File(file.parentFile, "RailRoad Tycoon II"), File(file.parentFile, "railroad.iso"))).pack(out)
//    out.close()
}