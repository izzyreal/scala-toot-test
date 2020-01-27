package com.lunatech.akka.audio.ui

import java.awt.Color

import com.lunatech.akka.audio.Config
import com.lunatech.akka.audio.source.Synth
import com.lunatech.akka.audio.ui.ComputerMidiKeyboard._

import scala.swing.event.{Key, KeyPressed}
import scala.swing.{Component, Graphics2D}

class Canvas(synths: Seq[Synth]) extends Component {

  listenTo(keys)
  focusable = true
  requestFocus()

  reactions += {
    case KeyPressed(_, keyValue, _, _) if keyToNote.contains(keyValue) =>
      synths.foreach(_.trigger(keyToNote(keyValue).freq))
    case KeyPressed(_, Key.Z, _, _) =>
      octave -= 1
    case KeyPressed(_, Key.X, _, _) =>
      octave += 1
  }

  override def paintComponent(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, Config.width, Config.height)
  }
}
