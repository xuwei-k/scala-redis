package com.redis.cluster


class RedisShardsSpec extends CommonRedisClusterSpec[String] {

  override def rProvider(): SuperCluster = new RedisShards(nodes) {
    val keyTag = Some(RegexKeyTag)
  }

}
