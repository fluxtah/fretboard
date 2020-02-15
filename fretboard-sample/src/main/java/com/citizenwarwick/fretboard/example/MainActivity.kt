package com.citizenwarwick.fretboard.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import androidx.ui.layout.Container
import androidx.ui.material.MaterialTheme
import com.citizenwarwick.fretboard.Fretboard
import com.citizenwarwick.fretboard.Marker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Container {
                    val markers = listOf(
                        Marker.FrettedNote(1, 2),
                        Marker.FrettedNote(2, 3),
                        Marker.FrettedNote(3, 2),
                        Marker.FrettedNote(4, 0),
                        Marker.Mute(5),
                        Marker.Mute(6)
                    )
                    Fretboard(0, 5, markers, scale = 2.0f)
                }
            }
        }
    }
}
