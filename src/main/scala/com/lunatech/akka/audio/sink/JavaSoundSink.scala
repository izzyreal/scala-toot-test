package com.lunatech.akka.audio.sink

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import com.lunatech.akka.audio.Config
import javax.sound.sampled.{AudioFormat, AudioSystem, SourceDataLine}
import uk.org.toot.audio.core.AudioBuffer

class JavaSoundSink extends GraphStage[SinkShape[Seq[Double]]] {

  val in: Inlet[Seq[Double]] = Inlet("JavaSoundSink")
  override val shape: SinkShape[Seq[Double]] = SinkShape(in)

  private val bufferSize = Config.bufferSize
  private val sampleRate = Config.sampleRate

  private val af = new AudioFormat(sampleRate, 16, 2, true, false)
  private val line: SourceDataLine = openLine()
  private val audioBuffer = new AudioBuffer("foo", 2, bufferSize, sampleRate)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    override def preStart(): Unit = pull(in)

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        val data = grab(in)
        play(data)
        pull(in)
      }
    })
  }

  private def openLine(): SourceDataLine = {
    val line = AudioSystem.getSourceDataLine(af)
    line.open(af, bufferSize * 2 * 2)
    line.start()
    line
  }

  private def play(buffer: Seq[Double]) = {
    val l = audioBuffer.getChannel(0)
    val r = audioBuffer.getChannel(1)
    for (i <- buffer.indices) {
      l(i) = buffer(i).toFloat
      r(i) = buffer(i).toFloat
    }
    line.write(audioBuffer.convertToByteArray(af), 0, audioBuffer.getByteArrayBufferSize(af))
  }

  def closeLine() = {
    line.drain()
    line.close()
  }

}
