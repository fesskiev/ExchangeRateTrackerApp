package com.example.exchangeratetrackerapp.core.data.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable(with = CurrencyResponseSerializer::class)
data class CurrencyResponse(
    val currencies: Map<String, String>
)

object CurrencyResponseSerializer : KSerializer<CurrencyResponse> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("CurrencyResponse") {
            element("currencies", MapSerializer(String.serializer(), String.serializer()).descriptor)
        }

    override fun serialize(encoder: Encoder, value: CurrencyResponse) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This serializer can only be used with JSON")

        jsonEncoder.encodeJsonElement(JsonObject(value.currencies.map {
            it.key to JsonPrimitive(it.value)
        }.toMap()))
    }

    override fun deserialize(decoder: Decoder): CurrencyResponse {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer can only be used with JSON")

        val jsonObject = jsonDecoder.decodeJsonElement() as JsonObject
        val map = jsonObject.mapValues {
            (it.value as JsonPrimitive).content
        }

        return CurrencyResponse(map)
    }
}