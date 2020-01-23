package com.lunatech.akka.audio

import akka.stream.Shape
import akka.stream.stage.GraphStageLogic
import uk.org.toot.audio.core.{AudioBuffer, AudioProcess, ChannelFormat}
import uk.org.toot.audio.server.IOAudioProcess

class GraphStageLogicAudioProcess(shape: Shape)
  extends GraphStageLogic(shape) with IOAudioProcess {

  var buf = Ring[Double](1000000)()

  override def getChannelFormat: ChannelFormat = ChannelFormat.STEREO

  override def getName: String = "foo"

  override def open(): Unit = {}

  override def processAudio(buffer: AudioBuffer): Int = {
    if (buffer.getSampleCount > buf.size) {
      buffer.makeSilence()
      return AudioProcess.AUDIO_SILENCE
    }
    val l: Array[Float] = buffer.getChannel(0)
    val r: Array[Float] = buffer.getChannel(1)
    for (i <- l.indices) {
      val popped = buf.pop
      buf = popped._2
      val sample = popped._1
      l(i) = sample.toFloat
      r(i) = sample.toFloat
    }
    AudioProcess.AUDIO_OK
  }

  override def close(): Unit = {}
}
