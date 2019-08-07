package com.redis.api

trait NodeApi {

  /**
   * save the DB on disk now.
   */
  def save: Boolean

  /**
   * save the DB in the background.
   */
  def bgsave: Boolean

  /**
   * return the UNIX TIME of the last DB SAVE executed with success.
   */
  def lastsave: Option[Long]

  /**
   * Stop all the clients, save the DB, then quit the server.
   */
  def shutdown: Boolean

  def bgrewriteaof: Boolean

  /**
   * The info command returns different information and statistics about the server.
   */
  def info: Option[String]

  /**
   * is a debugging command that outputs the whole sequence of commands received by the Redis server.
   */
  def monitor: Boolean

  /**
   * The SLAVEOF command can change the replication settings of a slave on the fly.
   */
  def slaveof(options: Any): Boolean

}
