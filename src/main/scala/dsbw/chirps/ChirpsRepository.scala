package dsbw.chirps

import dsbw.mongo.MongoDao
import org.bson.types.ObjectId
import java.util.Date
import com.mongodb.casbah.commons.MongoDBObject

case class ChirpRecord(author:ObjectId, message:String, date:Date=new Date(), _id:ObjectId = new ObjectId())

class ChirpsDao(db:DB) extends MongoDao[ChirpRecord](db.chirps) {
}

class ChirpsRepository(dao: ChirpsDao) {

  def findAll = dao.findAll.sort(MongoDBObject("date"-> -1)).toList

//  dao.save(ChirpRecord(author = new ObjectId("503df1850364e1967b576e5d"), message = "And... yet another awesome chirp!"))

}
