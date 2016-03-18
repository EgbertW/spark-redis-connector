# Spark Redis Connector
[jedis]: <https://github.com/xetorthio/jedis>
This is a minimal Spark library that allows to stream data from a Redis Node or a Redis Cluster. The library is based on [Jedis] client for Redis. It uses Jedis 2.8.0 which is currently under development, however ...this is the only version that supports Jedis Cluster specification. In the near future, we plan to provide streaming data to Redis.

### Requirements
- This library is developed on Spark 1.6.0 and Scala 2.11.7 and has been tested on Spark 1.5.2 and Scala 2.10.6. So No worries about versioning (it's very simple anyways)
- Jedis is fully compatible with redis 2.8.x and 3.0.x.

### Using the Library
[sbt-assembly]:<https://github.com/sbt/sbt-assembly>
This is an sbt project. We have created a FatJar using [sbt-assembly] which is included in ```/targert/spark-redis-receiver-assembly-0.0.1-SNAPSHOT.jar```. For more information about the project dependencies you can check the provided ```build.sbt```

### Provided Functionality 
Currently receiving data from a (set of) redis **List(s)** or **Set(s)** is supported. The user is typically required to provide a Map with a set of configuration parameters (described next) and a Set of List/Set Keys. The resulting stream will be of ```Tuple (String: Key, String: popedValue)```. It's important to be clear that values are ***popped*** from the Redis Lists/Sets. The implemented Spark StorageLevel is StorageLevel.MEMORY_AND_DISK_SER_2 (if too technical; forget about it ... that's the default anyways).

***Configuration Parameters***
- host: single Redis host address  [default: localhost]
- port: corresponding Redis port [default: 6379]
- cluster: specifies if spark needs to connect to a RedisCluster or a single RedisNode {true, fale} [default: false]
- struct: specifies the Redis data structure to receive data from  {list, set} [default: list]
- timeout: specifies if in case no data is found in the target Lists/Sets, shall the receiver wait for a specific timeout (in ms) then continue receiving data or not. [default: 200]. To disable this feature set timeout=0

In case of a cluster mode, i.e. cluster=true, the remaining cluster nodes will be automatically discovered; this is directly available from Jedis.



### Code Example
```scala
/*importing the library (for this to work add the fat jar to your lib folder)*/
import nl.anchormen.receiver.RedisUtils
/*ssc: StreamingContext initialized somewhere*/
/*in case of cluster mode, specifying a single host/port is sufficient. The rest will be discovered automatically*/
val clusterParams = Map ("host" -> "localhost", "port" -> "6379", "cluster" -> "true", "timeout" -> "0", "struct" -> "set")
val keySet = Set("Key1", "Key2", "Key3") /*set of keys*/
RedisUtils.createStream(ssc, clusterParams, keySet).print()
```
For more code examples Check ```/tests/nl.anchormen.receiver.RedisConsumer ```

### Testing the library (Check!)
[Grokzen/docker-redis-cluster]:<https://github.com/Grokzen/docker-redis-cluster>
The simplest way to test the library is to run a Dockerized Redis cluster/Node and run the provided integration tests. 
We recommend using the Docker image provided by [Grokzen/docker-redis-cluster]. Given that you have Docker installed & a working Spark isntallation. ***You only need to do the following***
```sh
1. $ docker run -d -p 7000:7000 -p 7001:7001 -p 7002:7002 -p 7003:7003 -p 7004:7004 -p 7005:7005 -p 7006:7006 -p 7007:7007 grokzen/redis-cluster:3.0.6  
```
+ This runs a single Docker container containing 8 Redis Nodes. 6 forming a cluster of 3 Masters & 3 Slaves, and 2 standalone nodes (see documentation  [Grokzen/docker-redis-cluster]). The Redis nodes are listening to the clients at ports 7000 to 7007 respectively, which we map on our localhost using Dockers -p commands.
+ Run the tests 
    + ```nl.anchormen.receiver.RedisProducer``` continuously pushes data to the RedisCluster and the standalone RedisNode listening @ port 7007
    + ```nl.anchormen.receiver.RedisConsumer``` uses the RedisReceriver to stream data from both settings. 
    

### More Information
[Redis]: <http://redis.io/>
[RedisCluster]:<http://redis.io/topics/cluster-tutorial>
For more information about [Redis], [RedisCluster], and [Jedis] you can visit their corresponding links.

### Want to contribute or have other questions?
Please Do !
