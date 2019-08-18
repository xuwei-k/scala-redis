package com.redis.cluster

import com.redis._
import com.redis.serialization._

abstract class RedisShards(val hosts: List[ClusterNode])
  extends RedisClusterOps
    with WithHashRing[String]
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
  private var clients = hosts.map { h => (h.nodename, new IdentifiableRedisClientPool(h)) } toMap

  // the hash ring will instantiate with the nodes up and added
  override protected[cluster] val hr: HashRing[String] = HashRing[String](hosts.map(_.nodename), POINTS_PER_SERVER)

  override protected[cluster] def nodeForKey(key: Any)(implicit format: Format): RedisClientPool = {
    val bKey = format(key)
    val selectedNode = hr.getNode(keyTag.flatMap(_.tag(bKey.toIndexedSeq)).getOrElse(bKey.toIndexedSeq))
    clients(selectedNode)
  }

  override def addServer(server: ClusterNode): Unit = {
    clients = clients + (server.nodename -> new IdentifiableRedisClientPool(server))
    hr addNode server.nodename
  }

  override def replaceServer(server: ClusterNode): Unit = {
    if (clients.contains(server.nodename)) {
      clients(server.nodename).close
      clients = clients - server.nodename
    }
    clients = clients + (server.nodename -> new IdentifiableRedisClientPool(server))
  }

  override def removeServer(nodename: String): Unit = {
    if (clients.contains(nodename)) {
      val pool = clients(nodename)
      pool.close
      clients = clients - nodename
      hr removeNode (nodename)
    }
  }

  def listServers: List[ClusterNode] = {
    clients.values.map(_.node).toList
  }

  override protected[cluster] def onAllConns[T](body: RedisClient => T): Iterable[T] =
    clients.values.map(p => p.withClient { client => body(client) }) // .forall(_ == true)

  def close(): Unit = clients.values.map(_.close)

}
