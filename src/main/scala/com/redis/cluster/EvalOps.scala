package com.redis.cluster

import com.redis.api.EvalApi
import com.redis.serialization.{Format, Parse}

trait EvalOps extends EvalApi {
  self: RedisClusterOps =>

  // todo: broken output
  override def evalMultiBulk[A](luaCode: String, keys: List[Any], args: List[Any])
                               (implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] =
    processForKeys(keys)(gkeys => rc => rc.evalMultiBulk(luaCode, gkeys, args))
      .flatten.headOption

  // todo: broken output
  override def evalBulk[A](luaCode: String, keys: List[Any], args: List[Any])
                          (implicit format: Format, parse: Parse[A]): Option[A] =
    processForKeys(keys)(gkeys => rc => rc.evalBulk(luaCode, gkeys, args))
      .flatten.headOption

  // todo: broken output
  override def evalInt(luaCode: String, keys: List[Any], args: List[Any]): Option[Int] =
    processForKeys(keys)(gkeys => rc => rc.evalInt(luaCode, gkeys, args))
      .flatten.headOption

  // todo: broken output
  override def evalMultiSHA[A](shahash: String, keys: List[Any], args: List[Any])
                              (implicit format: Format, parse: Parse[A]): Option[List[Option[A]]] =
    processForKeys(keys)(gkeys => rc => rc.evalMultiSHA(shahash, gkeys, args))
      .flatten.headOption

  // todo: broken output
  override def evalSHA[A](shahash: String, keys: List[Any], args: List[Any])
                         (implicit format: Format, parse: Parse[A]): Option[A] =
    processForKeys(keys)(gkeys => rc => rc.evalSHA(shahash, gkeys, args))
      .flatten.headOption

  // todo: broken output
  override def evalSHABulk[A](shahash: String, keys: List[Any], args: List[Any])
                             (implicit format: Format, parse: Parse[A]): Option[A] =
    processForKeys(keys)(gkeys => rc => rc.evalSHABulk(shahash, gkeys, args))
      .flatten.headOption

  override def scriptLoad(luaCode: String): Option[String] = {
    val r = onAllConns(_.scriptLoad(luaCode))
    oneCommonAnswerOr(r)(orError("ScriptLoad")).flatten
  }

  private val scriptExistsNot = Some(0)

  override def scriptExists(shahash: String): Option[Int] = {
    val r = onAllConns(_.scriptExists(shahash))
    oneCommonAnswerOr(r)(_.find(_ == scriptExistsNot)).flatten
  }

  override def scriptFlush: Option[String] = {
    val r = onAllConns(_.scriptFlush)
    oneCommonAnswerOr(r)(orError("ScriptFlush")).flatten
  }

  private def orError[A](method: String)(r: Iterable[A]): Option[A] =
    throw new IllegalStateException(s"Various values returned while $method from various instances: ${r.mkString(",")}")

  protected def oneCommonAnswerOr[A](r: Iterable[A])(moreResultHandler: Iterable[A] => Option[A]): Option[A] = {
    val distinct = r.toSeq.distinct
    if (distinct.size > 1) {
      moreResultHandler(distinct)
    } else {
      r.headOption
    }
  }

}
