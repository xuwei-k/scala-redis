package com.redis.cluster

import com.redis.api.BaseApi
import com.redis.serialization.{Format, Parse}

trait BaseOps extends BaseApi {
  rc: RedisClusterOps =>

  override def keys[A](pattern: Any)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] = Some {
    onAllConns(_.keys[A](pattern))
      .foldLeft(List.empty[Option[A]]) {
        case (acc, el) => el match {
          case Some(x) => x ::: acc
          case None => acc
        }
      }
  }

  override def rename(oldkey: Any, newkey: Any)(implicit format: Format): Boolean =
    processForKey(oldkey)(_.rename(oldkey, newkey))

  override def renamenx(oldkey: Any, newkey: Any)(implicit format: Format): Boolean =
    processForKey(oldkey)(_.renamenx(oldkey, newkey))

  override def dbsize: Option[Long] = {
    val r = onAllConns(_.dbsize).flatten
    if (r.isEmpty) None else Some(r.sum)
  }

  override def exists(key: Any)(implicit format: Format): Boolean =
    processForKey(key)(_.exists(key))

  override def del(key: Any, keys: Any*)(implicit format: Format): Option[Long] = {
    val r = (key :: keys.toList).groupBy(nodeForKey)
      .flatMap { case (r, keys) => r.withClient(_.del(keys.head, keys.tail)) }
    if (r.isEmpty) None else Some(r.sum)
  }

  override def getType(key: Any)(implicit format: Format): Option[String] =
    processForKey(key)(_.getType(key))

  override def expire(key: Any, ttl: Int)(implicit format: Format): Boolean =
    processForKey(key)(_.expire(key, ttl))

  override def pexpire(key: Any, ttlInMillis: Int)(implicit format: Format): Boolean =
    processForKey(key)(_.pexpire(key, ttlInMillis))

  override def expireat(key: Any, timestamp: Long)(implicit format: Format): Boolean =
    processForKey(key)(_.expireat(key, timestamp))

  override def pexpireat(key: Any, timestampInMillis: Long)(implicit format: Format): Boolean =
    processForKey(key)(_.pexpireat(key, timestampInMillis))

  override def ttl(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.ttl(key))

  override def pttl(key: Any)(implicit format: Format): Option[Long] =
    processForKey(key)(_.pttl(key))

  override def flushdb: Boolean =
    onAllConns(_.flushdb) forall (_ == true)

  override def flushall: Boolean =
    onAllConns(_.flushall) forall (_ == true)

  override def quit: Boolean =
    onAllConns(_.quit) forall (_ == true)

  // todo: implement
  override def time[A](implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] = ???

  // todo: implement
  override def randomkey[A](implicit parse: Parse[A]): Option[A] = ???

  // todo: implement
  override def select(index: Int): Boolean = ???

  // todo: implement
  override def move(key: Any, db: Int)(implicit format: Format): Boolean = ???

  // todo: implement
  override def auth(secret: Any)(implicit format: Format): Boolean = ???

  // todo: implement
  override def persist(key: Any)(implicit format: Format): Boolean = ???

  // todo: implement
  override def scan[A](cursor: Int, pattern: Any, count: Int)(implicit format: Format, parse: Parse[A]): Option[(Option[Int], Option[List[Option[A]]])] = ???

  // todo: implement
  override def ping: Option[String] = ???

  // todo: implement
  override def watch(key: Any, keys: Any*)(implicit format: Format): Boolean = ???

  // todo: implement
  override def unwatch(): Boolean = ???

  // todo: implement
  override def getConfig(key: Any)(implicit format: Format): Option[Map[String, Option[String]]] = ???

  // todo: implement
  override def setConfig(key: Any, value: Any)(implicit format: Format): Option[String] = ???

  // todo: implement
  override def sort[A](key: String, limit: Option[(Int, Int)], desc: Boolean, alpha: Boolean, by: Option[String], get: List[String])(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] = ???

  // todo: implement
  override def sortNStore[A](key: String, limit: Option[(Int, Int)], desc: Boolean, alpha: Boolean, by: Option[String], get: List[String], storeAt: String)(implicit format: Format, parse: Parse[A]): Option[Long] = ???
}
