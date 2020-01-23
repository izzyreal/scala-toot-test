package com.lunatech.akka.audio

import akka.stream.Shape
import akka.stream.stage.GraphStageLogic
import uk.org.toot.audio.core.{AudioBuffer, AudioProcess, ChannelFormat}
import uk.org.toot.audio.server.IOAudioProcess

class GraphStageLogicAudioProcess(shape: Shape)
  extends GraphStageLogic(shape) with IOAudioProcess {

  var buf = Array.emptyDoubleArray

  override def getChannelFormat: ChannelFormat = ChannelFormat.STEREO

  override def getName: String = "foo"

  override def open(): Unit = {}

  override def processAudio(buffer: AudioBuffer): Int = {
    try {
      val l: Array[Float] = buffer.getChannel(0)
      val r: Array[Float] = buffer.getChannel(1)

      for (i <- l.indices) {
        l(i) = buf(i).toFloat
        r(i) = buf(i).toFloat
      }
      buf = Array.emptyDoubleArray
    } catch {
      case (e: Exception) =>
//        println(e.getMessage)
    }
    AudioProcess.AUDIO_OK
  }

  override def close(): Unit = {}
}
