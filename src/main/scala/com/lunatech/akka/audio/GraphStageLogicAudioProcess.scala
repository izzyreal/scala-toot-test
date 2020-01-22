package com.lunatech.akka.audio

import akka.stream.Shape
import akka.stream.stage.GraphStageLogic
import uk.org.toot.audio.core.{AudioBuffer, AudioProcess, ChannelFormat}
import uk.org.toot.audio.server.IOAudioProcess

class GraphStageLogicAudioProcess(shape: Shape)
  extends GraphStageLogic(shape) with IOAudioProcess {

  override def getChannelFormat: ChannelFormat = ChannelFormat.STEREO

  override def getName: String = "foo"

  override def open(): Unit = {}

  override def processAudio(buffer: AudioBuffer): Int = {

    val l: Array[Float] = buffer.getChannel(0)
    val r: Array[Float] = buffer.getChannel(1)

    val samples = grab(shape.inlets(0)).asInstanceOf[Seq[Double]]

    for (i <- l.indices) {
      l(i) = samples(i).toFloat
      r(i) = samples(i).toFloat
    }

    pull(shape.inlets(0))
    AudioProcess.AUDIO_OK
  }

  override def close(): Unit = {}
}
