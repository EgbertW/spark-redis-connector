package nl.anchormen.redis.writer

import nl.anchormen.redis.common.RedisConfig
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import scala.util.{Failure, Success}

/**
  * Created by elsioufy on 14-6-16.
  */

class RedisSortedRDDWriter (rdd: RDD[(String, (String, Double))])  extends Serializable with LazyLogging{

  def saveToRedisSortedSet(redisConfig: RedisConfig): Unit = {
    rdd.foreachPartition(dataItr => {
      import nl.anchormen.redis.common.RedisInstanceManager._
      getRedisUnifiedAPI(redisConfig) match {
        case Success(j) => {
          logger.info("successfully connected to Redis")
          dataItr.foreach{ case(k,v)=> j.zadd(k,v._1, v._2) }
          j.close()
        }
        case Failure(f) => logger.error("Could not connect to Redis=> "+ f.getMessage)
      }
    })
  }

}

object RedisSortedRDDWriter {
  implicit def addRedisSortedRDDWriterFunctions(rdd: RDD[(String, (String, Double))]):
  RedisSortedRDDWriter = new RedisSortedRDDWriter(rdd)
}
