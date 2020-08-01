package net.cydhra.quicksilver.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineInterceptor
import net.cydhra.quicksilver.launcher.QuicksilverLauncher

val installGameInterceptor: PipelineInterceptor<Unit, ApplicationCall> = interceptor@{
    val url = call.request.queryParameters["url"]
    val library = call.request.queryParameters["library"]?.toInt()

    if (url == null) {
        call.respond(HttpStatusCode.BadRequest)
        return@interceptor
    }

    QuicksilverLauncher.installGame(url, library)
}