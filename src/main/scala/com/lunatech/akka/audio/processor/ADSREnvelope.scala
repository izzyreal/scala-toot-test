package com.lunatech.akka.audio.processor

import akka.stream.scaladsl.Flow
import uk.org.toot.synth.modules.envelope.EnvelopeGenerator

object ADSREnvelope {
  def apply(env: EnvelopeGenerator) = {
    Flow[Double].statefulMapConcat { () => { sample =>
      val factor = env.getEnvelope(false)
      Iterable(factor * sample)
    }
    }
  }
}
