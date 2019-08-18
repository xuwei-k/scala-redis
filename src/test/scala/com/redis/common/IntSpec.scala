package com.redis.common

import com.redis.api.BaseApi
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait IntSpec extends BeforeAndAfterAll with BeforeAndAfterEach {
  that: Suite =>

  val r: BaseApi with AutoCloseable

  override def afterAll: Unit = {
    r.flushall
    r.close()
    super.afterAll()
  }

  override def beforeAll: Unit = {
    super.beforeAll()
    r.flushall
  }

  override def beforeEach: Unit = {
    super.beforeEach()
  }

  override def afterEach: Unit = {
    r.flushdb
    super.afterEach()
  }

}
