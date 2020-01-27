package com.lunatech.akka.audio.ui

import com.lunatech.akka.audio.Config

import scala.swing.{Dimension, MainFrame}

class AkkaAudioUI extends MainFrame {
  title = "Akka Audio Toys"
  preferredSize = new Dimension(Config.width, Config.height)
  resizable = true
  peer.setUndecorated(false)
  contents = new Canvas
  visible = true
}
