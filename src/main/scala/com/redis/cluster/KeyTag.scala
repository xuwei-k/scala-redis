package com.redis.cluster

/**
 * <p>
 * Consistent hashing distributes keys across multiple servers. But there are situations
 * like <i>sorting</i> or computing <i>set intersections</i> or operations like <tt>rpoplpush</tt>
 * in redis that require all keys to be collocated on the same server.
 * </p>
 * <p>
 * One of the techniques that redis encourages for such forced key locality is called <i>key tagging</i>.
 * See <a href="https://redis.io/topics/cluster-tutorial#redis-cluster-data-sharding">Redis Cluster data sharding</a>
 * for reference.
 * </p>
 * <p><i>
 * (...) but the gist is that if there is a substring between {} brackets in a key, only what is inside the string
 * is hashed, so for example this{foo}key and another{foo}key are guaranteed to be in the same hash slot,
 * and can be used together in a command with multiple keys as arguments.
 * </i></p>
 */
trait KeyTag {

  /**
   * Takes a key and returns the part of the key on which we hash to determine the server on which it will be located.
   * If it returns <tt>None</tt> then we hash on the whole key, otherwise we hash only on the returned part.
   */
  def tag(key: Seq[Byte]): Option[Seq[Byte]]
}

object KeyTag {

  object RegexKeyTag extends KeyTag {

    val tagStart: Byte = '{'.toByte
    val tagEnd: Byte = '}'.toByte

    override def tag(key: Seq[Byte]): Option[Seq[Byte]] = {
      val start = key.indexOf(tagStart) + 1
      if (start > 0) {
        val end = key.indexOf(tagEnd, start)
        if (end > -1) Some(key.slice(start, end)) else None
      } else None
    }
  }

  object NoOpKeyTag extends KeyTag {
    def tag(key: Seq[Byte]): Option[Seq[Byte]] = None
  }

}
