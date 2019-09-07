package com.redis

import scala.collection.mutable

package object cluster {

  /**
   * a level of abstraction for each node decoupling it from the address. A node is now identified
   * by a name, so functions like <tt>replaceServer</tt> works seamlessly.
   */
  case class ClusterNode(nodename: String, host: String, port: Int, database: Int = 0, maxIdle: Int = 8, secret: Option[Any] = None, timeout: Int = 0) {
    assert(nodename.nonEmpty)

    override def toString: String = nodename
  }

  class IdentifiableRedisClientPool(val node: ClusterNode)
    extends RedisClientPool(node.host, node.port, node.maxIdle, node.database, node.secret, node.timeout) {
    override def toString: String = node.nodename
  }

  protected[cluster] def createSet[T](): mutable.Set[T] = {
    import scala.collection.JavaConverters._
    java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap[T, java.lang.Boolean]).asScala
  }

}
