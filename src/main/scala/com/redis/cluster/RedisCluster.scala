package com.redis.cluster

import com.redis._
import com.redis.serialization._

/**
 * Consistent hashing distributes keys across multiple servers. But there are situations
 * like <i>sorting</i> or computing <i>set intersections</i> or operations like <tt>rpoplpush</tt>
 * in redis that require all keys to be collocated on the same server.
 * <p/>
 * One of the techniques that redis encourages for such forced key locality is called
 * <i>key tagging</i>. See <http://code.google.com/p/redis/wiki/FAQ> for reference.
 * <p/>
 * The trait <tt>KeyTag</tt> defines a method <tt>tag</tt> that takes a key and returns
 * the part of the key on which we hash to determine the server on which it will be located.
 * If it returns <tt>None</tt> then we hash on the whole key, otherwise we hash only on the
 * returned part.
 * <p/>
 * redis-rb implements a regex based trick to achieve key-tagging. Here is the technique
 * explained in redis FAQ:
 * <i>
 * A key tag is a special pattern inside a key that, if preset, is the only part of the key
 * hashed in order to select the server for this key. For example in order to hash the key
 * "foo" I simply perform the CRC32 checksum of the whole string, but if this key has a
 * pattern in the form of the characters {...} I only hash this substring. So for example
 * for the key "foo{bared}" the key hashing code will simply perform the CRC32 of "bared".
 * This way using key tags you can ensure that related keys will be stored on the same Redis
 * instance just using the same key tag for all this keys. Redis-rb already implements key tags.
 * </i>
 */
trait KeyTag {
  def tag(key: Seq[Byte]): Option[Seq[Byte]]
}

object RegexKeyTag extends KeyTag {

  val tagStart: Byte = '{'.toByte
  val tagEnd: Byte = '}'.toByte

  override def tag(key: Seq[Byte]): Option[Seq[Byte]] = {
    val start = key.indexOf(tagStart) + 1
    if (start > 0) {
      val end = key.indexOf(tagEnd, start)
      if (end > -1) Some(key.slice(start, end)) else None
    } else None
  }
}

object NoOpKeyTag extends KeyTag {
  def tag(key: Seq[Byte]) = Some(key)
}

/**
 * a level of abstraction for each node decoupling it from the address. A node is now identified
 * by a name, so functions like <tt>replaceServer</tt> works seamlessly.
 */
case class ClusterNode(nodename: String, host: String, port: Int, database: Int = 0, maxIdle: Int = 8, secret: Option[Any] = None, timeout: Int = 0) {
  override def toString: String = nodename
}

abstract class RedisCluster(hosts: ClusterNode*)
  extends RedisClusterOps
    with BaseOps
    with NodeOps
    with StringOps
    with ListOps
    with SetOps
    with SortedSetOps
    // with GeoOps todo: implement GeoApi
    // with EvalOps todo: implement EvalApi
    // with HyperLogLogOps todo: implement HyperLogLogApi
    with HashOps {

  // instantiating a cluster will automatically connect participating nodes to the server
  val clients: List[IdentifiableRedisClientPool] = hosts.toList.map { h =>
    new IdentifiableRedisClientPool(h)
  }

  // the hash ring will instantiate with the nodes up and added
  val hr: HashRing[IdentifiableRedisClientPool] = HashRing[IdentifiableRedisClientPool](clients, POINTS_PER_SERVER)

  override def nodeForKey(key: Any)(implicit format: Format): IdentifiableRedisClientPool = {
    val bKey = format(key)
    hr.getNode(keyTag.flatMap(_.tag(bKey.toIndexedSeq)).getOrElse(bKey.toIndexedSeq))
  }

  override def addServer(server: ClusterNode): Unit = {
    hr addNode new IdentifiableRedisClientPool(server)
  }

  override def replaceServer(server: ClusterNode): Unit = {
    hr replaceNode new IdentifiableRedisClientPool(server) match {
      case Some(clientPool) => clientPool.close
      case None =>
    }
  }

  override def removeServer(nodename: String): Unit = {
    hr.cluster.find(_.node.nodename.equals(nodename)) match {
      case Some(pool) => {
        hr removeNode (pool)
        pool.close
      }
      case None =>
    }
  }

  def listServers: List[ClusterNode] = {
    hr.cluster.map(_.node).toList
  }

  override def onAllConns[T](body: RedisClient => T): Iterable[T] =
    hr.cluster.map(p => p.withClient { client => body(client) }) // .forall(_ == true)

  def close(): Unit = hr.cluster.map(_.close)

}
