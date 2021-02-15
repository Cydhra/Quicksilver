package net.cydhra.quicksilver.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
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
                Json.encodeToString(GameInfo.serializer(), gameInfo)
            )
        }
    }
}