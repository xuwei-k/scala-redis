package com.redis.ds

import com.redis.RedisCommand
import com.redis.common.RedisDocker
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec, Matchers}

import scala.concurrent.Future

class BlockingDequeSpec extends FunSpec with RedisDocker
  with Matchers
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  describe("blocking poll") {
    it("should pull out first element") {
      beforeAndAfter { (r1, r2) =>
        val pollV = Future {
          r1.poll.get
        }

        r2.size should equal(0)
        r2.addFirst("foo")
        pollV.futureValue should equal("foo")
      }
    }
  }

  describe("blocking poll with pollLast") {
    it("should pull out first element") {
      beforeAndAfter { (r1, r2) =>
        val pollV: Future[String] = Future {
          r1.pollLast.get
        }

        r2.size should equal(0)
        r2.addFirst("foo")
        pollV.futureValue should equal("foo")
      }
    }
  }

  type BlockingDeque = RedisDeque[String] with RedisCommand

  private def beforeAndAfter(t: (BlockingDeque, BlockingDeque) => Unit): Unit = {
    val r1 = createClient()
    val r2 = createClient()

    t(r1, r2)

    r1.flushall
    r1.close()
    r2.close()
  }

  private def createClient(): BlockingDeque =
    new RedisDequeClient(redisContainerHost, redisContainerPort).getDeque("btd", blocking = true, timeoutInSecs = 30)

}
