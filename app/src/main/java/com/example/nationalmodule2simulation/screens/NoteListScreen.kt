package com.example.nationalmodule2simulation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nationalmodule2simulation.LocalNavController
import com.example.nationalmodule2simulation.LocalNoteData
import com.example.nationalmodule2simulation.R
import com.example.nationalmodule2simulation.Screen
import java.text.SimpleDateFormat
import kotlin.math.exp

@Composable
fun NoteListScreen() {
    val nav = LocalNavController.current
    var note = LocalNoteData.current
    var search by remember { mutableStateOf("") }
    var selectedDropdown by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedDropdown) {
        when (selectedDropdown) {
            0 -> note.notes.sortByDescending { it.updateAt }
            1 -> note.notes.sortBy { it.updateAt }
        }
    }

    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text("新筆記") },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            onClick = {
                nav.id = note.create()
                nav.navTo(Screen.NoteDetail)
            })
    }) { innerPadding ->
        LazyColumn(contentPadding = innerPadding, modifier = Modifier.padding(horizontal = 20.dp)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MAD 記事本", fontSize = 30.sp, fontWeight = FontWeight.Bold)

                    var showDropdown by remember { mutableStateOf(false) }
                    var dropdownItems = listOf("較新", "較舊")
                    Box {
                        IconButton(onClick = {
                            showDropdown = !showDropdown
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.sort),
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            dropdownItems.forEachIndexed { index, item ->
                                DropdownMenuItem(text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item)
                                        if (index == selectedDropdown) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                }, onClick = {
                                    selectedDropdown = index
                                })
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    placeholder = { Text("搜尋筆記") },
                    trailingIcon = {
                        IconButton(onClick = {
                            search = ""
                        }, enabled = search.isNotEmpty()) {
                            Icon(
                                if (search.isNotEmpty()) Icons.Default.Clear else Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    }
                )
                Spacer(Modifier.height(10.dp))
            }
            items(note.notes) {
                if (it.name.contains(search))
                    Card(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(),
                        border = CardDefaults.outlinedCardBorder(), onClick = {
                            nav.id = it.id
                            note.update(it.id)
                            nav.navTo(Screen.NoteDetail)
                        }
                    ) {
                        Row(modifier = Modifier.padding(15.dp)) {
                            Column {
                                Text(it.name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                                Text(SimpleDateFormat("yyyy年MM月dd日 hh:mm a").format(it.updateAt))
                            }
                        }
                    }
            }
        }
    }
}