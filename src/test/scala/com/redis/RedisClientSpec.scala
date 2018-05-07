package com.redis

import java.net.URI

import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class RedisClientSpec extends FunSpec
  with Matchers {

  describe("constructor") {
    it("should parse the db-number from the path of connection uri") {
      val client = new RedisClient(new URI("redis://localhost:6379/4"))
      client.database shouldBe 4
    }

    it("should default to db 0 for connection uri without db-number") {
      val client = new RedisClient(new URI("redis://localhost:6379"))
      client.database shouldBe 0
    }
  }

  describe("toString") {
    it("should include the db-number") {
      new RedisClient("localhost", 6379, 1).toString shouldBe "localhost:6379/1"
    }
  }

  describe("test subscribe") {
    val r = new RedisClient("localhost", 6379)
  
    println(r.get("vvl:qm"))
  
    r.subscribe("vvl.qm") { m =>
      println(m)
    }
  
    Thread.sleep(3000)
  
    r.unsubscribe("vvl.qm")
  
    Thread.sleep(3000)
  
    println(r.get("vvl:qm"))
  
    r.subscribe("vvl.qm") { m =>
      println(m)
    }
  
    Thread.sleep(3000)
  
    r.unsubscribe("vvl.qm")
  
    Thread.sleep(3000)
  
    r.get("vvl:qm")
  }
}
