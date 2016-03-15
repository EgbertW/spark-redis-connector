package nl.anchormen.receiver

import java.util

import redis.clients.jedis.{HostAndPort, Jedis, JedisCluster}

/**
  * Created by elsioufy on 13-1-16.
  */

object RedisProducer {

  /*This is a simple Redis Producer that
  * 1) connects to a RedisCluster with node @localhost:7000 and pushes data to lists @keys Key1, Key2, Key3
  * 2) connects to a RedisNode @localhost:7007 and pushes data to sets @keys Key4, Key5
  * */

  def main(args: Array[String]) {
    val clusterHost = "localhost"
    val clusterPort = 7000
    val jedisClusterNodes = new util.HashSet[HostAndPort]()
    jedisClusterNodes.add(new HostAndPort(clusterHost, clusterPort))
    val jc = new JedisCluster(jedisClusterNodes)
    val clusterKeys = Array("Key1", "Key2", "Key3")
    /******************/
    val nodeHost = "localhost"
    val nodePort = 7007
    val jedis = new Jedis(nodeHost, nodePort)
    val nodeKeys = Array("Key4", "Key5")
    /******************/
    while (true){
      val clusterKey = clusterKeys((Math.random()*clusterKeys.length).toInt)
      val nodeKey = nodeKeys((Math.random()*nodeKeys.length).toInt)
      val timestamp = java.lang.System.currentTimeMillis()
      jc.lpush(clusterKey, timestamp +"")
      jedis.sadd(nodeKey, timestamp+"")
      Thread.sleep(500)
    }
  }

}
