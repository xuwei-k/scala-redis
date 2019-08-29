package com.redis

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

class PoolSpec extends FunSpec
               with Matchers
               with BeforeAndAfterEach
               with BeforeAndAfterAll {

  implicit val clients = new RedisClientPool("localhost", 6379)

  override def beforeEach = {
  }

  override def afterEach = clients.withClient{
    client => client.flushdb
  }

  override def afterAll = {
    clients.withClient{ client => client.disconnect }
    clients.close
  }

  def lp(msgs: List[String]) = {
    clients.withClient {
      client => {
        msgs.foreach(client.lpush("list-l", _))
        client.llen("list-l")
      }
    }
  }

  def rp(msgs: List[String]) = {
    clients.withClient {
      client => {
        msgs.foreach(client.rpush("list-r", _))
        client.llen("list-r")
      }
    }
  }

  def set(msgs: List[String]) = {
    clients.withClient {
      client => {
        var i = 0
        msgs.foreach { v =>
          client.set("key-%d".format(i), v)
          i += 1
        }
        Some(1000L)
      }
    }
  }

  describe("pool test") {
    it("should distribute work amongst the clients") {
      val l = (0 until 5000).map(_.toString).toList
      val fns = List[List[String] => Option[Long]](lp, rp, set)
      val tasks = fns map (fn => Future { fn(l) })
      val results = Await.result(Future.sequence(tasks), 60 seconds)
      results should equal(List(Some(5000), Some(5000), Some(1000)))
    }
  }

  def leftp(msgs: List[String]) = {
    clients.withClient {
      client => {
        val ln = new scala.util.Random().nextString(10)
        msgs.foreach(client.lpush(ln, _))
        val len = client.llen(ln)
println(len)
        len
      }
    }
  }

  import Bench._

  private val amountMultiplier = 1 // unit test multiplier
  // private val amountMultiplier = 1000 // benchmark multiplier

  describe("list load test 1") {
    it(s"should distribute work amongst the clients for ${400 * amountMultiplier} list operations") {
      val (s, o, r) = listLoad(2 * amountMultiplier)
      println(s"${400 * amountMultiplier} list operations: elapsed = " + s + " per sec = " + o)
      r.size should equal(100)
    }
  }

  describe("list load test 2") {
    it(s"should distribute work amongst the clients for ${1000 * amountMultiplier} list operations") {
      val (s, o, r) = listLoad(5 * amountMultiplier)
      println(s"${1000 * amountMultiplier} list operations: elapsed = " + s + " per sec = " + o)
      r.size should equal(100)
    }
  }

  describe("list load test 3") {
    it(s"should distribute work amongst the clients for ${2000 * amountMultiplier} list operations") {
      val (s, o, r) = listLoad(10 * amountMultiplier)
      println(s"${2000 * amountMultiplier} list operations: elapsed = " + s + " per sec = " + o)
      r.size should equal(100)
    }
  }

  describe("incr load test 1") {
    it(s"should distribute work amongst the clients for ${400 * amountMultiplier} incr operations") {
      val (s, o, r) = incrLoad(2 * amountMultiplier)
      println(s"${400 * amountMultiplier} incr operations: elapsed = " + s + " per sec = " + o)
      r.size should equal(100)
    }
  }

  describe("incr load test 2") {
    it(s"should distribute work amongst the clients for ${1000 * amountMultiplier} incr operations") {
      val (s, o, r) = incrLoad(5 * amountMultiplier)
      println(s"${1000 * amountMultiplier} incr operations: elapsed = " + s + " per sec = " + o)
      r.size should equal(100)
    }
  }

  describe("incr load test 3") {
    it(s"should distribute work amongst the clients for ${2000 * amountMultiplier} incr operations") {
      val (s, o, r) = incrLoad(10 * amountMultiplier)
      println(s"${2000 * amountMultiplier} incr operations: elapsed = " + s + " per sec = " + o)
      r.size should equal(100)
    }
  }
}
