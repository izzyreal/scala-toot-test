package com.lunatech.akka.audio

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import uk.org.toot.audio.server.JavaSoundAudioServer

import scala.concurrent.duration._

object Main extends App {

  import FilterElements._

  val firFilterStages: List[FilterStage] =
    List((3000, -0.3), (1500, -0.2), (4500, -0.35)).map(_.toFilterStage)

  val firBasedEcho = buildFIR(firFilterStages)

  //  val source = WavSource("welcome.wav")

  val source = new StreamingWavSource

  implicit val actorSystem = ActorSystem()

  implicit val flowMaterializer = ActorMaterializer()

  val audioServer = new JavaSoundAudioServer
  val mixer = Toot.mixer(audioServer)

  val sink = new AkkaTootSink(mixer)

  audioServer.start()

  val flow =
    Source.fromGraph(source)
      .via(firBasedEcho)
      .throttle(44100, 1.second)
      .grouped(1000)
      .runWith(sink)

}
