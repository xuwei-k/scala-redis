package com.redis.cluster

import com.redis.serialization.Format
import com.redis.{RedisClient, RedisClientPool, RedisCommand}

import scala.util.{Random, Try}

trait RedisClusterOps extends AutoCloseable {

  protected val r = new Random()

  protected val keyTag: Option[KeyTag]

  protected[cluster] val POINTS_PER_SERVER = 160 // default in libmemcached

  /**
   * get node for the key
   */
  protected[cluster] def nodeForKey(key: Any)(implicit format: Format): RedisClientPool

  protected[cluster] def onAllConns[T](body: RedisClient => T): Iterable[T]

  protected[cluster] def randomNode(): RedisClientPool

  /**
   * add server to internal pool
   */
  def addServer(server: ClusterNode): Try[Unit]

  /**
   * replace a server
   * very useful when you want to replace a server in test mode to one in production mode
   *
   * Use Case: Suppose I have a big list of key/value pairs which are replicated in 2 Redis servers -
   * one having test values for every key and the other having production values for the same set of
   * keys. In a cluster using <tt>replaceServer</tt> I can switch between test mode and production mode
   * without disturbing the hash ring. This gives an additional level of abstraction on the node name
   * decoupling it from the node address.
   */
  def replaceServer(server: ClusterNode): Unit

  /**
   * remove a server
   */
  def removeServer(nodename: String): Unit

  /**
   * list all running servers
   */
  def listServers: List[ClusterNode]

  protected[cluster] def processForKey[T](key: Any)(body: RedisCommand => T)(implicit format: Format): T = {
    nodeForKey(key).withClient(body(_))
  }

  // todo: need some way to combine T
  protected[cluster] def processForKeys[T](keys: List[Any])(body: List[Any] => RedisCommand => T)
                                          (implicit format: Format): Iterable[T] = {
    if (keys.isEmpty) {
      Iterable {
        randomNode().withClient(body(keys))
      }
    } else {
      keys.groupBy(nodeForKey)
        .map { case (rcp, gkeys) =>
          rcp.withClient(body(gkeys))
        }
    }
  }

  protected[cluster] def inSameNode[T](keys: Any*)(body: RedisClient => T)(implicit format: Format): T = {
    val nodes = keys.toList.map(nodeForKey(_))
    if (nodes.forall(_ == nodes.head)) {
      nodes.head.withClient(body(_))
    } else {
      throw new UnsupportedOperationException("can only occur if all keys map to same node")
    }
  }


}
