package com.lunatech.akka.audio.processor

import akka.NotUsed
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, Source, Zip}

object FilterElements {

  object IIRFlow {

    def apply(internalFir: Flow[Double, Double, NotUsed]) = {
      Flow.fromGraph(GraphDSL.create() { implicit b =>
        import GraphDSL.Implicits._

        val start = Source.single(0.0d)
        val concat = b.add(Concat[Double]())

        val zip = b.add(Zip[Double, Double]())
        val bcast = b.add(Broadcast[Double](2))
        val add = b.add(Flow[(Double, Double)].map { case (in, feedback) => in + feedback })

        start ~> concat ~> zip.in1
        zip.out ~> add ~> bcast
        concat.in(1) <~ internalFir <~ bcast.out(0)

        FlowShape[Double, Double](zip.in0, bcast.out(1))
      })
    }
  }

  def buildIIR(stages: Seq[FilterStage]) = {
    require(stages.nonEmpty, "There should be at least one stage in an FIR filter")
    require(stages.forall { case FilterStage(n, _) => n >= 1 }, "A filter stage should have at least a delay of one")
    require(stages.head.delay >= 1, "The first stage in an IIR filter should have at least a delay of two")

    val firstStage = stages.head
    val adaptedStages = FilterStage(firstStage.delay - 1, firstStage.coefficient) +: stages.tail
    IIRFlow(buildFIR(adaptedStages, FirInitialZero()))
  }

  def buildFIR(stages: Seq[FilterStage], initialStage: Flow[Double, (Double, Double), NotUsed] = FirInitial()) = {
    require(stages.nonEmpty, "There should be at least one stage in an FIR filter")
    require(stages.forall { case FilterStage(n, _) => n >= 1 }, "A filter stage should have at least a delay of one")

    val firCoreFLow = (stages foldLeft initialStage) {
      case (flow, FilterStage(delay, coef)) => flow.via(DelayLineFlow(delay, coef))
    }
    firCoreFLow.via(FirSelectOut())
  }

  object FirInitial {
    def apply() = {
      Flow.fromFunction[Double, (Double, Double)] { sample =>
        (sample, sample)
      }
    }
  }

  object FirInitialZero {
    def apply() = {
      Flow.fromFunction[Double, (Double, Double)] { sample =>
        (sample, 0.0d)
      }
    }
  }

  object FirSelectOut {
    def apply() = {
      Flow.fromFunction[(Double, Double), Double] { case (_, out) => out }
    }
  }

  object DelayLineFlow {
    def apply(delay: Int, scaleFactor: Double): Flow[(Double, Double), (Double, Double), NotUsed] = {
      Flow[(Double, Double)].statefulMapConcat { () =>
        val eq = Array.fill(delay)(0.0d)
        var idx = 0

        {
          case (sample, ff) =>
            val delayedSample = eq(idx)
            eq(idx) = sample
            idx = (idx + 1) % delay
            Iterable((delayedSample, ff + delayedSample * scaleFactor))
        }
      }
    }
  }

  object DelayLineFlowAlt {
    def apply(delay: Int, scaleFactor: Double): Flow[(Double, Double), (Double, Double), NotUsed] = {
      Flow[(Double, Double)].statefulMapConcat { () =>
        val eq = MQueue(List.fill(delay)(0.0d): _*)

        {
          case (sample, ff) =>
            eq.enqueue(sample)
            val delayedSample = eq.dequeue()
            Iterable((delayedSample, ff + delayedSample * scaleFactor))
        }
      }
    }
  }

  object TriangleWave {
    def apply(periodSupplier: () => Double, min: Double, max: Double): Source[Double, NotUsed] = {
      val intSource = Source.fromIterator(() => Iterator.from(0))
      intSource.statefulMapConcat { () =>

        val incr = (max - min) / periodSupplier()

        { y =>

          val seg = (y / periodSupplier()) % 2
          val out = if (seg == 0) {
            min + (y % periodSupplier()) * incr
          } else {
            min + (periodSupplier() - y % periodSupplier()) * incr
          }

          Iterable(out)
        }

      }
    }
  }

  object ScaleAndShift {
    def apply(scale: Double, shift: Double) = Flow[Double].map(sample => sample * scale + shift)
  }

  object LFO {
    def apply(phi0: Double) = {
      Flow[Double].statefulMapConcat { () =>
        var xn: Double = 0
        var yn: Double = scala.math.cos(phi0)

        { voltage =>
          val xnPlus1 = xn - yn * voltage
          val ynPlus1 = xn * voltage + yn * (1 - voltage * voltage)
          xn = xnPlus1
          yn = ynPlus1
          Iterable(xnPlus1)
        }

      }
    }
  }

}
