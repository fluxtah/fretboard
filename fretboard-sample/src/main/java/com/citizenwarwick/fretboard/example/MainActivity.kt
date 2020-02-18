package com.citizenwarwick.fretboard.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import androidx.ui.layout.Container
import androidx.ui.material.MaterialTheme
import com.citizenwarwick.fretboard.GuitarChord
import com.citizenwarwick.fretboard.fingering

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Container {
                    GuitarChord("2|3|2|0|x|x".fingering)
                }
            }
        }
    }
}
