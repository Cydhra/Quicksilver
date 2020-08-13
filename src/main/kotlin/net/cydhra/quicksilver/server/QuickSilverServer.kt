package net.cydhra.quicksilver.server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.serialization.json
import io.ktor.websocket.WebSockets
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
    install(Routing) {
        post("game/install", installGameInterceptor)
        post("game/start", startGameInterceptor)
        get("game/list", listGameInterceptor)
        get("game/info", {})

        get("library/list", {})
        get("library/add", {})
    }
}