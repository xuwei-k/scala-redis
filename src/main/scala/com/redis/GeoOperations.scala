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

  def geopos[A](key: Any, members: Iterable[Any])(implicit format: Format, parse: Parse[A]): Option[List[Option[List[Option[A]]]]] = {
    send("GEOPOS", key :: members.toList)(receive(multiBulkNested).map(_.map(_.map(_.map(_.map(parse))))))
  }

  def geohash[A](key: Any, members: Iterable[Any])(implicit format: Format, parse: Parse[A]): Option[List[A]]= {
    send("GEOHASH", key :: members.toList)(asList.map(_.flatten))
  }

  def geodist(key: Any, m1: Any, m2: Any, unit: Option[Any]): Option[String] = {
    send("GEODIST", List(key, m1, m2) ++ unit.toList)(asBulk[String])
  }

  def georadius[A](key: Any,
                longitude: Any,
                latitude: Any,
                radius: Any,
                unit: Any,
                withCoord: Boolean,
                withDist: Boolean,
                withHash: Boolean,
                count: Option[Int],
                sort: Option[Any],
                store: Option[Any],
                storeDist: Option[Any])(implicit format: Format, parse: Parse[A]): Option[List[Option[GeoRadiusMember]]] = {
    val radArgs = List( if (withCoord) List("WITHCOORD") else Nil
        , if (withDist) List("WITHDIST") else Nil
        , if (withHash) List("WITHHASH") else Nil
        , sort.fold[List[Any]](Nil)(b => List(b))
        , count.fold[List[Any]](Nil)(b => List("COUNT", b))
        , store.fold[List[Any]](Nil)(b => List("STORE", b))
        , storeDist.fold[List[Any]](Nil)(b => List("STOREDIST", b))
      ).flatMap(x=>x)
    send("GEORADIUS", List(key, longitude, latitude, radius, unit) ++ radArgs)(receive(geoRadiusMemberReply))
  }

  def georadiusbymember[A](key: Any,
                   member: Any,
                   radius: Any,
                   unit: Any,
                   withCoord: Boolean,
                   withDist: Boolean,
                   withHash: Boolean,
                   count: Option[Int],
                   sort: Option[Any],
                   store: Option[Any],
                   storeDist: Option[Any])(implicit format: Format, parse: Parse[A]): Option[List[Option[GeoRadiusMember]]] = {
    val radArgs = List( if (withCoord) List("WITHCOORD") else Nil
      , if (withDist) List("WITHDIST") else Nil
      , if (withHash) List("WITHHASH") else Nil
      , sort.fold[List[Any]](Nil)(b => List(b))
      , count.fold[List[Any]](Nil)(b => List("COUNT", b))
      , store.fold[List[Any]](Nil)(b => List("STORE", b))
      , storeDist.fold[List[Any]](Nil)(b => List("STOREDIST", b))
    ).flatMap(x=>x)
    send("GEORADIUSBYMEMBER", List(key, member, radius, unit) ++ radArgs)(receive(geoRadiusMemberReply))
  }

}
