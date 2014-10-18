package com.redis

import serialization._

trait HyperLogLogOperations { self: Redis =>
  // PFADD (>= 2.XXX)
  // Add a value to the hyperloglog
  def pfadd(key: Any, value: Any, values: Any*): Long =
    send("PFADD", List(key, value) ::: values.toList)(asLong).get

  def pfcount(keys: Any*): Long =
    send("PFCOUNT", keys.toList)(asLong).get

  def pfmerge(destination: Any, sources: Any*) =
    send("PFMERGE", List(destination) ::: sources.toList)(asBoolean)
}
