package nl.anchormen.redis.writer

import nl.anchormen.redis.common.RedisConfig
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import scala.util.{Failure, Success}

/**
  * Created by elsioufy on 14-6-16.
  */

class RedisSortedRDDWriter (rdd: RDD[(String, (String, Double))])  extends Serializable with Logging{

  def saveToRedisSortedSet(redisConfig: RedisConfig): Unit = {
    rdd.foreachPartition(dataItr => {
      import nl.anchormen.redis.common.RedisInstanceManager._
      getRedisUnifiedAPI(redisConfig) match {
        case Success(j) => {
          log.info("successfully connected to Redis")
          dataItr.foreach{ case(k,v)=> j.zadd(k,v._1, v._2) }
          j.close()
        }
        case Failure(f) => log.error("Could not connect to Redis=> "+ f.getMessage)
      }
    })
  }

}

object RedisSortedRDDWriter {
  implicit def addRedisSortedRDDWriterFunctions(rdd: RDD[(String, (String, Double))]):
  RedisSortedRDDWriter = new RedisSortedRDDWriter(rdd)
}
