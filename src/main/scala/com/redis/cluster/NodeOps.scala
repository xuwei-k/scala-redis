package com.redis.cluster

import com.redis.api.NodeApi

trait NodeOps extends NodeApi {
  rc: RedisClusterOps =>

  override def save: Boolean =
    onAllConns(_.save) forall (_ == true)

  override def bgsave: Boolean =
    onAllConns(_.bgsave) forall (_ == true)

  override def shutdown: Boolean =
    onAllConns(_.shutdown) forall (_ == true)

  override def bgrewriteaof: Boolean =
    onAllConns(_.bgrewriteaof) forall (_ == true)

  // todo: implement
  override def lastsave: Option[Long] = ???

  // todo: implement
  override def info: Option[String] = ???

  // todo: implement
  override def monitor: Boolean = ???

  // todo: implement
  override def slaveof(options: Any): Boolean = ???
}
