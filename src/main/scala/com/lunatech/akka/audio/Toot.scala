package com.lunatech.akka.audio

import uk.org.toot.audio.core.ChannelFormat
import uk.org.toot.audio.mixer.{AudioMixer, MixerControls, MixerControlsFactory}
import uk.org.toot.audio.server.AudioServer

object Toot {

  def mixer(audioServer: AudioServer) = {
    val mainMixerControls = new MixerControls("Mixer")
    MixerControlsFactory.createBusStrips(mainMixerControls, "L-R", ChannelFormat.STEREO, 0)

    val channelCount = 1
    MixerControlsFactory.createChannelStrips(mainMixerControls, channelCount)

    val audioMixer = new AudioMixer(mainMixerControls, audioServer)

    val defaultAudioOutput = audioServer.openAudioOutput(
      audioServer.getAvailableOutputNames.get(0),
      "Default Audio Device"
    )

    audioMixer.getMainBus.setOutputProcess(defaultAudioOutput)
    audioServer.setClient(audioMixer)

    audioMixer
  }

}
