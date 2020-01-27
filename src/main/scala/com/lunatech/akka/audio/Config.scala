package com.lunatech.akka.audio

import com.typesafe.config.ConfigFactory

object Config {

  private val root = ConfigFactory.load()
  private val ui = root.getConfig("ui")
  private val audio = root.getConfig("audio")

  val bufferSize = audio.getInt("bufferSize")
  val sampleRate = audio.getInt("sampleRate")

  val width = ui.getInt("width")
  val height = ui.getInt("height")
}
