package com.redis.cluster

import com.redis.api.StringApi
import com.redis.api.StringApi.{Always, SetBehaviour}
import com.redis.serialization.{Format, Parse}

import scala.concurrent.duration.Duration

trait StringOps extends StringApi {
  rc: RedisClusterOps =>

  override def set(key: Any, value: Any, whenSet: SetBehaviour = Always, expire: Duration = null)
                  (implicit format: Format): Boolean =
    processForKey(key)(_.set(key, value, whenSet, expire))

  override def get[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.get(key))

  override def getset[A](key: Any, value: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.getset(key, value))

  override def setnx(key: Any, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.setnx(key, value))

  override def setex(key: Any, expiry: Long, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.setex(key, expiry, value))

  override def psetex(key: Any, expiryInMillis: Long, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.psetex(key, expiryInMillis, value))

  override def incr(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.incr(key))

  override def incrby(key: Any, increment: Long)(implicit format: Format): Option[Long] =
    processForKey(key)(_.incrby(key, increment))

  override def incrbyfloat(key: Any, increment: Float)(implicit format: Format): Option[Float] =
    processForKey(key)(_.incrbyfloat(key, increment))

  override def decr(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.decr(key))

  override def decrby(key: Any, increment: Long)(implicit format: Format): Option[Long] =
    processForKey(key)(_.decrby(key, increment))

  override def mget[A](key: Any, keys: Any*)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] = {
    val keylist = (key :: keys.toList)
    val kvs = for {
      (n, ks) <- keylist.groupBy(nodeForKey)
      vs <- n.withClient(_.mget[A](ks.head, ks.tail: _*).toList)
      kv <- (ks).zip(vs)
    } yield kv
    Some(keylist.map(kvs))
  }

  override def mset(kvs: (Any, Any)*)(implicit format: Format): Boolean =
    kvs.toList.map { case (k, v) => set(k, v) }.forall(_ == true)

  override def msetnx(kvs: (Any, Any)*)(implicit format: Format): Boolean =
    kvs.toList.map { case (k, v) => setnx(k, v) }.forall(_ == true)

  override def setrange(key: Any, offset: Int, value: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.setrange(key, offset, value))

  override def getrange[A](key: Any, start: Int, end: Int)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.getrange(key, start, end))

  override def strlen(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.strlen(key))

  override def append(key: Any, value: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.append(key, value))

  override def getbit(key: Any, offset: Int)(implicit format: Format): Option[Int] =
    processForKey(key)(_.getbit(key, offset))

  override def setbit(key: Any, offset: Int, value: Any)(implicit format: Format): Option[Int] =
    processForKey(key)(_.setbit(key, offset, value))

  override def bitop(op: String, destKey: Any, srcKeys: Any*)(implicit format: Format): Option[Int] = ??? // todo: implement

  override def bitcount(key: Any, range: Option[(Int, Int)])(implicit format: Format): Option[Int] =
    processForKey(key)(_.bitcount(key, range))
}
