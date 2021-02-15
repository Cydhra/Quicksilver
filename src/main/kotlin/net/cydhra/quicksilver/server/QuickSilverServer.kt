package net.cydhra.quicksilver.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*

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
        get("game/info", gameInfoInterceptor)

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