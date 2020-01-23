package com.lunatech.akka.audio

import akka.stream.stage.{GraphStage, GraphStageLogic}
import akka.stream.{Attributes, Inlet, SinkShape}
import uk.org.toot.audio.mixer.AudioMixer

class AkkaTootSink(mixer: AudioMixer) extends GraphStage[SinkShape[Seq[Double]]] {
  val in: Inlet[Seq[Double]] = Inlet("AkkaTootSink")
  override val shape: SinkShape[Seq[Double]] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

    override def preStart(): Unit = pull(in)

    val process = new InHandlerAudioProcess {
      override def onPush(): Unit = {
        println("hasBeenPulled " + hasBeenPulled(in))
        buf = grab(in)
//        if (buf.isEmpty) {
          pull(in)
//        }
      }
    }

    setHandler(in, process)
    mixer.getStrip("1").setInputProcess(process)
  }

}