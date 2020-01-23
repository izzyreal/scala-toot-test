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

    println(s"Source audio from $fileName")
    println(s"Number of channels = $numChannels, number of frames: $numFrames, sampleRate: $sampleRate")

    val BufSize = 256
    val readBuffer = new Array[Double](BufSize * numChannels)

    @scala.annotation.tailrec
    def readFrames(accumulator: Array[Double]): Array[Double] = {
      wavFile.readFrames(readBuffer, BufSize) match {
        case 0 =>
          accumulator
        case BufSize =>
          readFrames(accumulator ++ readBuffer)
        case n =>
          accumulator ++ readBuffer.take(n)
      }
    }

    val source = Source(readFrames(Array.empty))
    wavFile.close()
    source
  }
}
