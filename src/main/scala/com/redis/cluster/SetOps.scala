package com.redis.cluster

import com.redis.api.SetApi
import com.redis.serialization.{Format, Parse}

trait SetOps extends SetApi {
  rc: RedisClusterOps =>

  override def sadd(key: Any, value: Any, values: Any*)(implicit format: Format): Option[Long] =
    processForKey(key)(_.sadd(key, value, values: _*))

  override def srem(key: Any, value: Any, values: Any*)(implicit format: Format): Option[Long] =
    processForKey(key)(_.srem(key, value, values: _*))

  override def spop[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.spop[A](key))

  override def smove(sourceKey: Any, destKey: Any, value: Any)(implicit format: Format): Option[Long] =
    inSameNode(sourceKey, destKey) { n => n.smove(sourceKey, destKey, value) }

  override def scard(key: Any)(implicit format: Format): Option[Long] = processForKey(key)(_.scard(key))

  override def sismember(key: Any, value: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.sismember(key, value))

  override def sinter[A](key: Any, keys: Any*)(implicit format: Format, parse: Parse[A]): Option[Set[Option[A]]] =
    inSameNode((key :: keys.toList): _*) { n => n.sinter[A](key, keys: _*) }

  override def sinterstore(key: Any, keys: Any*)(implicit format: Format): Option[Long] =
    inSameNode((key :: keys.toList): _*) { n => n.sinterstore(key, keys: _*) }

  override def sunion[A](key: Any, keys: Any*)(implicit format: Format, parse: Parse[A]): Option[Set[Option[A]]] =
    inSameNode((key :: keys.toList): _*) { n => n.sunion[A](key, keys: _*) }

  override def sunionstore(key: Any, keys: Any*)(implicit format: Format): Option[Long] =
    inSameNode((key :: keys.toList): _*) { n => n.sunionstore(key, keys: _*) }

  override def sdiff[A](key: Any, keys: Any*)(implicit format: Format, parse: Parse[A]): Option[Set[Option[A]]] =
    inSameNode((key :: keys.toList): _*) { n => n.sdiff[A](key, keys: _*) }

  override def sdiffstore(key: Any, keys: Any*)(implicit format: Format): Option[Long] =
    inSameNode((key :: keys.toList): _*) { n => n.sdiffstore(key, keys: _*) }

  override def smembers[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[Set[Option[A]]] =
    processForKey(key)(_.smembers(key))

  override def srandmember[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    processForKey(key)(_.srandmember(key))

  override def srandmember[A](key: Any, count: Int)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] =
    processForKey(key)(_.srandmember(key, count))

  override def spop[A](key: Any, count: Int)(implicit format: Format, parse: Parse[A]): Option[Set[Option[A]]] =
    processForKey(key)(_.spop[A](key, count))

  // todo: implement
  override def sscan[A](key: Any, cursor: Int, pattern: Any, count: Int)(implicit format: Format, parse: Parse[A]): Option[(Option[Int], Option[List[Option[A]]])] = ???
}
