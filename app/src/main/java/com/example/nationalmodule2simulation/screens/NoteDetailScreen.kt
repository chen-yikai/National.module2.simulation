package com.example.nationalmodule2simulation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nationalmodule2simulation.LocalNavController
import com.example.nationalmodule2simulation.LocalNoteData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen() {
    val nav = LocalNavController.current
    val noteData = LocalNoteData.current

    val tabs = listOf("文字", "手寫")
    var currentTab by remember { mutableIntStateOf(0) }

    var showRenameDialog by remember { mutableStateOf(false) }

    val note by remember { mutableStateOf(noteData.notes.find { it.id == nav.id }!!) }

    if (showRenameDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false }, title = { Text("重新命名此筆記") },
            confirmButton = {
                Button(onClick = {
                    note.name = name
                    val index = noteData.notes.indexOfFirst { it.id == nav.id }
                    noteData.notes[index] = note
                    noteData.write()
                    showRenameDialog = false
                }) { Text("重新命名") }
            },
            text = { OutlinedTextField(name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth()) })
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(note.name) },
                actions = {
                    IconButton(onClick = {
                        noteData.notes.removeIf { it.id == nav.id }
                        noteData.write()
                        nav.pop()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        showRenameDialog = true
                    }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { nav.pop() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = currentTab) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        selected = index == currentTab,
                        onClick = { currentTab = index },
                        content = { Text(item, modifier = Modifier.padding(vertical = 10.dp)) })
                }
            }
            when (currentTab) {
                0 -> TextArea()
                1 -> HandwritingArea()
            }
        }
    }
}