package com.redis.cluster

import com.redis.api.HashApi
import com.redis.serialization.{Format, Parse}

trait HashOps extends HashApi {
  rc: RedisClusterOps =>

  override def hset(key: Any, field: Any, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.hset(key, field, value))

  override def hset1(key: Any, field: Any, value: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.hset1(key, field, value))

  override def hget[A](key: Any, field: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.hget[A](key, field))

  override def hmset(key: Any, map: Iterable[Product2[Any, Any]])(implicit format: Format): Boolean =
    processForKey(key)(_.hmset(key, map))

  override def hmget[K, V](key: Any, fields: K*)(implicit format: Format, parseV: Parse[V]): Option[Map[K, V]] =
    processForKey(key)(_.hmget[K, V](key, fields: _*))

  override def hincrby(key: Any, field: Any, value: Long)(implicit format: Format): Option[Long] =
    processForKey(key)(_.hincrby(key, field, value))

  override def hexists(key: Any, field: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.hexists(key, field))

  override def hdel(key: Any, field: Any, fields: Any*)(implicit format: Format): Option[Long] =
    processForKey(key)(_.hdel(key, field, fields: _*))

  override def hlen(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.hlen(key))

  override def hkeys[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[List[A]] =
    processForKey(key)(_.hkeys[A](key))

  override def hvals[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[List[A]] =
    processForKey(key)(_.hvals[A](key))

  override def hgetall[K, V](key: Any)(implicit format: Format, parseK: Parse[K], parseV: Parse[V]): Option[Map[K, V]] =
    processForKey(key)(_.hgetall[K, V](key))

  override def hgetall1[K, V](key: Any)(implicit format: Format, parseK: Parse[K], parseV: Parse[V]): Option[Map[K, V]] =
    processForKey(key)(_.hgetall1[K, V](key))

  override def hsetnx(key: Any, field: Any, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.hsetnx(key, field, value))

  override def hincrbyfloat(key: Any, field: Any, value: Float)(implicit format: Format): Option[Float] =
    processForKey(key)(_.hincrbyfloat(key, field, value))

  // todo: implement
  override def hscan[A](key: Any, cursor: Int, pattern: Any, count: Int)(implicit format: Format, parse: Parse[A]): Option[(Option[Int], Option[List[Option[A]]])] = ???
}
