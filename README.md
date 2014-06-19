# Redis Scala client [![Build Status](https://secure.travis-ci.org/debasishg/scala-redis.png)](http://travis-ci.org/debasishg/scala-redis)

## Key features of the library

- Native Scala types Set and List responses.
- Transparent serialization
- Connection pooling
- Consistent Hashing on the client.
- Support for Clustering of Redis nodes.

## Information about redis

Redis is a key-value database. It is similar to memcached but the dataset is not volatile, and values can be strings, exactly like in memcached, but also lists and sets with atomic operations to push/pop elements.

http://redis.io

### Key features of Redis

- Fast in-memory store with asynchronous save to disk.
- Key value get, set, delete, etc.
- Atomic operations on sets and lists, union, intersection, trim, etc.

## Requirements

- sbt (get it at http://www.scala-sbt.org/)

## Installation

Add to `Build.scala` or `build.sbt`

```scala
libraryDependencies ++= Seq(
    "net.debasishg" % "redisclient_2.10" % "2.12"
)
```

## Usage

Start your redis instance (usually redis-server will do it)

    $ cd scala-redis
    $ sbt
    > update
    > console

And you are ready to start issuing commands to the server(s)

Redis 2 implements a new protocol for binary safe commands and replies

Let us connect and get a key:

    scala> import com.redis._
    import com.redis._

    scala> val r = new RedisClient("localhost", 6379)
    r: com.redis.RedisClient = localhost:6379

    scala> r.set("key", "some value")
    res3: Boolean = true

    scala> r.get("key")
    res4: Option[String] = Some(some value)

Let us try out some List operations:

    scala> r.lpush("list-1", "foo")
    res0: Option[Int] = Some(1)

    scala> r.rpush("list-1", "bar")
    res1: Option[Int] = Some(2)

    scala> r.llen("list-1")
    res2: Option[Int] = Some(2)

Let us look at some serialization stuff:

    scala> import serialization._
    import serialization._

    scala> import Parse.Implicits._
    import Parse.Implicits._

    scala> r.hmset("hash", Map("field1" -> "1", "field2" -> 2))
    res0: Boolean = true

    scala> r.hmget[String,String]("hash", "field1", "field2")
    res1: Option[Map[String,String]] = Some(Map(field1 -> 1, field2 -> 2))

    scala> r.hmget[String,Int]("hash", "field1", "field2")
    res2: Option[Map[String,Int]] = Some(Map(field1 -> 1, field2 -> 2))

    scala> val x = "debasish".getBytes("UTF-8")
    x: Array[Byte] = Array(100, 101, 98, 97, 115, 105, 115, 104)

    scala> r.set("key", x)
    res3: Boolean = true

    scala> import Parse.Implicits.parseByteArray
    import Parse.Implicits.parseByteArray

    scala> val s = r.get[Array[Byte]]("key")
    s: Option[Array[Byte]] = Some([B@6e8d02)

    scala> new String(s.get)
    res4: java.lang.String = debasish

    scala> r.get[Array[Byte]]("keey")
    res5: Option[Array[Byte]] = None

## Using Client Pooling

scala-redis is a blocking client, which serves the purpose in most of the cases since Redis is also single threaded. But there may be situations when clients need to manage multiple RedisClients to ensure thread-safe programming.

scala-redis includes a Pool implementation which can be used to serve this purpose. Based on Apache Commons Pool implementation, RedisClientPool maintains a pool of instances of RedisClient, which can grow based on demand. Here's a sample usage ..

```scala
val clients = new RedisClientPool("localhost", 6379)
def lp(msgs: List[String]) = {
  clients.withClient {
    client => {
      msgs.foreach(client.lpush("list-l", _))
      client.llen("list-l")
    }
  }
}
```

Using a combination of pooling and futures, scala-redis can be throttled for more parallelism. This is the typical recommended strategy if you are looking forward to scale up using this redis client. Here's a sample usage .. we are doing a parallel throttle of an lpush, rpush and set operations in redis, each repeated a number of times ..

If we have a pool initialized, then we can use the pool to repeat the following operations. 

```scala
// lpush
def lp(msgs: List[String]) = {
  clients.withClient {
    client => {
      msgs.foreach(client.lpush("list-l", _))
      client.llen("list-l")
    }
  }
}

// rpush
def rp(msgs: List[String]) = {
  clients.withClient {
    client => {
      msgs.foreach(client.rpush("list-r", _))
      client.llen("list-r")
    }
  }
}

// set
def set(msgs: List[String]) = {
  clients.withClient {
    client => {
      var i = 0
      msgs.foreach { v =>
        client.set("key-%d".format(i), v)
        i += 1
      }
      Some(1000)
    }
  }
}
```

And here's the snippet that throttles our redis server with the above operations in a non blocking mode using Scala futures:

```scala
val l = (0 until 5000).map(_.toString).toList
val fns = List[List[String] => Option[Int]](lp, rp, set)
val tasks = fns map (fn => scala.actors.Futures.future { fn(l) })
val results = tasks map (future => future.apply())
```

## Implementing asynchronous patterns using pooling and Futures

scala-redis is a blocking client for Redis. But you can develop high performance asynchronous patterns of computation using scala-redis and Futures. RedisClientPool allows you to work with multiple RedisClient instances and Futures offer a non-blocking semantics on top of this. The combination can give you good numbers for implementing common usage patterns like scatter/gather. Here's an example that you will also find in the test suite. It uses the scatter/gather technique to do loads of push across many lists in parallel. The gather phase pops from all those lists in parallel and does some compuation over them.

Here's the main routine that implements the pattern:

```scala
// set up Executors
val system = ActorSystem("ScatterGatherSystem")
import system.dispatcher

val timeout = 5 minutes

private[this] def flow[A](noOfRecipients: Int, opsPerClient: Int, keyPrefix: String, 
  fn: (Int, String) => A) = {
  (1 to noOfRecipients) map {i => 
    Future {
      fn(opsPerClient, "list_" + i)
    }
  }
}

// scatter across clients and gather them to do a sum
def scatterGatherWithList(opsPerClient: Int)(implicit clients: RedisClientPool) = {
  // scatter
  val futurePushes = flow(100, opsPerClient, "list_", listPush)

  // concurrent combinator: Future.sequence
  val allPushes = Future.sequence(futurePushes)

  // sequential combinator: flatMap
  val allSum = allPushes flatMap {result =>
    // gather
    val futurePops = flow(100, opsPerClient, "list_", listPop)
    val allPops = Future.sequence(futurePops)
    allPops map {members => members.sum}
  }
  Await.result(allSum, timeout).asInstanceOf[Long]
}

// scatter across clietns and gather the first future to complete
def scatterGatherFirstWithList(opsPerClient: Int)(implicit clients: RedisClientPool) = {
  // scatter phase: push to 100 lists in parallel
  val futurePushes = flow(100, opsPerClient, "seq_", listPush)

  // wait for the first future to complete
  val firstPush = Future.firstCompletedOf(futurePushes)

  // do a sum on the list whose key we got from firstPush
  val firstSum = firstPush map {key =>
    listPop(opsPerClient, key)
  }
  Await.result(firstSum, timeout).asInstanceOf[Int]
}
```

## License

This software is licensed under the Apache 2 license, quoted below.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.

