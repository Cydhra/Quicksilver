package net.cydhra.quicksilver.data.pack

import kotlinx.serialization.Serializable
import net.cydhra.quicksilver.data.serializing.DateSerializer
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Serializable
class GameInfo(
    val id: String,
    val display: String,
    val splash: String = "",
    val studio: String = "",
    val publisher: String = "",
    val release: @Serializable(with = DateSerializer::class) ZonedDateTime = ZonedDateTime.ofInstant(
        Instant.MIN,
        ZoneId.of("Europe/London")
    ),
    val version: String = ""
)