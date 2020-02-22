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
import androidx.ui.foundation.Clickable
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
import com.citizenwarwick.fretboard.FretboardMarker.FrettedNote
import com.citizenwarwick.fretboard.FretboardMarker.MutedString
import com.citizenwarwick.music.PitchClass

@Composable
@Preview
fun FretboardPreview() {
    Column {
        Column {
            GuitarChord("2|3|2|0|x|x".fingering, 0, 5)
            GuitarChord("8|9|8|0|x|x".fingering, 7, 11)
            GuitarChord("4|4|4|4|4|4".fingering, 2, 6)
        }
    }
}

@Composable
fun GuitarChord(
    fretboardMarkers: List<FretboardMarker>,
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    Fretboard(fromFret, toFret, fretboardMarkers, scale, onFretboardPressed)
}

@Composable
fun Fretboard(
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    fretboardMarkers: List<FretboardMarker> = listOf(),
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    check(fromFret in 0 until toFret) {
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
                FretMarkerLayer(from, toFret, fretboardMarkers, scale, onFretboardPressed)
            }
        }
        FretNumberGutter(from, toFret, scale)
    }
}

@Composable
private fun FretNumberGutter(fromFret: Int, toFret: Int, scale: Float = 1.5f) {
    val fretRange = toFret - fromFret
    Container(
        modifier = background(Color.White) + LayoutSize(
            width = (BASE_FRET_WIDTH * fretRange * scale).dp,
            height = (BASE_FRET_BOARD_GUTTER_HEIGHT * scale).dp
        )
    ) {
        Row(modifier = LayoutWidth.Fill) {
            for (n in fromFret until toFret) {
                Container(
                    modifier = LayoutFlexible(1f) + LayoutPadding(right = (BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT * scale).dp),
                    alignment = Alignment.Center
                ) {
                    if (n <= 0 || n != fromFret) {
                        Text(text = "$n")
                    }
                }
            }
        }
    }
}

@Composable
private fun FretMarkerLayer(
    fromFret: Int,
    toFret: Int,
    fretboardMarkers: List<FretboardMarker>,
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    val fretRange = toFret - fromFret

    Table(columns = fretRange) {
        repeat(6) { index ->
            val stringNumber = 6 - index
            tableRow {
                for (fretNumber in fromFret until toFret) {
                    Clickable(onClick = { onFretboardPressed(stringNumber, fretNumber) }) {
                        if (fretNumber == fromFret) {
                            val openNote = fretboardMarkers.findOpenStringOrNull(stringNumber)
                            val mute = fretboardMarkers.findMutedStringOrNull(stringNumber)
                            when {
                                openNote != null -> FretMarker(openNote, scale)
                                mute != null -> MutedMarker(mute, scale)
                                else -> FretMarker(null, scale)
                            }
                        } else {
                            val marker = fretboardMarkers.findFrettedNoteOrNull(stringNumber, fretNumber)
                            FretMarker(marker, scale)
                        }
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
private fun MutedMarker(marker: MutedString, scale: Float = 1.5f) {
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

/**
 * A marking on the Fretboard such as a note marker or an X muted string.
 */
sealed class FretboardMarker {
    data class FrettedNote(
        @IntRange(from = 1)
        val stringNumber: Int,
        @IntRange(from = 0)
        val fretNumber: Int,
        val tuning: (stringNumber: Int) -> PitchClass = standardTuningSixString()
    ) : FretboardMarker() {
        val pitch: PitchClass?
            get() = if (fretNumber >= 0) PitchClass.values()[((fretNumber + tuning(stringNumber).ordinal) % 12)] else null
    }

    data class MutedString(
        @IntRange(from = 1)
        val stringNumber: Int
    ) : FretboardMarker()
}

fun standardTuningSixString(): (stringNumber: Int) -> PitchClass = { stringNumber ->
    when (stringNumber) {
        1 -> PitchClass.E
        2 -> PitchClass.B
        3 -> PitchClass.G
        4 -> PitchClass.D
        5 -> PitchClass.A
        6 -> PitchClass.E
        else -> PitchClass.E // For any other string
    }
}

fun List<FretboardMarker>.findOpenStringOrNull(stringNumber: Int): FrettedNote? = firstOrNull {
    it is FrettedNote && it.stringNumber == stringNumber && it.fretNumber == 0
} as? FrettedNote

fun List<FretboardMarker>.findMutedStringOrNull(stringNumber: Int) =
    firstOrNull { it is MutedString && it.stringNumber == stringNumber } as? MutedString

fun List<FretboardMarker>.findFrettedNoteOrNull(stringNumber: Int, fretNumber: Int): FrettedNote? = firstOrNull {
    it is FrettedNote && it.stringNumber == stringNumber && it.fretNumber == fretNumber
} as? FrettedNote

/**
 * Replaces a marker in the list of markers if it falls on the same
 * string as the given marker
 */
fun MutableList<FretboardMarker>.replaceOnSameString(note: FrettedNote) {
    val index = indexOfFirst {
        when (it) {
            is FrettedNote -> it.stringNumber == note.stringNumber
            is MutedString -> it.stringNumber == note.stringNumber
        }
    }

    if (index > -1) {
        this[index] = note
    } else {
        add(note)
    }
}

/**
 * Replaces a marker in the list of markers if it falls on the same
 * string as the given marker
 */
fun MutableList<FretboardMarker>.replaceOnSameString(mutedString: MutedString) {
    val index = indexOfFirst {
        when (it) {
            is FrettedNote -> it.stringNumber == mutedString.stringNumber
            is MutedString -> it.stringNumber == mutedString.stringNumber
        }
    }

    if (index > -1) {
        this[index] = mutedString
    } else {
        add(mutedString)
    }
}

/**
 * Parse the string from a textual guitar chord fingering DSL / format into a List of [FretboardMarker].
 *
 * The simple format allows a pipe delimited sequence of fret numbers or an x to specify a muted string.
 *
 * Example: "2|3|2|0|x|x".fingering describes a D Major open chord where each section between a pipe | indicates
 * a fret number. The string number is implicitly the index of each section.
 *
 *         input: 2|3|2|0|x|x
 * string number: 1 2 3 4 5 6
 */
val String.fingering: List<FretboardMarker>
    get() {
        return split("|").mapIndexed { index, value ->
            when {
                value == "x" -> MutedString(index + 1)
                value.toIntOrNull() != null -> FrettedNote(index + 1, value.toInt())
                else -> throw IllegalArgumentException("Invalid fingering format $value")
            }
        }
    }

/**
 * Turns a list of [FretboardMarker] into a pipe delimited representation (see [fingering]
 *
 * String numbers are ignored and implied by order where the zeroth item in the list is string 1.
 *
 * You should make sure that this list is already in string order and has no duplicates otherwise
 * your string representation will give unexpected results.
 */
val List<FretboardMarker>.encodeFingering: String
    get() {
        return joinToString(separator = "|") {
            when (it) {
                is FrettedNote -> it.fretNumber.toString()
                is MutedString -> "x"
            }
        }
    }

public inline fun <T, R : Comparable<R>> Iterable<T>.minBy(lower: R, selector: (T) -> R): T? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var minElem = iterator.next()
    if (!iterator.hasNext()) return minElem
    var minValue = selector(minElem)
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v && v > lower) {
            minElem = e
            minValue = v
        }
    } while (iterator.hasNext())
    return minElem
}

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
