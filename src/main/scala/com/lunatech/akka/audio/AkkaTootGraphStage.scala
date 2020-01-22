package com.lunatech.akka.audio

import akka.stream.stage.{GraphStage, GraphStageLogic}
import akka.stream.{Attributes, SinkShape}

class AkkaTootGraphStage(override val shape: SinkShape[Seq[Double]], graphStageLogic: GraphStageLogic)
  extends GraphStage[SinkShape[Seq[Double]]] {

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = graphStageLogic

}
