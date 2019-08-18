package com.redis.cluster

trait WithHashRing[T] {

  protected[cluster] val hr: HashRing[T]

}
