package com

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.{Duration, FiniteDuration}

package object redis {

  @deprecated("Use implementation with scala.concurrent.duration.Duration and SetBehaviour", "3.10")
  sealed trait SecondsOrMillis {
    def toDuration: Duration
  }

  case class Seconds(value: Long) extends SecondsOrMillis {
    override def toDuration: Duration = FiniteDuration(value, TimeUnit.SECONDS)
  }

  case class Millis(value: Long) extends SecondsOrMillis {
    override def toDuration: Duration = FiniteDuration(value, TimeUnit.MILLISECONDS)
  }

}
