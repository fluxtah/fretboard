package com.citizenwarwick.fretboard

import com.citizenwarwick.fretboard.FretboardMarker.MutedString
import junit.framework.Assert.assertEquals
import org.junit.Test

class FretboardMarkerListExtensionTest {
    @Test
    fun replaceOnSameString() {
        val chord = "2|3|2|0|x|x".fingering.toMutableList()
        chord.replaceOnSameString(MutedString(2))
        val fingeringDsl = chord.encodeFingering
        assertEquals("2|x|2|0|x|x", fingeringDsl)
    }
}
