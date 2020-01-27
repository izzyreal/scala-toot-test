package com.lunatech.akka.audio.ui

import java.awt.Color

import com.lunatech.akka.audio.source.Sine

import scala.swing.event.KeyPressed
import scala.swing.{Component, Graphics2D}

class Canvas extends Component {

  listenTo(keys)
  focusable = true
  requestFocus()

  reactions += {
    case KeyPressed(_, v, _, _) =>
      if (v == KeyConfig.up) {
        Sine.freq += 10
      } else if (v == KeyConfig.down) {
        Sine.freq -= 10
      }
  }

  override def paintComponent(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, 640, 480)
  }
}
