package com.lunatech.akka.audio

import akka.stream.Shape
import akka.stream.stage.GraphStageLogic
import org.apache.commons.collections4.queue.CircularFifoQueue
import uk.org.toot.audio.core.{AudioBuffer, AudioProcess, ChannelFormat}
import uk.org.toot.audio.server.IOAudioProcess

class GraphStageLogicAudioProcess(shape: Shape)
  extends GraphStageLogic(shape) with IOAudioProcess {

  var ring = new CircularFifoQueue[Double](4000)

  override def getChannelFormat: ChannelFormat = ChannelFormat.STEREO

  override def getName: String = "foo"

  override def open(): Unit = {}

  override def processAudio(buffer: AudioBuffer): Int = {
    if (buffer.getSampleCount > ring.size()) {
      buffer.makeSilence()
      return AudioProcess.AUDIO_SILENCE
    }
    val l: Array[Float] = buffer.getChannel(0)
    val r: Array[Float] = buffer.getChannel(1)
    for (i <- l.indices) {
      val sample = ring.poll()
      l(i) = sample.toFloat
      r(i) = sample.toFloat
    }
    AudioProcess.AUDIO_OK
  }

  override def close(): Unit = {}
}
