import javax.sound.midi.ShortMessage
import uk.org.toot.audio.core.ChannelFormat
import uk.org.toot.audio.mixer.{AudioMixer, MixerControls, MixerControlsFactory}
import uk.org.toot.audio.server.JavaSoundAudioServer
import uk.org.toot.audio.system.{AudioSystem, MixerConnectedAudioSystem}
import uk.org.toot.midi.core.{DefaultConnectedMidiSystem, MidiSystem}
import uk.org.toot.synth.channels.valor.ValorSynthControls
import uk.org.toot.synth.synths.multi.{MultiMidiSynth, MultiSynthControls}
import uk.org.toot.synth.{SynthRack, SynthRackControls}

object Main extends App {
  private val audioServer = new JavaSoundAudioServer

  private var multiMidiSynth: MultiMidiSynth = _

  testFullSetup()

  def testFullSetup(): Unit = {
    try {
      val mainMixerControls = new MixerControls("Mixer")
      MixerControlsFactory.createBusStrips(mainMixerControls, "L-R", ChannelFormat.STEREO, 0)
      val channelCount = 32
      MixerControlsFactory.createChannelStrips(mainMixerControls, channelCount)
      val audioMixer = new AudioMixer(mainMixerControls, audioServer)
      val audioSystem = new MixerConnectedAudioSystem(audioMixer)
      audioSystem.setAutoConnect(true)
      val defaultAudioOutput = audioServer.openAudioOutput(audioServer.getAvailableOutputNames.get(0), "Default Audio Device")
      audioMixer.getMainBus.setOutputProcess(defaultAudioOutput)
      val midiSystem = new DefaultConnectedMidiSystem
      insertSynth(midiSystem, audioSystem)
      audioServer.setClient(audioMixer)
      audioServer.start()
      multiMidiSynth.getMidiInputs.get(0).transport(new ShortMessage, 0)
      Thread.sleep(1000)
      audioServer.stop()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  private def insertSynth(midiSystem: MidiSystem, audioSystem: AudioSystem): Unit = {
    val multiSynthControls = new MultiSynthControls
    val synthRackControls = new SynthRackControls(1)
    val synthRack = new SynthRack(synthRackControls, midiSystem, audioSystem)
    synthRackControls.setSynthControls(0, multiSynthControls)
    multiMidiSynth = synthRack.getMidiSynth(0).asInstanceOf[MultiMidiSynth]
    multiSynthControls.setChannelControls(0, new ValorSynthControls)
  }
}
