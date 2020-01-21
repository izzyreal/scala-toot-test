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

      val midiInput = multiMidiSynth.getMidiInputs.get(0)
      for (noteNumber <- 40 to 100) {
        midiInput.transport(new ShortMessage(ShortMessage.NOTE_ON, noteNumber, 127), 0)
        Thread.sleep(10)
        midiInput.transport(new ShortMessage(ShortMessage.NOTE_OFF, noteNumber, 0), 0)
        Thread.sleep(10)
      }
      for (noteNumber <- 100 to 40 by -1) {
        midiInput.transport(new ShortMessage(ShortMessage.NOTE_ON, noteNumber, 127), 0)
        Thread.sleep(10)
        midiInput.transport(new ShortMessage(ShortMessage.NOTE_OFF, noteNumber, 0), 0)
        Thread.sleep(10)
      }

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
