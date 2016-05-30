package nl.anchormen.redis.common

/**
  * Created by elsioufy on 30-5-16.
  */

class RedisConfig() extends Serializable{
  var params = scala.collection.mutable.Map[String, String]()

  private def set(k: String, v: String): Unit = {params+=(k->v)}
  /**/
  def setHost(h: String): Unit = set("host",h)
  def setPort(p: Int): Unit = set("port", p.toString)
  def setCluster(c: Boolean): Unit = set("cluster", c.toString)
  def setStreamingTimeout(t: Long): Unit = set("streaming.timeout", t.toString)
  def setStreamingStruct(s: String): Unit = set("streaming.struct", s )
  /**/
  def getHost : String = params.getOrElse("host", "localhost")
  def getPort : Int = (params.getOrElse("port", "6379")).toInt
  def isCluster: Boolean = params.getOrElse("cluster", "false").toBoolean
  def getStreamingTimeout: Long = params.getOrElse("streaming.timeout", "200").toLong
  def getStreamingStruct: String = params.getOrElse("streaming.struct", "list")
}