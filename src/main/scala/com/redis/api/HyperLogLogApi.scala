package com.redis.api

trait HyperLogLogApi {

  /**
   * Add a value to the hyperloglog (>= 2.8.9)
   */
  def pfadd(key: Any, value: Any, values: Any*): Option[Long]

  /**
   * Get the estimated cardinality from one or more keys (>= 2.8.9)
   */
  def pfcount(keys: Any*): Option[Long]

  /**
   * Merge existing keys (>= 2.8.9)
   */
  def pfmerge(destination: Any, sources: Any*): Boolean
}