# Spark Redis Connector
[jedis]: <https://github.com/xetorthio/jedis>

This is a minimal Spark library that allows Spark Executors to Read/Write data from/to Redis. The library is based on [Jedis] client for Redis. It uses Jedis 2.8.0 and supports Redis Clusters 

### Requirements

- This library is developed on Spark 1.6.1 and Scala 2.11.7 and is compatible with Spark 1.6.x, 1.5.x and Scala 2.11.x, 2.10.x
- Jedis is fully compatible with redis 2.8.x and 3.0.x.


### Using the Library

[sbt-assembly]:<https://github.com/sbt/sbt-assembly>

This is an sbt project. We have created a FatJar using [sbt-assembly] which is included in ```/target/spark-redis-connector-assembly-0.0.2-SNAPSHOT.jar```. For more information about the project dependencies you can check the provided ```build.sbt```


### Provided Functionality:

- **Streamming data from Redis: ** [Sets] [Lists]
 - consumed values are ***popped***
 - The resulting stream will be of ```Tuple (String: Key, String: popedValue)```
 - Example
 - 
    ```scala
    import nl.anchormen.redis.common.RedisConfig
    import nl.anchormen.redis.receiver.RedisUtils
	...
    val ssc /*streaming context*/
    val redisConfig /*see below how to create a redisConfig object*/
    
    val redisKeys = Set("Key1", "Key2", "Key3") /*a set holding [List] or [Set] keys to stream data from*/
    val redisStream: DStream[(String, String)] = RedisUtils.createStream(ssc, redisConfig, redisKeys)
    ```
- **Streaming data to Redis: ** not supported
- **Reading data from Redis as RDD: ** not supported
- **Writing data to Redis: ** [KV pairs][List][Set][Sorted Set]
 - Example [KV][List][Set]
 - 
	```scala
    val rdd: RDD[(String, String)] /*KV RDD representing Redis (Key,Value) respectively*/
    import nl.anchormen.redis.writer.RedisRDDWriter._
    rdd.saveToRedis(redisConfig)
    rdd.saveToRedisList(redisConfig)
    rdd.saveToRedisSet(redisConfig)
    ```
 - Example [Sorted Set]   
	```scala
    val rdd: RDD[(String, (String, Double)] /*KV RDD representing Redis (Key,(Value,Rank)) respectively*/
    import nl.anchormen.redis.writer.RedisSortedRDDWriter._
    rdd.saveToRedisSortedSet(redisConfig)
    ```



### RedisConfig
Holds RedisConfiguration to connect to a Redis Cluster or Node

 - General Config Parameters
  - ***host***: single Redis host address  [default: localhost]
  - ***port***: corresponding Redis port [default: 6379]
  - ***cluster***: specifies if spark needs to connect to a RedisCluster or a single RedisNode {true, fale} [default: false]

 - Streaming Config Parameters
  - ***streaming.struct***: specifies the Redis data structure to receive data from  {list, set} [default: list]
  - ***streaming.timeout***: specifies if in case no data is found in the target Lists/Sets, shall the receiver wait for a specific timeout (in ms) then continue receiving data or not. [default: 200]. To disable this feature set timeout=0

 - Example
    ``` scala
    import nl.anchormen.redis.common.RedisConfig    
    val redisConfig = new RedisConfig()
       .setHost("localhost")
       .setPort(6379)
       .setCluster(false)
       .setStreamingStruct("list")
       .setStreamingTimeout(200)

    ```

In case of a cluster mode, i.e. cluster=true, the remaining cluster nodes will be automatically discovered; this is directly available from Jedis.




### More Information
[Redis]: <http://redis.io/>
[RedisCluster]:<http://redis.io/topics/cluster-tutorial>
For more information about [Redis], [RedisCluster], and [Jedis] you can visit their corresponding links.


### Want to contribute or have other questions?
Please Do !
