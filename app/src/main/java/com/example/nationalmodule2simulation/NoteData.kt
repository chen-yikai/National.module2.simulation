package com.example.nationalmodule2simulation

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.nationalmodule2simulation.screens.CharData
import com.example.nationalmodule2simulation.screens.CustomStroke
import com.example.nationalmodule2simulation.screens.SimpleTextStyle
import com.example.nationalmodule2simulation.serializers.CharDataSerializer
import com.example.nationalmodule2simulation.serializers.CustomStrokeSerializer
import com.example.nationalmodule2simulation.serializers.SimpleTextStyleSerializer
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

data class Note(
    val id: String,
    val createAt: Long,
    val updateAt: Long,
    var name: String,
    val text: List<CharData>,
    val write: List<CustomStroke>
)

class NoteData(private val context: Application) : AndroidViewModel(context) {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(CharData::class.java, CharDataSerializer())
        .registerTypeAdapter(SimpleTextStyle::class.java, SimpleTextStyleSerializer())
        .registerTypeAdapter(CustomStroke::class.java, CustomStrokeSerializer())
        .create()
    private val file = File(context.getExternalFilesDir("data"), "data.json")
    var notes = mutableStateListOf<Note>()

    init {
        get()
    }

    fun create(): String {
        val id = UUID.randomUUID().toString()
        notes.add(
            Note(
                id,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                "未命名的筆記", emptyList(), emptyList()
            )
        )
        write()
        return id
    }

    fun update(id: String) {
        val index = getIndex(id)
        notes[index] = notes[index].copy(updateAt = System.currentTimeMillis())
    }

    fun write() {
        file.writeText(gson.toJson(notes))
    }

    fun get(id: String): Note? {
        return notes.find { it.id == id }
    }

    fun getIndex(id: String): Int {
        return notes.indexOfFirst { it.id == id }
    }

    fun get() {
        if (!file.exists()) {
            file.writeText("[]")
        }
        val jsonText = file.readText()
        notes.clear()
        try {
            notes.addAll(gson.fromJson(jsonText, object : TypeToken<List<Note>>() {}.type))
        } catch (e: Exception) {
            file.writeText("[]")
        }
    }
}