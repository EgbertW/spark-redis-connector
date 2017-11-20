package nl.anchormen.redis.writer

import nl.anchormen.redis.common.RedisConfig
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import scala.util.{Failure, Success}

/**
  * Created by elsioufy on 30-5-16.
  */

class RedisRDDWriter (rdd: RDD[(String, String)]) extends Serializable with LazyLogging{

  private def saveToRedis(redisConfig: RedisConfig, operation: Int): Unit = {
    rdd.foreachPartition(dataItr => {
      import nl.anchormen.redis.common.RedisInstanceManager._
      getRedisUnifiedAPI(redisConfig) match {
        case Success(j) => {
          logger.info("successfully connected to Redis")
          dataItr.foreach{
            case(k,v)=> {
              operation match {
                case 1 => j.add(k, v)/*KV*//*collision: overwrite*/
                case 2 => j.ladd(k,v)/*List*//*always append*/
                case 3 => j.sadd(k,v)/*Set*//*collision: overwrite*/
                case _ => logger.error("invalid input operation: (" + operation + ") doing nothing")
              }
            }
          }
          j.close()
        }
        case Failure(f) => logger.error("Could not connect to Redis=> "+ f.getMessage)
      }
    })
  }

  def saveToRedis(redisConfig: RedisConfig): Unit = saveToRedis(redisConfig, 1)
  def saveToRedisList(redisConfig: RedisConfig): Unit = saveToRedis(redisConfig, 2)
  def saveToRedisSet(redisConfig: RedisConfig): Unit = saveToRedis(redisConfig, 3)
}

object RedisRDDWriter {
  implicit def addRedisRDDWriterFunctions(rdd: RDD[(String, String)]): RedisRDDWriter = new RedisRDDWriter(rdd)
}
