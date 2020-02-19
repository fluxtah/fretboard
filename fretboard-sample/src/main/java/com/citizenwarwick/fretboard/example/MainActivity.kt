package com.citizenwarwick.fretboard.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.citizenwarwick.fretboard.GuitarChord
import com.citizenwarwick.fretboard.fingering

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Container {
                    Column {
                        var selectedFret by state { "Click a fret" }
                        Text(selectedFret, modifier = LayoutPadding(bottom = 4.dp))
                        GuitarChord("2|3|2|0|x|x".fingering, scale = 2.0f, onFretboardPressed = { string, fret ->
                            selectedFret = "You pressed string: $string on fret $fret"
                        })
                    }
                }
            }
        }
    }
}
