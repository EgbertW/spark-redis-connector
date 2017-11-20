package nl.anchormen.redis.receiver

/**
  * Created by elsioufy on 2-1-16.
  */

import nl.anchormen.redis.common.RedisConfig
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.Receiver

private class RedisReceiverInputDStream(@transient ssc_ : StreamingContext, redisConfig: RedisConfig,
  keySet: Set[String]) extends ReceiverInputDStream[(String, String)](ssc_) with LazyLogging {

  val _storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2
  override def getReceiver(): Receiver[(String, String)] = {
    redisConfig.getStreamingStruct match {
      case "list" => new RedisListReceiver(redisConfig, keySet, _storageLevel)
      case "set" => new RedisSetReceiver(redisConfig, keySet, _storageLevel)
      case _ => throw new IllegalArgumentException("unsupported Redis Structure. The only supported structuers are (1)list and (2)set")
    }
  }
}
