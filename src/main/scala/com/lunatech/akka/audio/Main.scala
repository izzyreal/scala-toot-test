package com.lunatech.akka.audio

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import uk.org.toot.audio.server.JavaSoundAudioServer

import scala.concurrent.duration._

object Main extends App {

  import FilterElements._

  val firFilterStages: List[FilterStage] =
    List((15000, -0.35), (30000, -0.25), (45000, -0.15), (60000, -0.05)).map(_.toFilterStage)

  val firBasedEcho = buildFIR(firFilterStages)

  val source = new StreamingWavSource

  implicit val actorSystem = ActorSystem()

  implicit val flowMaterializer = ActorMaterializer()

  val audioServer = new JavaSoundAudioServer
  val mixer = Toot.mixer(audioServer)

  val sink = new AkkaTootSink(mixer)

  audioServer.start()
  println("AudioServer sample rate: " + audioServer.getSampleRate)

  //  val flow =
  //    Source.fromGraph(source)
  //      .via(firBasedEcho)
  //        .throttle(44100, 1.second)
  //        .grouped(88)
  //        .runWith(sink)


  var period = 0.1

  def periodSupplier(): Double = period

  def increasePeriod(increment: Double): Unit = period += increment

  val flow1 =
    TriangleWave(periodSupplier, 0.1, 0.2)
      .via(LFO(1))
      .via(ScaleAndShift(0.1, 0))
      .via(firBasedEcho)
      .throttle(44100, 1.second)
      .grouped(100)
      .runWith(sink)

  val ui = new UI
  ui.visible = true
  while (true) {
    ui.repaint()
    Thread.sleep(1)
  }
}
