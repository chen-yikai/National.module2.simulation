package com.example.nationalmodule2simulation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavController : ViewModel() {
    private val initScreen = Screen.NoteList
    var currentNav by mutableStateOf(initScreen)
    var navStack = mutableStateListOf<Screen>()

    var id by mutableStateOf("")

    fun reset() {
        id = ""
    }

    init {
        navStack.add(initScreen)
    }

    fun navTo(screen: Screen) {
        navStack.add(currentNav)
        currentNav = screen
    }

    fun pop() {
        if (navStack.size > 1) {
            navStack.removeLast()
            currentNav = navStack.last()
        }
    }
}

enum class Screen {
    NoteList, NoteDetail
}