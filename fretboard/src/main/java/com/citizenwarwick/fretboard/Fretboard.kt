/*
 Copyright 2020 Ian Warwick

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.citizenwarwick.fretboard

import androidx.annotation.IntRange
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.Border
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.background
import androidx.ui.foundation.shape.DrawShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.SolidColor
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutGravity
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.Table
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.citizenwarwick.fretboard.Marker.FrettedNote
import com.citizenwarwick.fretboard.Marker.Mute
import com.citizenwarwick.music.PitchClass

@Composable
@Preview
fun FretboardPreview() {
    val markers = listOf(
        FrettedNote(1, 2),
        FrettedNote(2, 3),
        FrettedNote(3, 2),
        FrettedNote(4, 0),
        Mute(5),
        Mute(6)
    )
    Fretboard(0, 5, markers, scale = 2.0f)
}

@Composable
fun Fretboard(
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 24) toFret: Int = 12,
    markers: List<Marker> = listOf(),
    scale: Float = 1.5f
) {
    check(fromFret >= 0 && toFret > fromFret) {
        "Invalid fret range"
    }

    val from = if (fromFret > 0) fromFret - 1 else fromFret
    val fretRange = toFret - from

    Column {
        Container(
            modifier = background(Color.White) + Border(
                shape = RoundedCornerShape(0.dp),
                width = 1.dp,
                color = Color.Black
            ) + LayoutSize(
                width = (BASE_FRET_WIDTH * scale * fretRange).dp,
                height = (BASE_FRETBOARD_HEIGHT * scale).dp
            )
        ) {
            Stack {
                Row(modifier = LayoutWidth.Fill) {
                    for (n in from until toFret) {
                        if (n == 0) {
                            Nut(scale)
                        } else {
                            Fretwire(modifier = LayoutFlexible(1f), scale = scale)
                        }
                    }
                }
                Column(modifier = LayoutWidth.Fill) {
                    repeat(6) { index ->
                        GuitarString(
                            modifier = LayoutFlexible(1f) + LayoutPadding(left = (BASE_STRING_LEFT_PADDING * scale).dp),
                            thickness = ((((BASE_GUITAR_STRING_THICKNESS - index) * scale).toInt().let { if (it < 3) 3 else it }).dp
                                )
                        )
                    }
                }
                FretMarkerLayer(fretRange, from, toFret, markers, scale)
            }
        }
        FretNumberGutter(fretRange, from, toFret, scale)
    }
}

@Composable
private fun FretNumberGutter(fretRange: Int, from: Int, toFret: Int, scale: Float = 1.5f) {
    Container(
        modifier = background(Color.White) + LayoutSize(
            width = (BASE_FRET_WIDTH * fretRange * scale).dp,
            height = (BASE_FRET_BOARD_GUTTER_HEIGHT * scale).dp
        )
    ) {
        Row(modifier = LayoutWidth.Fill) {
            for (n in from until toFret) {
                Container(
                    modifier = LayoutFlexible(1f) + LayoutPadding(right = (BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT * scale).dp),
                    alignment = Alignment.Center
                ) {
                    if (n <= 0 || n != from) {
                        Text(text = "$n")
                    }
                }
            }
        }
    }
}

@Composable
private fun FretMarkerLayer(
    fretRange: Int,
    fromFret: Int,
    toFret: Int,
    markers: List<Marker>,
    scale: Float = 1.5f
) {
    Table(
        columns = fretRange,
        alignment = { Alignment.CenterLeft }) {
        repeat(6) { index ->
            val stringNumber = 6 - index
            tableRow {
                for (fretNumber in fromFret until toFret) {
                    // the gutter
                    if (fretNumber == fromFret) {
                        val openNote = markers.findOpenStringOrNull(stringNumber)
                        val mute = markers.findMutedStringOrNull(stringNumber)
                        when {
                            openNote != null -> FretMarker(openNote, scale)
                            mute != null -> MutedMarker(mute, scale)
                            else -> FretMarker(null, scale)
                        }
                    } else {
                        val marker = markers.findFrettedNoteOrNull(stringNumber, fretNumber)
                        FretMarker(marker, scale)
                    }
                }
            }
        }
    }
}

@Composable
private fun GuitarString(modifier: Modifier, thickness: Dp = 3.dp) {
    Container(modifier = modifier, alignment = Alignment.CenterRight) {
        ColoredRect(
            modifier = modifier + Border(
                RoundedCornerShape(1.dp),
                1.dp,
                Color.Black
            ) + LayoutHeight(thickness), brush = SolidColor(Color.LightGray)
        )
    }
}

@Composable
private fun Fretwire(modifier: Modifier, scale: Float = 1.5f) {
    Container(modifier = modifier, alignment = Alignment.CenterRight) {
        ColoredRect(
            modifier = Border(
                RoundedCornerShape(1.dp),
                1.dp,
                Color.Black
            ) + LayoutSize(width = (BASE_FRETWIRE_WIDTH * scale).dp, height = (BASE_FRETBOARD_HEIGHT * scale).dp),
            brush = SolidColor(Color.Gray)
        )
    }
}

@Composable
private fun Nut(scale: Float = 1.5f) {
    Row {
        ColoredRect(
            modifier = LayoutSize(
                width = (BASE_NUT_COLUMN_WIDTH * scale).dp,
                height = (BASE_FRETBOARD_HEIGHT * scale).dp
            ),
            brush = SolidColor(Color.Gray)
        )
        ColoredRect(
            modifier = LayoutSize(width = (BASE_NUT_WIDTH * scale).dp, height = (BASE_FRETBOARD_HEIGHT * scale).dp),
            brush = SolidColor(Color.Black)
        )
    }
}

@Composable
private fun MutedMarker(marker: Mute, scale: Float = 1.5f) {
    Container(
        modifier = LayoutSize(
            width = (BASE_FRET_WIDTH * scale).dp,
            height = (BASE_FRETMARKER_CONTAINER_HEIGHT * scale).dp
        ) + LayoutPadding(
            right = (BASE_MUTED_MARKER_RIGHT_PADDING * scale).dp
        ),
        alignment = Alignment.Center
    ) {
        Text("X")
    }
}

@Composable
private fun FretMarker(marker: FrettedNote?, scale: Float = 1.5f) {
    Stack(
        modifier = LayoutSize(
            width = (BASE_FRET_WIDTH * scale).dp,
            height = (BASE_FRETMARKER_CONTAINER_HEIGHT * scale).dp
        )
    ) {
        Container(
            modifier = LayoutSize(
                width = (BASE_FRETMARKER_SIZE * scale).dp,
                height = (BASE_FRETMARKER_SIZE * scale).dp
            ) + LayoutGravity.Center
        ) {
            if (marker != null) {
                DrawShape(shape = RoundedCornerShape(45.dp), brush = SolidColor(Color.Black))
            }
        }
        marker?.pitch?.let {
            Text(
                modifier = LayoutGravity.Center,
                text = it.toString().replace("s", "#"),
                style = TextStyle(color = Color.White)
            )
        }
    }
}

sealed class Marker {
    data class FrettedNote(
        @IntRange(from = 1, to = 6)
        val stringNumber: Int,
        @IntRange(from = 0)
        val fretNumber: Int
    ) : Marker() {
        val stringPitch = when (stringNumber) {
            1 -> PitchClass.E
            2 -> PitchClass.B
            3 -> PitchClass.G
            4 -> PitchClass.D
            5 -> PitchClass.A
            6 -> PitchClass.E
            else -> throw RuntimeException("Invalid string number")
        }

        val pitch: PitchClass?
            get() = if (fretNumber >= 0) PitchClass.values()[((fretNumber + stringPitch.ordinal) % 12)] else null
    }

    data class Mute(
        @IntRange(from = 1, to = 6)
        val stringNumber: Int
    ) : Marker()
}

fun List<Marker>.findOpenStringOrNull(stringNumber: Int): FrettedNote? = firstOrNull {
    it is FrettedNote && it.stringNumber == stringNumber && it.fretNumber == 0
} as? FrettedNote

fun List<Marker>.findMutedStringOrNull(stringNumber: Int) =
    firstOrNull { it is Mute && it.stringNumber == stringNumber } as? Mute

fun List<Marker>.findFrettedNoteOrNull(stringNumber: Int, fretNumber: Int): FrettedNote? = firstOrNull {
    it is FrettedNote && it.stringNumber == stringNumber && it.fretNumber == fretNumber
} as? FrettedNote

private const val BASE_FRETMARKER_CONTAINER_HEIGHT = 16
private const val BASE_FRETBOARD_HEIGHT = 6 * BASE_FRETMARKER_CONTAINER_HEIGHT
private const val BASE_FRET_WIDTH = 24
private const val BASE_STRING_LEFT_PADDING = 10
private const val BASE_FRETMARKER_SIZE = 14
private const val BASE_MUTED_MARKER_RIGHT_PADDING = 4
private const val BASE_NUT_COLUMN_WIDTH = 16
private const val BASE_NUT_WIDTH = 8
private const val BASE_FRETWIRE_WIDTH = 3
private const val BASE_GUITAR_STRING_THICKNESS = 5
private const val BASE_FRET_BOARD_GUTTER_HEIGHT = 16
private const val BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT = 4
