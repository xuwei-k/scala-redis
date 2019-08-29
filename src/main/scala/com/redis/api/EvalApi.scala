package com.redis.api

import com.redis.serialization.{Format, Parse}

trait EvalApi {

  /**
   * evaluates lua code on the server.
   */
  def evalMultiBulk[A](luaCode: String, keys: List[Any], args: List[Any])
                      (implicit format: Format, parse: Parse[A]): Option[List[Option[A]]]

  def evalBulk[A](luaCode: String, keys: List[Any], args: List[Any])
                 (implicit format: Format, parse: Parse[A]): Option[A]

  def evalInt(luaCode: String, keys: List[Any], args: List[Any]): Option[Int]

  def evalMultiSHA[A](shahash: String, keys: List[Any], args: List[Any])
                     (implicit format: Format, parse: Parse[A]): Option[List[Option[A]]]

  def evalSHA[A](shahash: String, keys: List[Any], args: List[Any])
                (implicit format: Format, parse: Parse[A]): Option[A]

  def evalSHABulk[A](shahash: String, keys: List[Any], args: List[Any])
                    (implicit format: Format, parse: Parse[A]): Option[A]

  def scriptLoad(luaCode: String): Option[String]

  def scriptExists(shahash: String): Option[Int]

  def scriptFlush: Option[String]

}
