package dsbw.chirps

import dsbw.mongo.MongoDao
import org.bson.types.ObjectId
import java.util.Date
import com.mongodb.casbah.commons.MongoDBObject

/** A record representing the scheme of Chirps stored in the chirps collection */
case class ChirpRecord(author:ObjectId, message:String, date:Date=new Date(), _id:ObjectId = new ObjectId())

/** Chirps Data Access Object */
class ChirpsDao(db:DB) extends MongoDao[ChirpRecord](db.chirps) {
}

/** Chirps Repository
  * A repository uses a DAO but doesn't expose all the DB centric API of the DAO
  */
class ChirpsRepository(dao: ChirpsDao) {

  def findAll = dao.findAll.sort(MongoDBObject("date"-> -1)).toList

//  dao.save(ChirpRecord(author = new ObjectId("503df1850364e1967b576e5d"), message = "And... yet another awesome chirp!"))

}
