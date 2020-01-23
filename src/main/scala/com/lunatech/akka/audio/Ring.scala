package com.lunatech.akka.audio

import scala.collection.immutable.Queue

case class Ring[+A] private (capacity: Int, size: Int, queue: Queue[A]) {
  def push[B >: A](b: B): (Option[A], Ring[B]) =
    if (size < capacity) (None, Ring(capacity, size + 1, queue.enqueue(b)))
    else queue.dequeue match {
      case (h, t) => (Some(h), Ring(capacity, size, t.enqueue(b)))
    }

  def pop: (A, Ring[A]) = popOption.getOrElse(throw new NoSuchElementException)

  def popOption: Option[(A, Ring[A])] = queue.dequeueOption.map {
    case (h, t) => (h, Ring(capacity, size - 1, t))
  }
}

object Ring {
  def empty[A](capacity: Int): Ring[A] = Ring(capacity, 0, Queue.empty)

  def apply[A](capacity: Int)(xs: A*): Ring[A] = {
    val elems = if (xs.size <= capacity) xs else xs.takeRight(capacity)
    Ring(capacity, elems.size, Queue(elems: _*))
  }
}