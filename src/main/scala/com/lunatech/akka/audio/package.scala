package com.lunatech.akka

package object audio {

  type Seq[+A] = scala.collection.immutable.Seq[A]

  val MQueue = scala.collection.mutable.Queue
  val Iterable = scala.collection.immutable.Iterable

  case class FilterStage(delay: Int, coefficient: Double)

  implicit class FilterStageOps(val s: (Int, Double)) extends AnyVal {
    def toFilterStage: FilterStage = FilterStage(s._1, s._2)
  }

}
