package com.lunatech.akka.audio

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import javax.sound.sampled.{AudioFormat, AudioSystem, SourceDataLine}
import uk.org.toot.audio.core.AudioBuffer

class JavaSoundSink extends GraphStage[SinkShape[Seq[Double]]] {

  val in: Inlet[Seq[Double]] = Inlet("JavaSoundSink")
  override val shape: SinkShape[Seq[Double]] = SinkShape(in)

  private val af = new AudioFormat(44100, 16, 2, true, false)
  private val line: SourceDataLine = openLine()
  private val audioBuffer = new AudioBuffer("foo", 2, Main.bufferSize, 44100f)

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
    line.open(af, Main.bufferSize * 2 * 2)
    line.start()
    line
  }

  private def play(buffer: Seq[Double]) = {
    val l = audioBuffer.getChannel(0)
    val r = audioBuffer.getChannel(1)
    for (i <- buffer.indices) {
      audioBuffer.getChannel(0)(i) = buffer(i).toFloat
      audioBuffer.getChannel(1)(i) = buffer(i).toFloat
    }
    line.write(audioBuffer.convertToByteArray(af), 0, audioBuffer.getByteArrayBufferSize(af))
  }

  def closeLine() = {
    line.drain()
    line.close()
  }

}
