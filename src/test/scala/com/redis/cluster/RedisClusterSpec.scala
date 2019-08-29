package com.redis.cluster

import com.redis.IdentifiableRedisClientPool
import com.redis.common.IntClusterSpec
import org.scalatest.{Assertion, FunSpec}

import scala.collection.mutable.ArrayBuffer


class RedisClusterSpec extends FunSpec
  with IntClusterSpec
  with ClusterUnimplementedMethods
  with ClusterIncompatibleTests
  with CommonRedisClusterSpec[IdentifiableRedisClientPool] {

  override def rProvider() =
    new RedisCluster(nodes: _*) {
    val keyTag = Some(RegexKeyTag)
  }

  override def specialClusterCheck(cluster: ArrayBuffer[IdentifiableRedisClientPool], nodename: String): Assertion =
    cluster.find(_.node.nodename.equals(nodename)).get.port should equal(6382)
}
