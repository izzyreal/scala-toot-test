package com.lunatech.akka.audio

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import uk.org.toot.audio.mixer.AudioMixer
import uk.org.toot.audio.server.IOAudioProcess

class AkkaTootSink(mixer: AudioMixer) extends GraphStage[SinkShape[Seq[Double]]] {
  val in: Inlet[Seq[Double]] = Inlet("AkkaTootSink")
  override val shape: SinkShape[Seq[Double]] = SinkShape(in)

  var process: GraphStageLogicAudioProcess = _

  def getProcess: IOAudioProcess = process

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    process = new GraphStageLogicAudioProcess(shape) {

      override def preStart(): Unit = pull(in)

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val data = grab(in)
          data.foreach(d => buf = buf.push(d)._2)
          pull(in)
        }
      })
    }
    mixer.getStrip("1").setInputProcess(process)
    process
  }
}