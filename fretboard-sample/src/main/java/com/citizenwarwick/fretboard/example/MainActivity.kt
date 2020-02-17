package com.citizenwarwick.fretboard.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import androidx.ui.layout.Container
import androidx.ui.material.MaterialTheme
import com.citizenwarwick.fretboard.GuitarChord
import com.citizenwarwick.fretboard.Marker.FrettedNote
import com.citizenwarwick.fretboard.Marker.Mute

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Container {
                    val fingers = listOf(
                        FrettedNote(1, 2),
                        FrettedNote(2, 3),
                        FrettedNote(3, 2),
                        FrettedNote(4, 0),
                        Mute(5),
                        Mute(6)
                    )
                    GuitarChord(fingers)
                }
            }
        }
    }
}
