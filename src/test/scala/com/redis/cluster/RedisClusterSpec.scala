package com.redis.cluster

import com.redis.IdentifiableRedisClientPool
import org.scalatest.Assertion

import scala.collection.mutable.ArrayBuffer


class RedisClusterSpec extends CommonRedisClusterSpec[IdentifiableRedisClientPool] {

  override def rProvider(): SuperCluster = new RedisCluster(nodes: _*) {
    val keyTag = Some(RegexKeyTag)
  }

  override def specialClusterCheck(cluster: ArrayBuffer[IdentifiableRedisClientPool], nodename: String): Assertion =
    cluster.find(_.node.nodename.equals(nodename)).get.port should equal(6382)
}
