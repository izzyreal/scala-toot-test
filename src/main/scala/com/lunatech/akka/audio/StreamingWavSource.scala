package com.lunatech.akka.audio

import java.io.File

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import uk.co.labbookpages.WavFile

class StreamingWavSource extends GraphStage[SourceShape[Double]] {

  val wavFile = WavFile.openWavFile(new File("breakbeat.wav"))
  val numChannels = wavFile.getNumChannels
  val buffer = new Array[Double](numChannels)

  val out: Outlet[Double] = Outlet("StreamingWavSource")
  override val shape: SourceShape[Double] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          wavFile.readFrames(buffer, 1)
          push(out, buffer(0))
        }
      })
    }
}
