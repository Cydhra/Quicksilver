package net.cydhra.quicksilver.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineInterceptor
import net.cydhra.quicksilver.launcher.QuicksilverLauncher

val startGameInterceptor: PipelineInterceptor<Unit, ApplicationCall> = interceptor@{
    val gameId = call.request.queryParameters["id"]

    if (gameId == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@interceptor
    }

    QuicksilverLauncher.runGame(gameId)
    call.respond(HttpStatusCode.OK)
}