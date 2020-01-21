package com.lunatech.akka.audio

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object AkkaMain extends App {

  import FilterElements._

  val firFilterStages: List[FilterStage] =
    List((3000, -0.3), (1500, -0.2), (4500, -0.35)).map(_.toFilterStage)

  val firBasedEcho = buildFIR(firFilterStages)

  val waveFileName = "welcome.wav"
  val WaveSource(soundSource, waveSettings) = WaveSourceFromFile(waveFileName)

  val waveOutputFileName = "welcome-out.wav"
  val wavOutputFile = WaveOutputFile(waveOutputFileName, waveSettings)

  implicit val actorSystem = ActorSystem()

  import actorSystem.dispatcher

  implicit val flowMaterializer = ActorMaterializer()

  val runFlow =
    soundSource
      .via(firBasedEcho)
      .grouped(1000)
      .runForeach(d => wavOutputFile.writeFrames(d.map(_ / 2.0).toArray, d.length))

  runFlow flatMap { _ => actorSystem.terminate() } onComplete { _ => wavOutputFile.close() }
}
