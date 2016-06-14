package nl.anchormen.redis.receiver

import nl.anchormen.redis.common.{JedisAPIWrapper, RedisConfig}
import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import scala.util.{Failure, Success}

/**
  * Created by elsioufy on 16-1-16.
  */

abstract class RedisReceiver(redisConfig: RedisConfig, keySet: Set[String], storageLevel: StorageLevel)
  extends Receiver[(String, String)](storageLevel) with Logging {

  override def onStart(): Unit = {
    implicit val akkaSystem = akka.actor.ActorSystem()
    import nl.anchormen.redis.common.RedisInstanceManager._
    getRedisUnifiedAPI(redisConfig) match {
      case Success(j) => log.info("onStart, Connecting to Redis API")
        new Thread("Redis List Receiver") {
          override def run() {
            receive(j)
          }
        }.start()
      case Failure(f) => log.error("Could not connect"); restart("Could not connect", f)
    }
  }

  def receive(j: JedisAPIWrapper) = {
    try {
      log.info("Accepting messages from Redis")
      /*keeps running until the streaming application isStopped*/
      while (!isStopped())  {
        var allNull = true
        keySet.iterator.foreach(k => {
          val res = getData(j, k)
          if (res != null) {
            allNull = false
            log.info("received data from key: " + k)
            /*we don't implement a reliable receiver since Redis doesnt support message acknoledgements*/
            store((k, res))
          }
        })
        /*in case there isn't any data, maybe u want chill abit !*/
        if (allNull)
          Thread.sleep(redisConfig.getStreamingTimeout)
      }
    }
    /*In case any failure occurs; log the failure and try to restart the receiver*/
    catch {
      case e : Throwable => {
        log.error("Got this exception: ", e)
        restart("Trying to connect again")
      }
    }
    /*closing the redis connection*/
    finally {
      log.info("The receiver has been stopped - Terminating Redis Connection")
      try { j.close()} catch { case _: Throwable => log.error("error on close connection, ignoring")}
    }
  }

  def getData (j: JedisAPIWrapper, k: String) : String

  override def onStop(): Unit = {
    log.info("onStop ...nothing to do!")
  }

}

class RedisListReceiver(redisConfig: RedisConfig, keySet: Set[String], storageLevel: StorageLevel)
  extends RedisReceiver(redisConfig, keySet, storageLevel) {
  override def getData(j: JedisAPIWrapper, k: String): String = j.lpop(k)
}

class RedisSetReceiver(redisConfig: RedisConfig, keySet: Set[String], storageLevel: StorageLevel)
  extends RedisReceiver(redisConfig: RedisConfig, keySet, storageLevel) {
  override def getData(j: JedisAPIWrapper, k: String): String = j.spop(k)
}