package dsbw.chirps

import dsbw.mongo.MongoDao
import org.bson.types.ObjectId
import java.util.Date

case class ChirpRecord(_id:ObjectId = new ObjectId(), author:ObjectId, date:Date, message:String)

class ChirpsDao(db:DB) extends MongoDao[ChirpRecord](db.chirps) {
}

class ChirpsRepository(dao: ChirpsDao) {

  def findAll = dao.findAll.toList

//  dao.save(ChirpRecord(author = new ObjectId(), date = new Date(), message = "Hello World!"))

}
