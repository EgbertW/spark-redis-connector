package nl.anchormen.redis.common

import java.util

import redis.clients.jedis.{HostAndPort, Jedis, JedisCluster}

import scala.util.Try

/**
  * Created by elsioufy on 30-5-16.
  */

object RedisCommon{

  def getRedisUnifiedAPI(redisConfig: RedisConfig): Try[JedisAPIWrapper] = {

    if (redisConfig.isCluster){
      val jedisClusterNodes = new util.HashSet[HostAndPort]()
      jedisClusterNodes.add(new HostAndPort(redisConfig.getHost, redisConfig.getPort))
      Try(new JedisClusterWrapper(new JedisCluster(jedisClusterNodes)))
    }
    else{
      Try(new JedisClientWrapper(new Jedis(redisConfig.getHost, redisConfig.getPort)))
    }
  }

}