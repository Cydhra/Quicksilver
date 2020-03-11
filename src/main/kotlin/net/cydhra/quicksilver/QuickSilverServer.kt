package net.cydhra.quicksilver

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get

/**
 * path where all game related endpoints are
 */
const val GAME_PATH = "/game"

/**
 * endpoint to start a game
 */
const val START_GAME_ENDPOINT = "$GAME_PATH/start"


@Suppress("unused")
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get(START_GAME_ENDPOINT) interceptor@{
            val articleUrl = call.request.queryParameters["id"]

            if (articleUrl == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@interceptor
            }
        }
    }
}