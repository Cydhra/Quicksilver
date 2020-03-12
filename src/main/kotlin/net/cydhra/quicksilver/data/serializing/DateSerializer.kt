package net.cydhra.quicksilver.data.serializing

import kotlinx.serialization.*
import java.time.ZonedDateTime

/**
 * Custom kotlinx (de-)serializer for ZonedDateTime instances.
 */
@Serializer(forClass = ZonedDateTime::class)
object DateSerializer : KSerializer<ZonedDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveDescriptor("timestamp", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return ZonedDateTime.parse(decoder.decodeString())
    }
}