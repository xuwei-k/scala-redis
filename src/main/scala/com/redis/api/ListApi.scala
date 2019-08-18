package com.redis.api

import com.redis.serialization.{Format, Parse}

trait ListApi {

  /**
   * add values to the head of the list stored at key (Variadic: >= 2.4)
   */
  def lpush(key: Any, value: Any, values: Any*)(implicit format: Format): Option[Long]

  /**
   * add value to the head of the list stored at key (Variadic: >= 2.4)
   */
  def lpushx(key: Any, value: Any)(implicit format: Format): Option[Long]

  /**
   * add values to the tail of the list stored at key (Variadic: >= 2.4)
   */
  def rpush(key: Any, value: Any, values: Any*)(implicit format: Format): Option[Long]

  /**
   * add value to the tail of the list stored at key (Variadic: >= 2.4)
   */
  def rpushx(key: Any, value: Any)(implicit format: Format): Option[Long]

  /**
   * return the length of the list stored at the specified key.
   * If the key does not exist zero is returned (the same behaviour as for empty lists).
   * If the value stored at key is not a list an error is returned.
   */
  def llen(key: Any)(implicit format: Format): Option[Long]

  /**
   * return the specified elements of the list stored at the specified key.
   * Start and end are zero-based indexes.
   */
  def lrange[A](key: Any, start: Int, end: Int)(implicit format: Format, parse: Parse[A]): Option[List[Option[A]]]

  /**
   * Trim an existing list so that it will contain only the specified range of elements specified.
   */
  def ltrim(key: Any, start: Int, end: Int)(implicit format: Format): Boolean

  /**
   * return the especified element of the list stored at the specified key.
   * Negative indexes are supported, for example -1 is the last element, -2 the penultimate and so on.
   */
  def lindex[A](key: Any, index: Int)(implicit format: Format, parse: Parse[A]): Option[A]

  /**
   * set the list element at index with the new value. Out of range indexes will generate an error
   */
  def lset(key: Any, index: Int, value: Any)(implicit format: Format): Boolean

  /**
   * Remove the first count occurrences of the value element from the list.
   */
  def lrem(key: Any, count: Int, value: Any)(implicit format: Format): Option[Long]

  /**
   * atomically return and remove the first (LPOP) or last (RPOP) element of the list
   */
  def lpop[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A]

  /**
   * atomically return and remove the first (LPOP) or last (RPOP) element of the list
   */
  def rpop[A](key: Any)(implicit format: Format, parse: Parse[A]): Option[A]

  /**
   * Remove the first count occurrences of the value element from the list.
   */
  def rpoplpush[A](srcKey: Any, dstKey: Any)(implicit format: Format, parse: Parse[A]): Option[A]

  def brpoplpush[A](srcKey: Any, dstKey: Any, timeoutInSeconds: Int)(implicit format: Format, parse: Parse[A]): Option[A]

  def blpop[K, V](timeoutInSeconds: Int, key: K, keys: K*)(implicit format: Format, parseK: Parse[K], parseV: Parse[V]): Option[(K, V)]

  def brpop[K, V](timeoutInSeconds: Int, key: K, keys: K*)(implicit format: Format, parseK: Parse[K], parseV: Parse[V]): Option[(K, V)]

}