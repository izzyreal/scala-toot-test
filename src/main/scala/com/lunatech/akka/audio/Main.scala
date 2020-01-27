package com.lunatech.akka.audio

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import uk.org.toot.audio.server.JavaSoundAudioServer

import scala.concurrent.duration._

object Main extends App {

  import FilterElements._

  val bufferSize = 32768

  val firFilterStages: List[FilterStage] =
    List((15000, -0.35), (30000, -0.25), (45000, -0.15), (60000, -0.05)).map(_.toFilterStage)

  val firBasedEcho = buildFIR(firFilterStages)

  val wavSource = new StreamingWavSource

  implicit val actorSystem = ActorSystem()

  implicit val flowMaterializer = ActorMaterializer()

  val sink = new JavaSoundSink

//  val flow =
//    Source.fromGraph(wavSource)
//      .via(firBasedEcho)
//      .grouped(bufferSize)
//      .runWith(sink)
//
  var period = 0.1
//
  def periodSupplier(): Double = period
//
  def increasePeriod(increment: Double): Unit = period += increment
//
  val flow1 =
    TriangleWave(periodSupplier, 0.1, 0.2)
      .via(LFO(1))
      .via(ScaleAndShift(0.1, 0.1))
      .via(firBasedEcho)
      .grouped(bufferSize)
      .runWith(sink)

  val ui = new UI
  ui.visible = true
  while (true) {
    ui.repaint()
    Thread.sleep(1)
  }
}
