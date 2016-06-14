package nl.anchormen.redis.common

/**
  * Created by elsioufy on 30-5-16.
  */

/**
  * Holds RedisConfiguration to connect to a Redis Cluster or Node
  * RedisConfig has a unique identifier (id) that changes whenever a configuration parameter changes
  */

class RedisConfig() extends Serializable{
  private var id = randomize()
  private var params = scala.collection.mutable.Map[String, String]()
  private def randomize(): Int = (Math.random()*10000).toInt /*very simple to create a random id*/
  private def set(k: String, v: String): RedisConfig = {
    id = randomize()
    params+=(k->v)
    this
  }
  /**/
  def setHost(h: String): RedisConfig  = set("host",h)
  def setPort(p: Int): RedisConfig  = set("port", p.toString)
  def setCluster(c: Boolean): RedisConfig  = set("cluster", c.toString)
  def setStreamingTimeout(t: Long): RedisConfig  = set("streaming.timeout", t.toString)
  def setStreamingStruct(s: String): RedisConfig  = set("streaming.struct", s)
  /**/
  def getHost : String = params.getOrElse("host", "localhost")
  def getPort : Int = (params.getOrElse("port", "6379")).toInt
  def isCluster: Boolean = params.getOrElse("cluster", "false").toBoolean
  def getStreamingTimeout: Long = params.getOrElse("streaming.timeout", "200").toLong
  def getStreamingStruct: String = params.getOrElse("streaming.struct", "list")
  /**/
  def getConfigId: Int = id
  def show(): Unit = {
    println("RedisConfig Settings ..")
    params.foreach(println)
    println("RedisConfig Settings ..[OK!]")
  }

}