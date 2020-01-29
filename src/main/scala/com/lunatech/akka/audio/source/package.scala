package com.lunatech.akka.audio

import akka.stream.scaladsl.Flow
import com.lunatech.akka.audio.processor.Iterable
import uk.org.toot.control.FloatControl
import uk.org.toot.synth.modules.envelope.{EnvelopeControls, EnvelopeGenerator}

package object source {

  // https://www.inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies
  private val baseFreq = 440d
  private val baseNote = 69

  // http://www.techlib.com/reference/musical_note_frequencies.htm
  def freqFromNote(note: Int) = baseFreq * scala.math.pow(2, (note - baseNote) / 12d)

  trait Synth {
    protected var freq = 440d

    private var release = false

    private val envVars = new EnvelopeControls(0, "", 0)

    private val attackControl = envVars.getControls.get(0).asInstanceOf[FloatControl]
    private val decayControl = envVars.getControls.get(1).asInstanceOf[FloatControl]
    private val sustainControl = envVars.getControls.get(2).asInstanceOf[FloatControl]
    private val releaseControl = envVars.getControls.get(3).asInstanceOf[FloatControl]

    envVars.setSampleRate(Config.sampleRate)

    attackControl.setValue(15)
    decayControl.setValue(1000)
    sustainControl.setValue(1)
    releaseControl.setValue(15)

    private val envelopeGenerator = new EnvelopeGenerator(envVars)

    val adsrEnvelope = Flow[Double].mapConcat { sample =>
      val envelope = envelopeGenerator.getEnvelope(release)
      Iterable(envelope * sample)
    }

    def trigger(f: Double) = {
      freq = f
      release = false
      envelopeGenerator.trigger()
    }

    def stop(): Unit = {
      release = true
    }

  }

}
