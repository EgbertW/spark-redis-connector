package nl.anchormen.receiver

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
    val clusterParams = Map (
      "host" -> "localhost",
      "port" -> "7000",
      "cluster" -> "true",
      "timeout" -> "500")
    RedisUtils.createStream(ssc, clusterParams, Set("Key1", "Key2", "Key3")) .print()

    /*note: using the default timeout=200 and cluster=false*/
    val nodeParams = Map (
      "host" -> "localhost",
      "port" -> "7007",
      "struct" -> "set")
    RedisUtils.createStream(ssc, nodeParams, Set("Key4", "Key5")).print()
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
