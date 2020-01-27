package com.lunatech.akka.audio

package object source {

  // https://www.inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies
  private val baseFreq = 440
  private val baseNote = 69

  // http://www.techlib.com/reference/musical_note_frequencies.htm
  def freq(note: Int) = baseFreq * 2 ^ (baseNote - note)
  
}
