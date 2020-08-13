package net.cydhra.quicksilver.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.util.pipeline.PipelineInterceptor
import net.cydhra.quicksilver.data.pack.GameInfo
import net.cydhra.quicksilver.launcher.QuicksilverLauncher

val gameInfoInterceptor: PipelineInterceptor<Unit, ApplicationCall> = interceptor@{
    val gameId = call.request.queryParameters["id"]

    if (gameId == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@interceptor
    }

    val gameInfo = QuicksilverLauncher.getGameInfo(gameId)
    if (gameInfo == null) {
        call.respond(HttpStatusCode.NotFound)
    } else {
        call.respondTextWriter(ContentType.Application.Json, HttpStatusCode.OK) {
            this.write(
                serverJsonSerializer.stringify(GameInfo.serializer(), gameInfo)
            )
        }
    }
}