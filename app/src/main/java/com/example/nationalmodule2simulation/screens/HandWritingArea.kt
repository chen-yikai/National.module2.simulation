package com.example.nationalmodule2simulation.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.nationalmodule2simulation.LocalNavController
import com.example.nationalmodule2simulation.LocalNoteData
import com.example.nationalmodule2simulation.R

data class CustomStroke(
    val offsets: List<Offset>, val width: Float, val color: Color
)

@Composable
fun HandwritingArea() {
    var paths = remember { mutableStateListOf<CustomStroke>() }
    var currentPoint = remember { mutableStateListOf<Offset>() }

    var strokeWidth by remember { mutableFloatStateOf(10f) }
    var showStrokePicker by remember { mutableStateOf(false) }

    var color by remember { mutableStateOf(Color.Black) }
    var showColorPicker by remember { mutableStateOf(false) }

    var activeEraser by remember { mutableStateOf(false) }
    val noteData = LocalNoteData.current
    val nav = LocalNavController.current

    LaunchedEffect(Unit) {
        paths.addAll(noteData.notes[noteData.getIndex(nav.id)].write)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val noHand = event.changes.all { !it.pressed }

                        if (activeEraser) {
                            paths.removeAll { stroke ->
                                stroke.offsets.any { point ->
                                    (point - event.changes[0].position).getDistance() < 40f
                                }
                            }
                        } else {
                            when {
                                noHand -> {
                                    if (currentPoint.isNotEmpty()) {
                                        paths.add(
                                            CustomStroke(
                                                currentPoint.toList(), strokeWidth, color
                                            )
                                        )
                                        currentPoint.clear()
                                    }
                                }

                                event.changes[0].pressed -> {
                                    currentPoint.add(event.changes[0].position)
                                }
                            }
                        }

                        noteData.notes[noteData.getIndex(nav.id)] =
                            noteData.notes[noteData.getIndex(nav.id)].copy(write = paths)
                        noteData.write()
                    }
                }
            }) {
            paths.forEach {
                drawPath(
                    Path().apply {
                        it.offsets.forEachIndexed { index, item ->
                            if (index == 0) moveTo(item.x, item.y)
                            lineTo(item.x, item.y)
                        }
                    }, color = it.color, style = Stroke(cap = StrokeCap.Round, width = it.width)
                )
            }
            if (currentPoint.isNotEmpty()) {
                drawPath(
                    Path().apply {
                        currentPoint.forEachIndexed { index, item ->
                            if (index == 0) moveTo(item.x, item.y)
                            lineTo(item.x, item.y)
                        }
                    }, color = color, style = Stroke(cap = StrokeCap.Round, width = strokeWidth)
                )
            }
        }
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            AnimatedVisibility(showStrokePicker) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .imePadding(), elevation = CardDefaults.elevatedCardElevation(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                    ) {
                        Slider(
                            strokeWidth,
                            onValueChange = { strokeWidth = it },
                            valueRange = 5f..30f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    }
                }
            }
            AnimatedVisibility(showColorPicker) {
                val colors =
                    listOf(Color.Black, Color(0xffD32FFF), Color(0xff60FFAC), Color(0xffFFD66B))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .imePadding(), elevation = CardDefaults.elevatedCardElevation(5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        colors.forEach {
                            Box(modifier = Modifier
                                .clip(CircleShape)
                                .background(it)
                                .size(50.dp)
                                .clickable {
                                    color = it
                                }) {
                                if (it == color) Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clip(
                                            CircleShape
                                        )
                                        .background(Color.White)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .imePadding(),
                elevation = CardDefaults.elevatedCardElevation(5.dp)
            ) {
                Row(
                    modifier = Modifier.padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolIconButton(R.drawable.color, showColorPicker) {
                        showStrokePicker = false
                        showColorPicker = !showColorPicker
                    }
                    ToolIconButton(R.drawable.stroke, showStrokePicker) {
                        showColorPicker = false
                        showStrokePicker = !showStrokePicker
                    }
                    ToolIconButton(R.drawable.eraser, activeEraser) { activeEraser = !activeEraser }
                }
            }
        }
    }
}
