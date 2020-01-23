package com.lunatech.akka.audio

import java.io.File

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import uk.co.labbookpages.WavFile

class StreamingWavSource extends GraphStage[SourceShape[Double]] {


  val wavFile = WavFile.openWavFile(new File("breakbeat.wav"))
  val numChannels = wavFile.getNumChannels
  val frameBuffer = new Array[Double](numChannels)
  var frameCounter: Int = 0
  val cache = new Array[Double](wavFile.getNumFrames.toInt)

  val out: Outlet[Double] = Outlet("StreamingWavSource")
  override val shape: SourceShape[Double] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if (wavFile.getFramesRemaining == 0) {
            wavFile.close()
            push(out, cache(frameCounter % wavFile.getNumFrames.toInt))
            frameCounter += 1
          } else {
            wavFile.readFrames(frameBuffer, 1)
            cache(frameCounter % wavFile.getNumFrames.toInt) = frameBuffer(0)
            push(out, frameBuffer(0))
            frameCounter += 1
          }
        }
      })
    }
}
