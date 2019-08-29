package com.redis

import java.net.URI

import com.redis.api.ApiSpec
import org.scalatest.FunSpec
import org.scalatest.Matchers

class RedisClientSpec extends FunSpec
  with Matchers with ApiSpec {

  override protected lazy val r: RedisClient =
    new RedisClient(redisContainerHost, redisContainerPort)

  private lazy val redisUrl = s"$redisContainerHost:$redisContainerPort"

  describe("constructor") {
    it("should parse the db-number from the path of connection uri") {
      val client = new RedisClient(new URI(s"redis://$redisUrl/4"))
      client.database shouldBe 4
      client.close()
    }

    it("should default to db 0 for connection uri without db-number") {
      val client = new RedisClient(new URI(s"redis://$redisUrl"))
      client.database shouldBe 0
      client.close()
    }
  }

  describe("toString") {
    it("should include the db-number") {
      val c = new RedisClient(redisContainerHost, redisContainerPort, 1)
      c.toString shouldBe s"$redisUrl/1"
      c.close()
    }
  }

  describe("test subscribe") {
    it("should subscribe") {
    val r = new RedisClient(redisContainerHost, redisContainerPort)
  
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
    r.close()
  }}
}
