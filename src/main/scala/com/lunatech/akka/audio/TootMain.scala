package com.lunatech.akka.audio

import javax.sound.midi.ShortMessage
import uk.org.toot.audio.core.ChannelFormat
import uk.org.toot.audio.mixer.{AudioMixer, MixerControls, MixerControlsFactory}
import uk.org.toot.audio.server.JavaSoundAudioServer
import uk.org.toot.audio.system.{AudioSystem, MixerConnectedAudioSystem}
import uk.org.toot.midi.core.{DefaultConnectedMidiSystem, MidiSystem}
import uk.org.toot.synth.channels.valor.ValorSynthControls
import uk.org.toot.synth.synths.multi.{MultiMidiSynth, MultiSynthControls}
import uk.org.toot.synth.{SynthRack, SynthRackControls}

object TootMain extends App {

  testFullSetup()

  def testFullSetup(): Unit = {
    try {
      val audioServer = new JavaSoundAudioServer

      val mainMixerControls = new MixerControls("Mixer")
      MixerControlsFactory.createBusStrips(mainMixerControls, "L-R", ChannelFormat.STEREO, 0)

      val channelCount = 1
      MixerControlsFactory.createChannelStrips(mainMixerControls, channelCount)

      val audioMixer = new AudioMixer(mainMixerControls, audioServer)
      val audioSystem = new MixerConnectedAudioSystem(audioMixer)

      val defaultAudioOutput = audioServer.openAudioOutput(
        audioServer.getAvailableOutputNames.get(0),
        "Default Audio Device"
      )

      audioMixer.getMainBus.setOutputProcess(defaultAudioOutput)

      audioServer.setClient(audioMixer)
      audioServer.start()

      val multiMidiSynth = insertSynth(new DefaultConnectedMidiSystem, audioSystem)

      val midiInput = multiMidiSynth.getMidiInputs.get(0)
      midiInput.transport(new ShortMessage(ShortMessage.NOTE_ON, 80, 127), 0)
      Thread.sleep(1000)

      audioServer.stop()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  private def insertSynth(midiSystem: MidiSystem, audioSystem: AudioSystem): MultiMidiSynth = {
    val synthRackControls = new SynthRackControls(1)
    val synthRack = new SynthRack(synthRackControls, midiSystem, audioSystem)
    val multiSynthControls = new MultiSynthControls

    synthRackControls.setSynthControls(0, multiSynthControls)
    multiSynthControls.setChannelControls(0, new ValorSynthControls)
    synthRack.getMidiSynth(0).asInstanceOf[MultiMidiSynth]
  }
}
