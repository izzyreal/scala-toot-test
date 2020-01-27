package com.lunatech.akka.audio.source

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import com.lunatech.akka.audio.Config

class Sine extends GraphStage[SourceShape[Double]] with Synth {

  private val SampleRate = Config.sampleRate
  private val TwoPi = 2 * Math.PI

  val out: Outlet[Double] = Outlet("StreamingWavSource")
  override val shape: SourceShape[Double] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      private var frameIndex = 0

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          val sample = Math.sin((TwoPi * freq) / SampleRate * frameIndex) * 0.5
          push(out, sample)
          frameIndex += 1
        }
      })
    }
}
