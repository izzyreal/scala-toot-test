package com.lunatech.akka

import java.io.File

import akka.NotUsed
import akka.stream.scaladsl.Source
import uk.co.labbookpages.WavFile

package object audio {

  type Seq[+A] = scala.collection.immutable.Seq[A]

  val MQueue = scala.collection.mutable.Queue
  val Iterable = scala.collection.immutable.Iterable

  case class FilterStage(delay: Int, coefficient: Double)

  implicit class FilterStageOps(val s: (Int, Double)) extends AnyVal {
    def toFilterStage: FilterStage = FilterStage(s._1, s._2)
  }

  object WaveSourceFromFile {
    def apply(wavFileName: String): Source[Double, NotUsed] = {
      val BUFSIZE = 256
      val wavFile = WavFile.openWavFile(new File(wavFileName))
      val numChannels = wavFile.getNumChannels
      val numFrames = wavFile.getNumFrames
      val sampleRate = wavFile.getSampleRate
      println(s"Number of channels = $numChannels, number of frames: $numFrames, sampleRate: $sampleRate")
      val buffer = new Array[Double](256 * numChannels)

      @scala.annotation.tailrec
      def readFrames(wavFile: WavFile, buf: Vector[Double] = Vector.empty[Double]): Vector[Double] = {
        wavFile.readFrames(buffer, BUFSIZE) match {
          case 0 =>
            buf
          case BUFSIZE =>
            readFrames(wavFile, buf ++ buffer.toVector)
          case n =>
            buf ++ buffer.toVector.take(n)
        }
      }

      val source = Source(readFrames(wavFile))
      wavFile.close()
      println(s"Source audio from $wavFileName")
      source
    }
  }

}
