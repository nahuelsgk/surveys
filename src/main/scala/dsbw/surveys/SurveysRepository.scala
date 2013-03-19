package dsbw.surveys

import dsbw.mongo.MongoDao
import org.bson.types.ObjectId
import java.util.Date
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._

/** A record representing the scheme of Surveys stored in the surveys collection */
//case class SurveysRecord(id: ObjectId, question:String, date:Date=new Date())
case class SurveysRecord(_id: ObjectId= new org.bson.types.ObjectId(), title:String, start:String, end:String)

/** Surveys Data Access Object */
class SurveysDao(db:DB) extends MongoDao[SurveysRecord](db.surveys) {


}

/** Surveys Repository
  * A repository uses a DAO but doesn't expose all the DB centric API of the DAO
  */
class SurveysRepository(dao: SurveysDao) {

}
