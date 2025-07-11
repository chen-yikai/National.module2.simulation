package com.example.nationalmodule2simulation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider
import com.example.nationalmodule2simulation.screens.NoteDetailScreen
import com.example.nationalmodule2simulation.screens.NoteListScreen
import com.example.nationalmodule2simulation.ui.theme.Nationalmodule2simulationTheme
import kotlinx.coroutines.delay

val LocalNavController = compositionLocalOf<NavController> { error("") }
val LocalNoteData = compositionLocalOf<NoteData> { error("") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nationalmodule2simulationTheme {
                val nav = ViewModelProvider(this)[NavController::class.java]
                val noteData = ViewModelProvider(this)[NoteData::class.java]

                BackHandler {
                    if (nav.navStack.size > 1) {
                        nav.pop()
                    } else {
                        finish()
                    }
                }
                CompositionLocalProvider(
                    LocalNavController provides nav,
                    LocalNoteData provides noteData
                ) {
                    Surface {
                        LaunchedEffect(Unit) {
                            if(intent.getStringExtra("note_action") == "recent"){
                                Log.e("action intent", "recent")
                                noteData.notes.sortByDescending { it.updateAt }
                                val id = noteData.notes.first().id
                                nav.id = id
                                nav.navTo(Screen.NoteDetail)
                            }else if(intent.getStringExtra("note_action") =="new") {

                                Log.e("action intent", "new")
                                nav.id = noteData.create()
                                nav.navTo(Screen.NoteDetail)
                            }
                        }
                        when (nav.currentNav) {
                            Screen.NoteList -> NoteListScreen()
                            Screen.NoteDetail -> NoteDetailScreen()
                        }
                    }
                }
            }
        }
    }
}
