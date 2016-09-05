package com.redis

import com.redis.serialization._

/**
  * Created by alexis on 05/09/16.
  */
trait GeoOperations { self: Redis =>

  private def flattenProduct3(in: Iterable[Product3[Any, Any, Any]]): List[Any] =
    in.iterator.flatMap(x => Iterator(x._1, x._2, x._3)).toList

  def geoadd(key: Any, members: Iterable[Product3[Any, Any, Any]]): Option[Int] = {
    send("GEOADD", key :: flattenProduct3(members))(asInt)
  }

  def geohash[A](key: Any, members: Iterable[Any])(implicit format: Format, parse: Parse[A]): Option[List[A]]= {
    send("GEOHASH", key :: members.toList)(asList.map(_.flatten))
  }

}
