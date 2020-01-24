package com.lunatech.akka.audio

import scala.swing.{Dimension, MainFrame}

class UI extends MainFrame {
  title = "TurboSynth"
  preferredSize = new Dimension(640, 480)
  resizable = false
  peer.setUndecorated(false)
  contents = new Canvas
}
