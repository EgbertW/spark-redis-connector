package nl.anchormen.redis.common

import java.util
import redis.clients.jedis.{HostAndPort, Jedis, JedisCluster}
import scala.util.Try

/**
  * Created by elsioufy on 30-5-16.
  */

private [redis] object RedisInstanceManager{

  /** This object shall be upgraded to take care of the following
    * Jedis (client for single node) is not Thread safe; We shall use JedisPool for provisioning (straight forward)
    * http://stackoverflow.com/questions/30107383/why-a-single-jedis-instance-is-not-threadsafe
    * JedisCluster (client for cluster) is Thread Safe; we don't need to  use any pools for provisioning
    * https://github.com/xetorthio/jedis/issues/560
    * https://github.com/xetorthio/jedis/issues/738
    * -------------------------------------------------
    * todo: Need to decide upon when to close a connection (either being in a pool or not) and when to keep it
    * for later soon use ... RedisConfig
    */

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