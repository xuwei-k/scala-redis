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

  override def lastsave: Option[Long] =
    onAllConns(_.lastsave).max

  override def info: Option[String] = {
    val e = onAllConns(_.info)
    if (e.isEmpty) {
      None
    } else {
      Some(e.flatten.mkString(","))
    }
  }

  override def monitor: Boolean =
    onAllConns(_.monitor).forall(_ == true)

  // todo: implement
  override def slaveof(options: Any): Boolean = ???
}
