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

  describe("geopos") {
    it("should correctly expose coordinates of requested members"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geopos("Sicily", List("Catania", "Palermo", "testFail", "TestTest"))
      out should equal(
        Some(List(
          Some(List(Some("15.08726745843887329"), Some("37.50266842333162032"))),
          Some(List(Some("13.36138933897018433"), Some("38.11555639549629859"))),
          None,
          None))
      )
    }
    it("correctly handle empty requested members"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geopos("Sicily", List("testFail", "TestTest"))
      out should equal(
        Some(List(
          None,
          None))
      )
    }
  }

  describe("geohash") {
    it("should expose correctly the stored hash"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geohash[String]("Sicily", List("Palermo",  "Catania"))
      out should be(Some(List("sqc8b49rny0", "sqdtr74hyu0")))
    }
  }

  describe("geodist"){
    it("should correctly compute the distance between two objects, defaulting to meters"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geodist("Sicily", "Palermo", "Catania", None)
      out.isDefined should be(true)
      out.get should startWith("166274.151") //precision handling ???
    }
    it("should correctly compute the distance between two objects in kilometers"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geodist("Sicily", "Palermo", "Catania", Some("km"))
      out.isDefined should be(true)
      out.get should startWith("166.274") //precision handling ???
    }
    it("should return an empty value if a member can't be found"){
      r.geoadd("Sicily", Seq(("13.361389", "38.115556", "Palermo"), ("15.087269", "37.502669", "Catania")))
      val out = r.geodist("Sicily", "Palermo", "testfail", Some("km"))
      out.isDefined should be(false)
    }
  }
}
