package com.example.nationalmodule2simulation.serializers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.nationalmodule2simulation.screens.CharData
import com.example.nationalmodule2simulation.screens.CustomStroke
import com.example.nationalmodule2simulation.screens.SimpleTextStyle
import com.google.gson.*
import java.lang.reflect.Type

class CharDataSerializer : JsonSerializer<CharData>, JsonDeserializer<CharData> {
    override fun serialize(src: CharData, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("char", src.char.toString())
        jsonObject.add("style", context.serialize(src.style))
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CharData {
        val jsonObject = json.asJsonObject
        val char = jsonObject.get("char").asString.first()
        val style = context.deserialize<SimpleTextStyle>(jsonObject.get("style"), SimpleTextStyle::class.java)
        return CharData(char, style)
    }
}

class SimpleTextStyleSerializer : JsonSerializer<SimpleTextStyle>, JsonDeserializer<SimpleTextStyle> {
    override fun serialize(src: SimpleTextStyle, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("isBold", src.isBold)
        jsonObject.addProperty("isItalic", src.isItalic)
        jsonObject.addProperty("isUnderline", src.isUnderline)
        jsonObject.addProperty("isColor", src.isColor)
        jsonObject.addProperty("fontSize", src.fontSize)
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SimpleTextStyle {
        val jsonObject = json.asJsonObject
        return SimpleTextStyle(
            isBold = jsonObject.get("isBold").asBoolean,
            isItalic = jsonObject.get("isItalic").asBoolean,
            isUnderline = jsonObject.get("isUnderline").asBoolean,
            isColor = jsonObject.get("isColor").asBoolean,
            fontSize = jsonObject.get("fontSize").asFloat
        )
    }
}

class CustomStrokeSerializer : JsonSerializer<CustomStroke>, JsonDeserializer<CustomStroke> {
    override fun serialize(src: CustomStroke, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        val offsetsArray = JsonArray()
        src.offsets.forEach { offset ->
            val offsetObject = JsonObject()
            offsetObject.addProperty("x", offset.x)
            offsetObject.addProperty("y", offset.y)
            offsetsArray.add(offsetObject)
        }
        jsonObject.add("offsets", offsetsArray)
        jsonObject.addProperty("width", src.width)
        jsonObject.addProperty("color", src.color.value.toLong())
        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CustomStroke {
        val jsonObject = json.asJsonObject
        val offsetsArray = jsonObject.getAsJsonArray("offsets")
        val offsets = offsetsArray.map {
            val offsetObject = it.asJsonObject
            Offset(offsetObject.get("x").asFloat, offsetObject.get("y").asFloat)
        }
        val width = jsonObject.get("width").asFloat
        val color = Color(jsonObject.get("color").asLong.toULong())
        return CustomStroke(offsets, width, color)
    }
}

