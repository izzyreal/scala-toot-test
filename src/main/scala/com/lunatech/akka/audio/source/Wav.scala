package com.lunatech.akka.audio.source

import java.io.File

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import uk.co.labbookpages.WavFile

class Wav extends GraphStage[SourceShape[Double]] with Synth {

  var frameIndex: Int = 0
  val cache = readWavToCache()

  val out: Outlet[Double] = Outlet("StreamingWavSource")
  override val shape: SourceShape[Double] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          push(out, cache(frameIndex % cache.length))
          frameIndex += 1
        }
      })
    }

  override def trigger(f: Double): Unit = {
    if (keyDown) return
    frameIndex = 0
    super.trigger(f)
  }

  private def readWavToCache() = {
    val wavFile = WavFile.openWavFile(new File("breakbeat.wav"))
    val frameCount = wavFile.getNumFrames.toInt
    val frameBuffer = new Array[Double](frameCount)
    wavFile.readFrames(frameBuffer, 0, frameCount)
    wavFile.close()
    frameBuffer
  }
}
