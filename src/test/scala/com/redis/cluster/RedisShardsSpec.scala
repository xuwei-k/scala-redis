package com.redis.cluster

import com.redis.common.IntClusterSpec
import org.scalatest.FunSpec


class RedisShardsSpec extends FunSpec
  with IntClusterSpec
  with ClusterUnimplementedMethods
  with ClusterIncompatibleTests
  with CommonRedisClusterSpec[String] {

  override def rProvider() =
    new RedisShards(nodes) {
    val keyTag = Some(RegexKeyTag)
  }

}
