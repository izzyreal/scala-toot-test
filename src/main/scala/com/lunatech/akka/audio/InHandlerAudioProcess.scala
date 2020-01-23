package com.lunatech.akka.audio

import akka.stream.stage.InHandler
import uk.org.toot.audio.core.{AudioBuffer, AudioProcess, ChannelFormat}
import uk.org.toot.audio.server.IOAudioProcess

class InHandlerAudioProcess extends IOAudioProcess with InHandler {

  //  var ring = Ring[Double](1000)()

  var buf = Seq.empty[Double]

  override def getChannelFormat: ChannelFormat = ChannelFormat.STEREO

  override def getName: String = "foo"

  override def open(): Unit = {}

  override def processAudio(buffer: AudioBuffer): Int = {

    if (buffer.getSampleCount != buf.size) {
      buffer.makeSilence()
      return AudioProcess.AUDIO_SILENCE
    }

    val l: Array[Float] = buffer.getChannel(0)
    val r: Array[Float] = buffer.getChannel(1)

    for (i <- l.indices) {
      l(i) = buf(i).toFloat
      r(i) = buf(i).toFloat
    }
    buf = Seq.empty[Double]
    AudioProcess.AUDIO_OK
  }

  override def close(): Unit = {}

  override def onPush(): Unit = {}
}
