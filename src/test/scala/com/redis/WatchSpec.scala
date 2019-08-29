package com.redis

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Milliseconds, Seconds => SSeconds, Span}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WatchSpec extends FunSpec with ScalaFutures
                     with Matchers
                     with BeforeAndAfterEach
                     with BeforeAndAfterAll {

  implicit val pc: PatienceConfig = PatienceConfig(Span(5, SSeconds), Span(100, Milliseconds))

  val clients: RedisClientPool = new RedisClientPool("localhost", 6379)

  override def beforeAll(): Unit = {
    clients.withClient(_.flushall)
  }

  override def afterAll(): Unit = {
    clients.close
  }

  describe("watch") {
    it("should fail a transaction if modified from another client") {
      val p1: Future[Option[List[Any]]] = Future {
        clients.withClient { client =>
          client.watch("key")
          client.pipeline { p =>
            p.set("key", "debasish")
            Thread.sleep(50)
            p.get("key")
            p.get("key1")
          }
      }
      }

      val p2: Future[Boolean] = Future {
        clients.withClient { client =>
          Thread.sleep(10)
          client.set("key", "anshin")
        }
      }

      p2.futureValue should equal(true)
      p1.futureValue should equal(None)
    }
  }
}

