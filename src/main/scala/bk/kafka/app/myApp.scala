package bk.kafka.app

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object myApp
  extends App
    with LazyLogging {

  val system = ActorSystem("mysys")

  implicit val materializer = ActorMaterializer.create(system)

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val topicName = "test"

  system.scheduler.schedule(1.second,
    10.second
  )(Source(1 to 23)
    .map(_.toString)
    .map { elem =>
      new ProducerRecord[Array[Byte], String](topicName, elem)
    }
    .runWith(Producer.plainSink(producerSettings)))


  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val db = new OffsetDb
  db.loadOffset().foreach { fromOffset =>
    val partition = 0
    val subscription = Subscriptions.assignmentWithOffset(
      new TopicPartition(topicName, partition) -> fromOffset
    )
    val done =
      Consumer.plainSource(consumerSettings, subscription)
        .mapAsync(1)(db.save)
        .runWith(Sink.ignore)
  }
}
