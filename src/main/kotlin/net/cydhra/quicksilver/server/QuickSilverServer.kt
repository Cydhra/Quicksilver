package net.cydhra.quicksilver.server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.serialization.json

@Suppress("unused")
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    install(Routing) {
        get("game/install", installGameInterceptor)
        get("game/start", startGameInterceptor)
        get("game/list", {})
        get("game/info", {})

        get("library/list", {})
        get("library/add", {})
        get("library/start", {})
    }
}