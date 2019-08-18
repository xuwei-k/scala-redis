package com.redis

import com.redis.api.HyperLogLogApi

trait HyperLogLogOperations extends HyperLogLogApi {
  self: Redis =>

  override def pfadd(key: Any, value: Any, values: Any*): Option[Long] =
    send("PFADD", List(key, value) ::: values.toList)(asLong)

  override def pfcount(keys: Any*): Option[Long] =
    send("PFCOUNT", keys.toList)(asLong)

  override def pfmerge(destination: Any, sources: Any*): Boolean =
    send("PFMERGE", List(destination) ::: sources.toList)(asBoolean)
}
