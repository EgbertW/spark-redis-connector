package nl.anchormen.redis.common

import redis.clients.jedis.{Jedis, JedisCluster}
/**
  * Created by elsioufy on 16-1-16.
  */

/**
  * Allows to use JedisClient and JedisCluster through a single interface
  */
trait JedisAPIWrapper {
  def spop(k: String) : String
  def lpop(k: String) : String
  /**/
  def ladd (k: String, v: String)
  def sadd (k: String, v: String)
  def add (k: String, v: String)
  /**/
  def close() : Unit
}

class JedisClientWrapper(jedis: Jedis) extends JedisAPIWrapper {
  override def spop(k: String): String = jedis.spop(k)
  override def lpop(k: String): String = jedis.lpop(k)
  override def sadd(k: String, v: String) = jedis.sadd(k,v)
  override def ladd(k: String, v: String) = jedis.lpush(k,v)
  override def add(k: String, v: String) = jedis.set(k,v)
  override def close(): Unit = jedis.close()
}

class JedisClusterWrapper(jc: JedisCluster) extends JedisAPIWrapper {
  override def spop(k: String): String = jc.spop(k)
  override def lpop(k: String): String = jc.lpop(k)
  override def sadd(k: String, v: String) = jc.sadd(k,v)
  override def ladd(k: String, v: String) = jc.lpush(k,v)
  override def add(k: String, v: String) = jc.set(k,v)
  override def close(): Unit = jc.close()
}