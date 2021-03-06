package com.lunatech.akka.audio

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import com.lunatech.akka.audio.processor._
import com.lunatech.akka.audio.sink.JavaSoundSink
import com.lunatech.akka.audio.source.{Sine, Wav}
import com.lunatech.akka.audio.ui.AkkaAudioUI

object Main extends App {

  import com.lunatech.akka.audio.processor.FilterElements._

  val bufferSize = Config.bufferSize

  val firFilterStages: List[FilterStage] =
    List((15000, -0.35), (30000, -0.25), (45000, -0.15), (60000, -0.05)).map(_.toFilterStage)

  val firBasedEcho = buildFIR(firFilterStages)

  implicit val actorSystem = ActorSystem()

  val sink = new JavaSoundSink

//    val source = new Sine
  val source = new Wav

  val flow =
    Source.fromGraph(source)
      .via(source.adsrEnvelope)
      .via(firBasedEcho)
      .grouped(bufferSize)
      .runWith(sink)

  val ui = new AkkaAudioUI(Seq(source))
  while (true) {
    ui.repaint()
    Thread.sleep(1)
  }
}
