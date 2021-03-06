package bk.kafka.app

import java.util.concurrent.atomic.AtomicLong

import akka.Done
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.Future

class OffsetDb extends LazyLogging {
  private val offset = new AtomicLong

  def save(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
    logger.debug(s"DB.save: ${record.value()}")
    offset.set(record.offset)
    Future.successful(Done)
  }

  def loadOffset(): Future[Long] =
    Future.successful(offset.get)

  def update(data: String): Future[Done] = {
    logger.debug(s"DB.update: $data")
    Future.successful(Done)
  }
}
