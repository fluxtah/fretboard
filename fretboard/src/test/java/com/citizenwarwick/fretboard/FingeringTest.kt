package com.citizenwarwick.fretboard

import junit.framework.Assert.assertEquals
import org.junit.Test

class FingeringTest {
    @Test
    fun fingeringFromStringForSixStrings() {
        val fingering = "2|3|2|0|x|x".fingering

        assertEquals(6, fingering.size)
        assertFrettedNote(1, 2, fingering[0])
        assertFrettedNote(2, 3, fingering[1])
        assertFrettedNote(3, 2, fingering[2])
        assertFrettedNote(4, 0, fingering[3])
        assertMutedString(5, fingering[4])
        assertMutedString(6, fingering[5])
    }

    @Test
    fun fingeringFromStringForFourStrings() {
        val fingering = "2|3|2|0".fingering

        assertEquals(4, fingering.size)
        assertFrettedNote(1, 2, fingering[0])
        assertFrettedNote(2, 3, fingering[1])
        assertFrettedNote(3, 2, fingering[2])
        assertFrettedNote(4, 0, fingering[3])
    }

    @Test
    fun fingeringFromStringForSevenStrings() {
        val fingering = "2|3|2|0|x|x|12".fingering

        assertEquals(7, fingering.size)
        assertFrettedNote(1, 2, fingering[0])
        assertFrettedNote(2, 3, fingering[1])
        assertFrettedNote(3, 2, fingering[2])
        assertFrettedNote(4, 0, fingering[3])
        assertMutedString(5, fingering[4])
        assertMutedString(6, fingering[5])
        assertFrettedNote(7, 12, fingering[6])
    }

    @Test
    fun encodeFingeringToString() {
        val fingering = "2|3|2|0|x|x".fingering.encodeFingering

        assertEquals("2|3|2|0|x|x", fingering)
    }

    @Test
    fun encodeFingeringToStringForFourString() {
        val fingering = "2|3|2|0".fingering.encodeFingering

        assertEquals("2|3|2|0", fingering)
    }

    @Test
    fun encodeFingeringToStringForSevenString() {
        val fingering = "2|3|2|0|x|x|12".fingering.encodeFingering

        assertEquals("2|3|2|0|x|x|12", fingering)
    }

    fun assertFrettedNote(expectedString: Int, expectedFret: Int, fretboardMarker: FretboardMarker) {
        assertEquals(expectedString, (fretboardMarker as FretboardMarker.FrettedNote).stringNumber)
        assertEquals(expectedFret, fretboardMarker.fretNumber)
    }

    fun assertMutedString(expectedString: Int, fretboardMarker: FretboardMarker) {
        assertEquals(expectedString, (fretboardMarker as FretboardMarker.MutedString).stringNumber)
    }
}
