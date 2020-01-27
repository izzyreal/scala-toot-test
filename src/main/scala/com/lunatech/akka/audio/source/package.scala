package com.lunatech.akka.audio

package object source {

  // https://www.inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies
  private val baseFreq = 440d
  private val baseNote = 69

  // http://www.techlib.com/reference/musical_note_frequencies.htm
  def freqFromNote(note: Int) = baseFreq * scala.math.pow(2, (note - baseNote) / 12d)

}
