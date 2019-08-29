package com.redis.cluster

import com.redis.api._

/**
 * Methods that are currently missing implementation in cluster mode can not be tested
 * Should be removed at some point
 */
trait ClusterUnimplementedMethods
  extends BaseApiSpec
    with EvalApiSpec
    //    with GeoApiSpec
    //    with HyperLogLogApiSpec
    with HashApiSpec
    with ListApiSpec
    with NodeApiSpec
    with SetApiSpec
    with SortedSetApiSpec
    with StringApiSpec {

  override protected def r: AutoCloseable
    with BaseApi
    with EvalApi
    //    with GeoApi
    //    with HyperLogLogApi
    with HashApi
    with ListApi
    with NodeApi
    with SetApi
    with SortedSetApi
    with StringApi

  override protected def getConfig(): Unit = ()

  override protected def setConfig(): Unit = ()

  override protected def sort(): Unit = ()

  override protected def sortNStore(): Unit = ()

  override protected def bitop(): Unit = ()

}
