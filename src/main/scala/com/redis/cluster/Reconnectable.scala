package com.redis.cluster

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.redis.Log
import com.redis.api.BaseApi

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

trait Reconnectable extends Log {
  rc: RedisClusterOps with AutoCloseable with BaseApi with WithHashRing[IdentifiableRedisClientPool] =>

  protected[cluster] val disconnectedNodes: mutable.Set[ClusterNode] = createSet[ClusterNode]()
  protected[cluster] val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  protected lazy val checkIntervalSeconds: Int = 15

  protected[cluster] def disconnectInactive(): Unit = {
    val unreachable = hr.cluster.filterNot(n =>
      Try(n.withClient(_.ping) == pong).getOrElse(false))

    unreachable.foreach { node =>
      info(s"Disconnecting $node")
      disconnectedNodes.add(node.node)
      removeServer(node.node.nodename)
    }

  }

  protected[cluster] def tryToReconnect(): Unit = {
    val reconnected = disconnectedNodes.filter { node =>
      addServer(node) match {
        case Success(_) =>
          true
        case Failure(e) =>
          error(s"Failed to reconnect node [${node.nodename}] to cluster, because [${e.getMessage}]", e)
          false
      }
    }
    reconnected.foreach { node =>
      info(s"Reconnecting $node")
      disconnectedNodes.remove(node)
    }
  }

  scheduler.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = Try {
      debug(s"Nodes in cluster: [${hr.cluster.mkString(",")}]")
      debug(s"Disconnected nodes: [${disconnectedNodes.mkString(",")}]")
      tryToReconnect()
      disconnectInactive()
    }.recover {
      case e => error(e.getMessage, e)
    }
  }, checkIntervalSeconds, checkIntervalSeconds, TimeUnit.SECONDS)

  override def close(): Unit = {
    scheduler.shutdown()
    hr.cluster.foreach(_.close())
  }

}
