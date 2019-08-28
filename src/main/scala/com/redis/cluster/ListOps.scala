package com.redis.cluster

import com.redis.api.ListApi
import com.redis.serialization.{Format, Parse}

trait ListOps extends ListApi {
  rc: RedisClusterOps =>

  override def lpush(key: Any, value: Any, values: Any*)(implicit format: Format): Option[Long] =
    processForKey(key)(_.lpush(key, value, values: _*))

  override def rpush(key: Any, value: Any, values: Any*)(implicit format: Format): Option[Long] =
    processForKey(key)(_.rpush(key, value, values: _*))

  override def llen(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.llen(key))

  override def lrange[A](key: Any, start: Int, end: Int)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] =
    processForKey(key)(_.lrange[A](key, start, end))

  override def ltrim(key: Any, start: Int, end: Int)(implicit format: Format): Boolean =
    processForKey(key)(_.ltrim(key, start, end))

  override def lindex[A](key: Any, index: Int)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.lindex(key, index))

  override def lset(key: Any, index: Int, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.lset(key, index, value))

  override def lrem(key: Any, count: Int, value: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.lrem(key, count, value))

  override def lpop[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.lpop[A](key))

  override def rpop[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.rpop[A](key))

  override def rpoplpush[A](srcKey: Any, dstKey: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    inSameNode(srcKey, dstKey) { n => n.rpoplpush[A](srcKey, dstKey) }

  override def brpoplpush[A](srcKey: Any, dstKey: Any, timeoutInSeconds: Int)(implicit format: Format, parse: Parse[A]): Option[A] =
    inSameNode(srcKey, dstKey) { n => n.brpoplpush[A](srcKey, dstKey, timeoutInSeconds) }

  override def blpop[K, V](timeoutInSeconds: Int, key: K, keys: K*)(implicit format: Format, parseK: Parse[K], parseV: Parse[V]): Option[(K, V)] =
    inSameNode((key :: keys.toList): _*) { n => n.blpop[K, V](timeoutInSeconds, key, keys: _*) }

  override def brpop[K, V](timeoutInSeconds: Int, key: K, keys: K*)(implicit format: Format, parseK: Parse[K], parseV: Parse[V]): Option[(K, V)] =
    inSameNode((key :: keys.toList): _*) { n => n.brpop[K, V](timeoutInSeconds, key, keys: _*) }

  override def lpushx(key: Any, value: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.lpushx(key, value))

  override def rpushx(key: Any, value: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.rpushx(key, value))

}
