package com.lunatech.akka.audio.ui

import com.lunatech.akka.audio.source.freqFromNote

import scala.swing.event.Key

/**
 * https://sonicbloom.net/en/ableton-live-tutorial-computer-keyboard-as-midi-controller/
 */

object ComputerMidiKeyboard {

  val keyToNote = Map(
    Key.A -> C,
    Key.W -> CSharp,
    Key.S -> D,
    Key.E -> DSharp,
    Key.D -> E

  )

  private val BaseOctave: Int = 3

  var octave: Int = BaseOctave

  sealed trait Note {
    val baseNoteNumber: Int
    def noteNumber: Int = baseNoteNumber + (octave - BaseOctave) * 12
    def freq: Double = freqFromNote(noteNumber)
  }

  case object C extends Note {
    val baseNoteNumber = 48
  }

  case object CSharp extends Note {
    val baseNoteNumber = 49
  }

  case object D extends Note {
    val baseNoteNumber = 50
  }

  case object DSharp extends Note {
    val baseNoteNumber = 51
  }

  case object E extends Note {
    val baseNoteNumber = 52
  }

  case object F extends Note {
    val baseNoteNumber = 53
  }

  case object FSharp extends Note {
    val baseNoteNumber = 54
  }

  case object G extends Note {
    val baseNoteNumber = 55
  }

  case object GSharp extends Note {
    val baseNoteNumber = 56
  }

  case object A extends Note {
    val baseNoteNumber = 57
  }



}
