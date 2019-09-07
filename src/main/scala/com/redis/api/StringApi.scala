package com.redis.api

import com.redis.SecondsOrMillis
import com.redis.api.StringApi.{Always, NX, SetBehaviour, XX}
import com.redis.serialization.{Format, Parse}

import scala.concurrent.duration.Duration

trait StringApi {

  @deprecated("Use implementation with scala.concurrent.duration.Duration and SetBehaviour", "3.10")
  def set(key: Any, value: Any, onlyIfExists: Boolean, time: SecondsOrMillis): Boolean =
    set(key, value, if (onlyIfExists) XX else NX, time.toDuration)

  /**
    * sets the key with the specified value.
    * Starting with Redis 2.6.12 SET supports a set of options that modify its behavior:
    *
    * NX -- Only set the key if it does not already exist.
    * XX -- Only set the key if it already exist.
    * PX milliseconds -- Set the specified expire time, in milliseconds.
    */
  def set(key: Any, value: Any, whenSet: SetBehaviour = Always, expire: Duration = null)
         (implicit format: Format): Boolean

  /**
   * gets the value for the specified key.
   */
  def get[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A]

  /**
   * is an atomic set this value and return the old value command.
   */
  def getset[A](key: Any, value: Any)(implicit format: Format, parse: Parse[A]): Option[A]

  /**
   * sets the value for the specified key, only if the key is not there.
   */
  def setnx(key: Any, value: Any)(implicit format: Format): Boolean

  def setex(key: Any, expiry: Long, value: Any)(implicit format: Format): Boolean

  def psetex(key: Any, expiryInMillis: Long, value: Any)(implicit format: Format): Boolean

  /**
   * increments the specified key by 1
   */
  def incr(key: Any)(implicit format: Format): Option[Long]

  /**
   * increments the specified key by increment
   */
  def incrby(key: Any, increment: Long)(implicit format: Format): Option[Long]

  def incrbyfloat(key: Any, increment: Float)(implicit format: Format): Option[Float]

  /**
   * decrements the specified key by 1
   */
  def decr(key: Any)(implicit format: Format): Option[Long]

  /**
   * decrements the specified key by increment
   */
  def decrby(key: Any, increment: Long)(implicit format: Format): Option[Long]

  /**
   * get the values of all the specified keys.
   */
  def mget[A](key: Any, keys: Any*)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]]

  /**
   * set the respective key value pairs. Overwrite value if key exists
   */
  def mset(kvs: (Any, Any)*)(implicit format: Format): Boolean

  /**
   * set the respective key value pairs. Noop if any key exists
   */
  def msetnx(kvs: (Any, Any)*)(implicit format: Format): Boolean

  /**
   * SETRANGE key offset value
   * Overwrites part of the string stored at key, starting at the specified offset,
   * for the entire length of value.
   */
  def setrange(key: Any, offset: Int, value: Any)(implicit format: Format): Option[Long]

  /**
   * Returns the substring of the string value stored at key, determined by the offsets
   * start and end (both are inclusive).
   */
  def getrange[A](key: Any, start: Int, end: Int)(implicit format: Format, parse: Parse[A]): Option[A]

  /**
   * gets the length of the value associated with the key
   */
  def strlen(key: Any)(implicit format: Format): Option[Long]

  /**
   * appends the key value with the specified value.
   */
  def append(key: Any, value: Any)(implicit format: Format): Option[Long]

  /**
   * Returns the bit value at offset in the string value stored at key
   */
  def getbit(key: Any, offset: Int)(implicit format: Format): Option[Int]

  /**
   * Sets or clears the bit at offset in the string value stored at key
   */
  def setbit(key: Any, offset: Int, value: Any)(implicit format: Format): Option[Int]

  /**
   * Perform a bitwise operation between multiple keys (containing string values) and store the result in the destination key.
   */
  def bitop(op: String, destKey: Any, srcKeys: Any*)(implicit format: Format): Option[Int]

  /**
   * Count the number of set bits in the given key within the optional range
   */
  def bitcount(key: Any, range: Option[(Int, Int)] = None)(implicit format: Format): Option[Int]
}

object StringApi {

  // @formatter:off
  sealed abstract class SetBehaviour(val command: List[String]) // singleton list
  case object NX     extends SetBehaviour(List("NX"))
  case object XX     extends SetBehaviour(List("XX"))
  case object Always extends SetBehaviour(List.empty)
  // @formatter:on

}
