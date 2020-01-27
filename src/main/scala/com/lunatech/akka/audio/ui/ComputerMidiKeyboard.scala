package com.lunatech.akka.audio.ui

import com.lunatech.akka.audio.source.freqFromNote

import scala.swing.event.Key

/**
 * https://sonicbloom.net/en/ableton-live-tutorial-computer-keyboard-as-midi-controller/
 */

object ComputerMidiKeyboard {

  val keyToNote = Map(
    Key.A -> Note(48),
    Key.W -> Note(49),
    Key.S -> Note(50),
    Key.E -> Note(51),
    Key.D -> Note(52),
    Key.F -> Note(53),
    Key.T -> Note(54),
    Key.G -> Note(55),
    Key.Y -> Note(56),
    Key.H -> Note(57),
    Key.U -> Note(58),
    Key.J -> Note(59),
    Key.K -> Note(60),
    Key.O -> Note(61),
    Key.L -> Note(62)
  )

  private val BaseOctave: Int = 3

  var octave: Int = BaseOctave

  case class Note(baseNote: Int) {
    def noteNumber: Int = baseNote + (octave - BaseOctave) * 12

    def freq: Double = freqFromNote(noteNumber)
  }

}
