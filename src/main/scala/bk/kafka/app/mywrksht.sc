import java.util.concurrent.atomic.AtomicLong

import bk.kafka.app.OffsetDb

import scala.concurrent.Future

val mydb = new OffsetDb

mydb.loadOffset()

val myAlong = new AtomicLong

Future.successful(myAlong.get()).foreach()