package com.redis

trait PingOperations {
  self: Redis =>

  def ping(): Option[String] = send("PING")(asString)

}
