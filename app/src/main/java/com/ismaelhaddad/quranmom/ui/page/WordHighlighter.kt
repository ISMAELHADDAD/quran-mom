package com.ismaelhaddad.quranmom.ui.page

import androidx.media3.common.Player
import com.ismaelhaddad.quranmom.model.AyahWordWithSegment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WordHighlighter(
    private val player: Player,
    private val onWordHighlightChanged: (AyahWordWithSegment?) -> Unit
) {
    private var wordSegments: List<AyahWordWithSegment> = emptyList()
    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) startTracking()
            else stopTracking()
        }
    }

    init {
        player.addListener(listener)
    }

    fun setWordSegments(words: List<AyahWordWithSegment>) {
        wordSegments = words.sortedBy { it.segmentStart }
    }

    private fun startTracking() {
        CoroutineScope(Dispatchers.Main).launch {
            while (player.isPlaying) {
                val currentPosition = player.currentPosition
                val currentWord = findCurrentWord(currentPosition)
                onWordHighlightChanged(currentWord)
                delay(100)
            }
            onWordHighlightChanged(null)
        }
    }

    private fun stopTracking() {
        onWordHighlightChanged(null)
    }

    private fun findCurrentWord(position: Long): AyahWordWithSegment? {
        return wordSegments.find { position in (it.segmentStart+1)..<it.segmentEnd }
    }

    fun release() {
        player.removeListener(listener)
    }
}
