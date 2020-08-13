package net.cydhra.quicksilver.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondTextWriter
import io.ktor.util.pipeline.PipelineInterceptor
import kotlinx.serialization.builtins.list
import net.cydhra.quicksilver.data.pack.GameInfo
import net.cydhra.quicksilver.launcher.QuicksilverLauncher

val listGameInterceptor: PipelineInterceptor<Unit, ApplicationCall> = interceptor@{
    call.respondTextWriter(ContentType.Application.Json, HttpStatusCode.OK) {
        this.write(serverJsonSerializer.stringify(GameInfo.serializer().list, QuicksilverLauncher.listGames()))
    }
}