package nl.anchormen.redis.receiver

import nl.anchormen.redis.common.RedisConfig
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream

/**
  * Created by elsioufy on 2-1-16.
  */

object RedisUtils {
  /**
    * Create an input stream that receives messages from a set of redis Lists or Sets.
    * @param ssc          StreamingContext object
    * @param redisConfig  RedisConfig object
    * @param keySet       keySet regarding lists/sets to read from
    * @throws IllegalArgumentException if struct is not either list or set
    */

  def createStream(ssc: StreamingContext, redisConfig: RedisConfig, keySet: Set[String]):
  ReceiverInputDStream[(String, String)] = { new RedisReceiverInputDStream(ssc, redisConfig, keySet)}

}