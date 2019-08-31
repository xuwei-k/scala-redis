package com.redis.api

trait ApiSpec
  extends BaseApiSpec
    with EvalApiSpec
    with GeoApiSpec
    with HashApiSpec
    with HyperLogLogApiSpec
    with ListApiSpec
    with NodeApiSpec
    with SetApiSpec
    with SortedSetApiSpec
    with StringApiSpec {

  override protected def r: AutoCloseable
    with BaseApi
    with EvalApi
    with GeoApi
    with HashApi
    with HyperLogLogApi
    with ListApi
    with NodeApi
    with SetApi
    with SortedSetApi
    with StringApi

}

