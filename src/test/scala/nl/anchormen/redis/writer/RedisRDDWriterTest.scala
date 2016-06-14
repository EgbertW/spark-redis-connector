package nl.anchormen.redis.writer

import nl.anchormen.redis.common.RedisConfig
import nl.anchormen.testing.TestSpec
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis
import scala.collection.mutable

/**
  * Created by elsioufy on 30-5-16.
  */

class RedisRDDWriterTest extends TestSpec{

  val redisHost = "localhost"
  val redisPort = 6379
  val isRedisCluster = false
  /**/
  val redisConfig = (new RedisConfig()).setHost(redisHost).setPort(redisPort).setCluster(isRedisCluster)
  val jedis = new Jedis(redisHost, redisPort)


  before {}

  after {
    /*removing all testing keys*/
    val testKeys = jedis.keys("l_*")   //list keys
    testKeys.addAll(jedis.keys("s_*")) //set keys
    testKeys.addAll(jedis.keys("ss_*"))//sorted-set keys
    testKeys.addAll(jedis.keys("kv_*")) //kv_pairs
    val it = testKeys.iterator()
    while (it.hasNext)
      jedis.del(it.next)
    jedis.close()
  }


  "ABC" should "DEF" in {

    val sc = new SparkContext(new SparkConf().setAppName("redis-test").setMaster("local[4]"))
    val rdd1 = sc.parallelize(List(("K1","V11"),("K2","V21"),("K3","V31"),("K2","V22"),("K1","V12"),("K3","V32"),("K1","V13")))
    val rdd2 = sc.parallelize(List(("K1","V14"),("K2","V24"),("K3","V34"),("K2","V25"),("K2","V26"),("K2","V27"),("K1","V15")))
    val rdd3 = sc.parallelize(List(("K1","V16"),("K2","V20"),("K3","V3!")))
    val testStreamingQueue = mutable.Queue(rdd1,rdd2,rdd3)
    val ssc = new StreamingContext(sc, Seconds(5))
    val testStream = ssc.queueStream(testStreamingQueue)


    testStream.map{case(k,v)=> ("kv_"+k,v)}
      .foreachRDD(rdd=>{
      import nl.anchormen.redis.writer.RedisRDDWriter._
      rdd.saveToRedis(redisConfig)
    })

    testStream.map{case(k,v)=>("l_"+k,v)}.
      foreachRDD(rdd=>{
        import nl.anchormen.redis.writer.RedisRDDWriter._
        rdd.saveToRedisList(redisConfig)
      })

    testStream.map{case(k,v)=>{
      val key = "ss_"+k
      val rank = (Math.random()*100).toInt
      val value = v+"("+rank+")"
      (key,(value,rank.toDouble))
    }}
      .foreachRDD(rdd=>{
        import nl.anchormen.redis.writer.RedisSortedRDDWriter._
        rdd.saveToRedisSortedSet(redisConfig)
      })

    ssc.start()
    ssc.awaitTerminationOrTimeout(20000)
    /**************************************************************/

    /**/
    assert(jedis.get("kv_K1") == "V16")
    assert(jedis.get("kv_K2") == "V20")
    assert(jedis.get("kv_K3") == "V3!")
    /**/
    assert(jedis.llen("l_K1") == 6)
    assert(jedis.llen("l_K2") == 7)
    assert(jedis.llen("l_K3") == 4)
    /**/

    val a = jedis.zrange("ss_K1",0,-1)
    val aItr = a.iterator()
    while (aItr.hasNext)
      println(aItr.next())





  }




}
