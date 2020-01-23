package com.lunatech.akka.audio

import java.io.File

import akka.NotUsed
import akka.stream.scaladsl.Source
import uk.co.labbookpages.WavFile

object WavSource {
  def apply(fileName: String): Source[Double, NotUsed] = {
    val wavFile = WavFile.openWavFile(new File(fileName))
    val numChannels = wavFile.getNumChannels
    val numFrames = wavFile.getNumFrames
    val sampleRate = wavFile.getSampleRate
    println(s"Number of channels = $numChannels, number of frames: $numFrames, sampleRate: $sampleRate")
    val BufSize = 256
    val buffer = new Array[Double](BufSize * numChannels)

    @scala.annotation.tailrec
    def readFrames(wavFile: WavFile, buf: Vector[Double] = Vector.empty[Double]): Vector[Double] = {
      wavFile.readFrames(buffer, BufSize) match {
        case 0 =>
          buf
        case BufSize =>
          readFrames(wavFile, buf ++ buffer.toVector)
        case n =>
          buf ++ buffer.toVector.take(n)
      }
    }

    val source = Source(readFrames(wavFile))
    wavFile.close()
    println(s"Source audio from $fileName")
    source
  }
}
