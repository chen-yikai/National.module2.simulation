package com.example.nationalmodule2simulation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider
import com.example.nationalmodule2simulation.screens.NoteDetailScreen
import com.example.nationalmodule2simulation.screens.NoteListScreen
import com.example.nationalmodule2simulation.ui.theme.Nationalmodule2simulationTheme

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
