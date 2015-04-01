package com.redis

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class PingOperationsSpec extends FunSpec
                        with Matchers
                        with BeforeAndAfterEach
                        with BeforeAndAfterAll {

  val r = new RedisClient("localhost", 6379)

  override def beforeEach = {
  }

  override def afterEach = {
    r.flushdb
  }

  override def afterAll = {
    r.disconnect
  }

  describe("ping") {
    it("should return pong") {
      r.ping().get should equal("PONG")
    }
  }
}
