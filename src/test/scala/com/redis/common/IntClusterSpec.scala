package com.redis.common

import com.redis.cluster.ClusterNode
import com.redis.serialization.Format
import org.scalatest.Suite

trait IntClusterSpec extends IntSpec {
  that: Suite =>

  protected val nodes: List[ClusterNode] = List(
    ClusterNode("node1", "localhost", 6379),
    ClusterNode("node2", "localhost", 6380),
    ClusterNode("node3", "localhost", 6381),
    ClusterNode("node4", "localhost", 6382)
  )

  def formattedKey(key: Any)(implicit format: Format): Array[Byte] = {
    format(key)
  }
}
