package dsbw.chirps

import org.bson.types.ObjectId
import dsbw.mongo.MongoDao

/** Case class that documents the document scheme in the chirper MongoDB collection */
case class ChirperRecord(_id:ObjectId = new ObjectId(), username:String, name:String, avatar:String)

class ChirpersDao(db:DB) extends MongoDao[ChirperRecord](db.chirpers) {
}

class ChirpersRepository(dao: ChirpersDao) {

  def findById(id:ObjectId) = dao.findOneByID(id)

  //dao.save(ChirperRecord(username="agile_jordi", name="Jordi Pradel", avatar="/img/avatar4.png"))

}

