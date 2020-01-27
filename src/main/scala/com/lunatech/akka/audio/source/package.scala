package com.lunatech.akka.audio

import com.lunatech.akka.audio.processor.ADSREnvelope
import uk.org.toot.control.FloatControl
import uk.org.toot.synth.modules.envelope.{EnvelopeControls, EnvelopeGenerator}

package object source {

  // https://www.inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies
  private val baseFreq = 440d
  private val baseNote = 69

  // http://www.techlib.com/reference/musical_note_frequencies.htm
  def freqFromNote(note: Int) = baseFreq * scala.math.pow(2, (note - baseNote) / 12d)

  trait Synth {
    var freq = 440d

    val envVars = new EnvelopeControls(0, "", 0)
    envVars.setSampleRate(Config.sampleRate)

    val attack = envVars.getControls.get(0).asInstanceOf[FloatControl]
    val decay = envVars.getControls.get(1).asInstanceOf[FloatControl]
    val sustain = envVars.getControls.get(2).asInstanceOf[FloatControl]
    val release = envVars.getControls.get(3).asInstanceOf[FloatControl]

    attack.setValue(15)
    decay.setValue(1000)
    sustain.setValue(0)
    release.setValue(1000)

    val env = new EnvelopeGenerator(envVars)

    val adsrEnvelope = ADSREnvelope(env)

    def trigger(f: Double) = {
      freq = f
      env.trigger()
    }
  }

}
