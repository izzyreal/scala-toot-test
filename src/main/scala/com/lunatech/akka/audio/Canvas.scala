package com.lunatech.akka.audio

import java.awt.Color

import scala.swing.{Component, Graphics2D}
import scala.swing.event.KeyPressed

class Canvas extends Component {

  listenTo(keys)
  focusable = true
  requestFocus()

  reactions += {
    case KeyPressed(_, v, _, _) =>
      if (v == KeyConfig.up) {
        Main.increasePeriod(0.5)
      } else if (v == KeyConfig.down) {
        Main.increasePeriod(-0.5)
      }
  }

  override def paintComponent(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, 640, 480)
  }
}
