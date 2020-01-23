package com.lunatech.akka.audio

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}

class StreamingWavSourceSeq extends GraphStage[SourceShape[Seq[Double]]] {

  private val Freq = 440
  private val SampleRate = 44100
  private val TwoPi = 2 * Math.PI
  private val BufferSize = 88

  val out: Outlet[Seq[Double]] = Outlet("StreamingWavSource")
  override val shape: SourceShape[Seq[Double]] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      private var frameIndex = 0

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          val buffer = scala.collection.mutable.Seq[Double]()
          for (i <- 0 to BufferSize) {
            val sample = Math.sin((TwoPi * Freq) / SampleRate * frameIndex + i)
            buffer :+ sample
          }
          push(out, buffer.toSeq)
          frameIndex += BufferSize
        }
      })
    }
}
