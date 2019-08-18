package com.redis.cluster

import com.redis.common.IntSpec
import com.redis.serialization.Format
import org.scalatest.Suite

trait IntClusterSpec extends IntSpec {
  that: Suite =>

  val nodes = List(
    ClusterNode("node1", "localhost", 6379),
    ClusterNode("node2", "localhost", 6380),
    ClusterNode("node3", "localhost", 6381)
  )

  def formattedKey(key: Any)(implicit format: Format): Array[Byte] = {
    format(key)
  }
}
