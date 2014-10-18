package com.redis

import serialization._

trait HyperLogLogOperations { self: Redis =>
  // PFADD (>= 2.XXX)
  // Add a value to the hyperloglog
  def pfadd(key: Any, value: Any, values: Any*): Long =
    send("PFADD", List(key, value) ::: values.toList)(asLong).get

  def pfcount(key: Any): Long =
    send("PFCOUNT", List(key))(asLong).get
}
