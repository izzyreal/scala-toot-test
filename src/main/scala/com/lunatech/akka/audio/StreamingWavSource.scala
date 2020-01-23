package com.lunatech.akka.audio

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}

class StreamingWavSource extends GraphStage[SourceShape[Double]] {

  private val Freq = 440
  private val SampleRate = 44100
  private val TwoPi = 2 * Math.PI

  val out: Outlet[Double] = Outlet("StreamingWavSource")
  override val shape: SourceShape[Double] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      private var frameIndex = 0

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if (frameIndex % 88200 > 10000) {
            push(out, 0d)
          } else {
            val sample = Math.sin((TwoPi * Freq) / SampleRate * frameIndex)
            push(out, sample)
          }
          frameIndex += 1
        }
      })
    }
}
