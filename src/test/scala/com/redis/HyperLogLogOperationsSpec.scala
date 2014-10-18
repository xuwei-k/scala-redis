package com.redis

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class HyperLogLogOperationsSpec extends FunSpec
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

  describe("pfadd") {
    it("should return one for changed estimated cardinality") {
      r.pfadd("hll-updated-cardinality", "value1") should equal(1)
    }

    it("should return zero for unchanged estimated cardinality") {
      r.pfadd("hll-nonupdated-cardinality", "value1")
      r.pfadd("hll-nonupdated-cardinality", "value1") should equal(0)
    }

    it("should return one for variadic values and estimated cardinality changes") {
      r.pfadd("hll-variadic-cardinality", "value1")
      r.pfadd("hll-variadic-cardinality", "value1", "value2") should equal(1)
    }
  }

  describe("pfcount") {
    it("should return zero for an empty") {
      r.pfcount("hll-empty") should equal(0)
    }

    it("should return estimated cardinality") {
      r.pfadd("hll-1", "value1") should equal(1)
      r.pfcount("hll-1") should equal(1)
    }
  }
}
