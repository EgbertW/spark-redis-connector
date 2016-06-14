package nl.anchormen.redis.receiver

import nl.anchormen.redis.common.RedisConfig
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by elsioufy on 13-1-16.
  */


object RedisConsumer {

  /* This is a simple Redis Consumer that consumes data produced by the producer*/
  def main(args: Array[String]) {
    setLogsToErrorOnly
    val conf = new SparkConf()
      .setAppName("redis-spark-receiver")
      .setIfMissing("spark.master", "local[4]")
    val ssc = new StreamingContext(conf, Seconds(5))
    /******************************************************************************************************************/
    /*note using the default struct=list*/
    val redisConfigCluster = new RedisConfig()
    redisConfigCluster.setHost("localhost")
    redisConfigCluster.setPort(7000)
    redisConfigCluster.setCluster(true)
    redisConfigCluster.setStreamingTimeout(500)
    redisConfigCluster.setStreamingStruct("list")
    RedisUtils.createStream(ssc, redisConfigCluster, Set("Key1", "Key2", "Key3")) .print()


    /*note: using the default timeout=200 and cluster=false*/
    val redisConfigNode = new RedisConfig()
    redisConfigNode.setHost("localhost")
    redisConfigNode.setPort(7007)
    redisConfigNode.setCluster(false)
    redisConfigNode.setStreamingTimeout(200)
    redisConfigNode.setStreamingStruct("set")
    RedisUtils.createStream(ssc, redisConfigNode, Set("Key4", "Key5")).print()
    /**/
    /******************************************************************************************************************/
    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate
  }


  def setLogsToErrorOnly = {
    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    Logger.getLogger("streaming").setLevel(Level.ERROR)
    Logger.getLogger("spark").setLevel(Level.ERROR)
    Logger.getLogger("nl.anchormen").setLevel(Level.ERROR)
  }













}
