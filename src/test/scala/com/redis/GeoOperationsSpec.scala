package com.redis
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

/**
  * Created by alexis on 05/09/16.
  */
class GeoOperationsSpec extends FunSpec
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

  describe("geoadd") {
    it("should add values with their coordinates and return the added quantity") {
      val out = r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      out should  be(Some(2))
    }
    it("should not add a value twice") {
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      out should  be(Some(0))
    }
  }

  describe("geohash") {
    it("should expose correctly the stored hash"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geohash[String]("Sicily", List("Palermo",  "Catania"))
      out should be(Some(List("sqc8b49rny0", "sqdtr74hyu0")))
    }
  }
}
