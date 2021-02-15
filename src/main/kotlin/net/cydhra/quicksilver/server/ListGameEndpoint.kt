package net.cydhra.quicksilver.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.cydhra.quicksilver.data.pack.GameInfo
import net.cydhra.quicksilver.launcher.QuicksilverLauncher

@Serializable
data class ListGameResponse(val games: List<GameInfo>)

val listGameInterceptor: PipelineInterceptor<Unit, ApplicationCall> = interceptor@{
    call.respondTextWriter(ContentType.Application.Json, HttpStatusCode.OK) {
        this.write(
            Json.encodeToString(
                ListGameResponse.serializer(),
                ListGameResponse(QuicksilverLauncher.listGames())
            )
        )
    }
}