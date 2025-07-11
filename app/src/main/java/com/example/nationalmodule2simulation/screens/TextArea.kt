package com.example.nationalmodule2simulation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextDelegate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.unpackInt1
import com.example.nationalmodule2simulation.LocalNavController
import com.example.nationalmodule2simulation.LocalNoteData
import com.example.nationalmodule2simulation.R

data class CharData(
    val char: Char,
    val style: SimpleTextStyle
)

data class SimpleTextStyle(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isColor: Boolean = false,
    val fontSize: Float = 20f
)

@Composable
fun TextArea() {
    var text by remember { mutableStateOf(TextFieldValue(AnnotatedString(""))) }
    val textList = remember { mutableStateListOf<CharData>() }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isColor by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }

    val noteData = LocalNoteData.current
    val nav = LocalNavController.current

    val index = noteData.getIndex(nav.id)

    var note by remember { mutableStateOf(noteData.notes[index]) }

    fun getSimpleStyle() = SimpleTextStyle(
        isBold = isBold,
        isItalic = isItalic,
        isUnderline = isUnderline,
        isColor = isColor,
        fontSize = 20f
    )

    LaunchedEffect(Unit) {
        textList.addAll(note.text)
        text = text.copy(annotatedString = buildAnnotatedString {
            textList.forEachIndexed { index, data ->
                append(data.char)
                addStyle(
                    SpanStyle(
                        fontWeight = if (data.style.isBold) FontWeight.Bold else null,
                        color = if (data.style.isColor) Color.Red else Color.Unspecified,
                        fontSize = 20.sp,
                        fontStyle = if (data.style.isItalic) FontStyle.Italic else null,
                        textDecoration = if (data.style.isUnderline) TextDecoration.Underline else null
                    ), index, index + 1
                )
            }
        })
    }

    Column {
        BasicTextField(
            textStyle = TextStyle(fontSize = 20.sp),
            value = text, onValueChange = { newValue ->
                val newText = newValue.annotatedString
                val oldText = text.annotatedString
                val cursor = newValue.selection

                if (cursor.start == cursor.end && newText.length > oldText.length) {
                    if (cursor.start < newText.length) {
                        textList.add(
                            cursor.start - 1,
                            CharData(newText[cursor.start - 1], getSimpleStyle())
                        )
                    } else {
                        textList.add(CharData(newText.last(), getSimpleStyle()))
                    }
                }

                if (cursor.start == cursor.end && newText.length < oldText.length) {
                    textList.removeAt(cursor.start)
                }

                text = newValue.copy(annotatedString = buildAnnotatedString {
                    textList.forEachIndexed { index, data ->
                        append(data.char)
                        addStyle(
                            SpanStyle(
                                fontWeight = if (data.style.isBold) FontWeight.Bold else null,
                                fontSize = data.style.fontSize.sp,
                                color = if (data.style.isColor) Color.Red else Color.Unspecified,
                                fontStyle = if (data.style.isItalic) FontStyle.Italic else null,
                                textDecoration = if (data.style.isUnderline) TextDecoration.Underline else null
                            ), index, index + 1
                        )
                    }
                    noteData.notes[index] = note.copy(text = textList)
                    noteData.write()
                })
            }, modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .weight(1f)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .imePadding(), elevation = CardDefaults.elevatedCardElevation(5.dp)
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ToolIconButton(R.drawable.bold, isBold) {
                    isBold = !isBold
                }
                ToolIconButton(R.drawable.italic, isItalic) {
                    isItalic = !isItalic
                }
                ToolIconButton(R.drawable.text_color, isColor) {
                    isColor = !isColor
                }
                ToolIconButton(R.drawable.underline, isUnderline) {
                    isUnderline = !isUnderline
                }
            }
        }
    }
}

@Composable
fun ToolIconButton(icon: Int, active: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = if (active) MaterialTheme.colorScheme.background else Color.Unspecified)
    ) {
        Icon(painter = painterResource(icon), contentDescription = null)
    }
}