package com.redis

import com.redis.api.StringApi
import com.redis.api.StringApi.{Always, SetBehaviour}
import com.redis.serialization._

import scala.concurrent.duration.Duration

trait StringOperations extends StringApi {
  self: Redis =>

  override def set(key: Any, value: Any, whenSet: SetBehaviour = Always, expire: Duration = null)
                  (implicit format: Format): Boolean = {
    val expireCmd = if (expire != null) {
      List("PX", expire.toMillis.toString)
    } else {
      List.empty
    }
    val cmd = List(key, value) ::: expireCmd ::: whenSet.command
    send("SET", cmd)(asBoolean)
  }

  override def get[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    send("GET", List(key))(asBulk)

  override def getset[A](key: Any, value: Any)(implicit format: Format, parse: Parse[A]): Option[A] =
    send("GETSET", List(key, value))(asBulk)

  override def setnx(key: Any, value: Any)(implicit format: Format): Boolean =
    send("SETNX", List(key, value))(asBoolean)

  override def setex(key: Any, expiry: Long, value: Any)(implicit format: Format): Boolean =
    send("SETEX", List(key, expiry, value))(asBoolean)

  override def psetex(key: Any, expiryInMillis: Long, value: Any)(implicit format: Format): Boolean =
    send("PSETEX", List(key, expiryInMillis, value))(asBoolean)

  override def incr(key: Any)(implicit format: Format): Option[Long] =
    send("INCR", List(key))(asLong)

  override def incrby(key: Any, increment: Long)(implicit format: Format): Option[Long] =
    send("INCRBY", List(key, increment))(asLong)

  override def incrbyfloat(key: Any, increment: Float)(implicit format: Format): Option[Float] =
    send("INCRBYFLOAT", List(key, increment))(asBulk.map(_.toFloat))

  override def decr(key: Any)(implicit format: Format): Option[Long] =
    send("DECR", List(key))(asLong)

  override def decrby(key: Any, increment: Long)(implicit format: Format): Option[Long] =
    send("DECRBY", List(key, increment))(asLong)

  override def mget[A](key: Any, keys: Any*)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] =
    send("MGET", key :: keys.toList)(asList)

  override def mset(kvs: (Any, Any)*)(implicit format: Format): Boolean =
    send("MSET", kvs.foldRight(List[Any]()) { case ((k, v), l) => k :: v :: l })(asBoolean)

  override def msetnx(kvs: (Any, Any)*)(implicit format: Format): Boolean =
    send("MSETNX", kvs.foldRight(List[Any]()) { case ((k, v), l) => k :: v :: l })(asBoolean)

  override def setrange(key: Any, offset: Int, value: Any)(implicit format: Format): Option[Long] =
    send("SETRANGE", List(key, offset, value))(asLong)

  override def getrange[A](key: Any, start: Int, end: Int)(implicit format: Format, parse: Parse[A]): Option[A] =
    send("GETRANGE", List(key, start, end))(asBulk)

  override def strlen(key: Any)(implicit format: Format): Option[Long] =
    send("STRLEN", List(key))(asLong)

  override def append(key: Any, value: Any)(implicit format: Format): Option[Long] =
    send("APPEND", List(key, value))(asLong)

  override def getbit(key: Any, offset: Int)(implicit format: Format): Option[Int] =
    send("GETBIT", List(key, offset))(asInt)

  override def setbit(key: Any, offset: Int, value: Any)(implicit format: Format): Option[Int] =
    send("SETBIT", List(key, offset, value))(asInt)

  override def bitop(op: String, destKey: Any, srcKeys: Any*)(implicit format: Format): Option[Int] =
    send("BITOP", op :: destKey :: srcKeys.toList)(asInt)

  override def bitcount(key: Any, range: Option[(Int, Int)] = None)(implicit format: Format): Option[Int] =
    send("BITCOUNT", List[Any](key) ++ (range.map { r => List[Any](r._1, r._2) } getOrElse List[Any]()))(asInt)

}
