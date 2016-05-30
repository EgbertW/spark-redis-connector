package nl.anchormen.redis.writer

import nl.anchormen.redis.common.{RedisConfig}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis
import scala.collection.mutable

/**
  * Created by elsioufy on 30-5-16.
  */

object RedisRDDWriterTest {

  def main(args: Array[String]) {

    val sc = new SparkContext(new SparkConf().setAppName("redis-test").setMaster("local[4]"))
    val rdd1 = sc.parallelize(List(("K1","V11"),("K2","V21"),("K3","V31"),("K2","V22"),("K1","V12"),("K3","V32"),("K1","V13")))
    val rdd2 = sc.parallelize(List(("K1","V14"),("K2","V24"),("K3","V34"),("K2","V25"),("K2","V26"),("K2","V27"),("K1","V15")))
    val rdd3 = sc.parallelize(List(("K1","V16"),("K2","V20"),("K3","V3!")))
    val testStreamingQueue = mutable.Queue(rdd1,rdd2,rdd3)

    val ssc = new StreamingContext(sc, Seconds(5))
    //defaults: host:localhost, port:6379, cluster?false
    val redisConfig = new RedisConfig()

    val testStream = ssc.queueStream(testStreamingQueue)

    testStream.foreachRDD(rdd=>{
      import nl.anchormen.redis.writer.RedisRDDWriter._
      rdd.saveToRedis(redisConfig)
    })

    testStream.map{case(k,v)=>("l_"+k,v)}.
      foreachRDD(rdd=>{
        import nl.anchormen.redis.writer.RedisRDDWriter._
        rdd.saveToRedisList(redisConfig)
      })

    testStream.print()

    ssc.start()
    ssc.awaitTerminationOrTimeout(20000)
    /**************************************************************/
    //shall be the case on first run
    val j = new Jedis(redisConfig.getHost, redisConfig.getPort)
    /**/
    assert(j.get("K1") == "V16")
    assert(j.get("K2") == "V20")
    assert(j.get("K3") == "V3!")
    /**/
    assert(j.llen("l_K1") == 6)
    assert(j.llen("l_K2") == 7)
    assert(j.llen("l_K3") == 4)

  }




}
