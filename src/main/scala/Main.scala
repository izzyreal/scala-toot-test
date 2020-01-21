import javax.sound.midi.ShortMessage
import uk.org.toot.audio.core.ChannelFormat
import uk.org.toot.audio.mixer.{AudioMixer, MixerControls, MixerControlsFactory}
import uk.org.toot.audio.server.{AudioServerServices, JavaSoundAudioServer}
import uk.org.toot.audio.system.MixerConnectedAudioSystem
import uk.org.toot.midi.core.DefaultConnectedMidiSystem
import uk.org.toot.service.ServiceDescriptor
import uk.org.toot.synth.channels.pluck.PluckSynthControls
import uk.org.toot.synth.channels.valor.ValorSynthControls
import uk.org.toot.synth.synths.multi.MultiSynthControls
import uk.org.toot.synth.{SynthControls, SynthRack, SynthRackControls, SynthServices}

object Main extends App {
  AudioServerServices.scan()

  import java.util

  import uk.org.toot.audio.server.{AudioServer, AudioServerServices}
  import uk.org.toot.service.ServiceVisitor

  val serverNames = new util.ArrayList[String]
  val modelNames = new util.ArrayList[String]

  AudioServerServices.scan()
  AudioServerServices.accept(new ServiceVisitor() {
    override def visitDescriptor(d: ServiceDescriptor): Unit = {
      serverNames.add(d.getName)
      modelNames.add(d.getDescription)
    }
  }, classOf[AudioServer])

  SynthServices.scan()
  SynthServices.accept(new ServiceVisitor() {
    override def visitDescriptor(d: ServiceDescriptor): Unit = {
      println("Synth name: " + d.getName)
      println("Synth description: " + d.getDescription)
    }
  }, classOf[SynthControls])

  val nMixerChans = 32
  val mixerControls = new MixerControls("Mixer")
  mixerControls.createFxBusControls("FX#1", null)
  mixerControls.createFxBusControls("FX#2", null)
  mixerControls.createFxBusControls("FX#3", null)
  mixerControls.createAuxBusControls("Aux#1", ChannelFormat.MONO)
  mixerControls.createAuxBusControls("Aux#2", ChannelFormat.QUAD)
  MixerControlsFactory.createBusStrips(mixerControls, "L-R", ChannelFormat.STEREO, 2)
  MixerControlsFactory.createGroupStrips(mixerControls, 2)
  MixerControlsFactory.createChannelStrips(mixerControls, nMixerChans)
  val audioServer = new JavaSoundAudioServer()
  val mixer = new AudioMixer(mixerControls, audioServer)

  val audioSystem = new MixerConnectedAudioSystem(mixer)
  audioSystem.setAutoConnect(false)


  val synthRackControls = new SynthRackControls(1)

  val midiSystem = new DefaultConnectedMidiSystem()

  val synthRack = new SynthRack(synthRackControls, midiSystem, audioSystem)

  val msc = new MultiSynthControls()
  synthRackControls.setSynthControls(0, msc)

  for (i <- 0 until 16) {
    msc.setChannelControls(i, new ValorSynthControls())
  }

  val synth = synthRack.getMidiSynth(0)
  midiSystem.getMidiDevices.get(0).getMidiInputs
  audioServer.start()

  synth.transport(new ShortMessage(), 0)
  midiSystem.getMidiDevices.get(0).getMidiInputs.get(0).transport(new ShortMessage(),0)
  Thread.sleep(1000)

  audioServer.stop()
}
