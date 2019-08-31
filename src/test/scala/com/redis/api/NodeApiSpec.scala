package com.redis.api

import com.redis.common.IntSpec
import org.scalatest.{FunSpec, Matchers}

// todo: prepare NodeApi Spec
trait NodeApiSpec extends FunSpec with Matchers
  with IntSpec {

  override protected def r: BaseApi with StringApi with NodeApi with AutoCloseable

  describe("NodeApiTest") {

    it("should bgsave") {

    }

    it("should shutdown") {

    }

    it("should lastsave") {

    }

    it("should monitor") {

    }

    it("should save") {

    }

    it("should bgrewriteaof") {

    }

    it("should info") {

    }

    it("should slaveof") {

    }

  }
}