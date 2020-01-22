package com.lunatech.akka.audio

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Inlet, SinkShape}
import uk.org.toot.audio.server.JavaSoundAudioServer

object AkkaToot extends App {

  import FilterElements._

  val firFilterStages: List[FilterStage] =
    List((3000, -0.3), (1500, -0.2), (4500, -0.35)).map(_.toFilterStage)

  val firBasedEcho = buildFIR(firFilterStages)

  val waveFileName = "welcome.wav"
  val WaveSource(soundSource, waveSettings) = WaveSourceFromFile(waveFileName)

  val waveOutputFileName = "welcome-out.wav"
  val wavOutputFile = WaveOutputFile(waveOutputFileName, waveSettings)

  implicit val actorSystem = ActorSystem()

  implicit val flowMaterializer = ActorMaterializer()

  val audioServer = new JavaSoundAudioServer
  val mixer = Toot.mixer(audioServer)

  val in: Inlet[Seq[Double]] = Inlet("AkkaTootAudioProcess")
  val shape: SinkShape[Seq[Double]] = SinkShape(in)

  val akkaTootBridge = new GraphStageLogicAudioProcess(shape)

  val graphStage = new AkkaTootGraphStage(shape, akkaTootBridge)

  mixer.getStrip("1").setInputProcess(akkaTootBridge)

  audioServer.start()

  val flow =
    soundSource
      .via(firBasedEcho)
      .grouped(32768)
      .runWith(graphStage)

}
