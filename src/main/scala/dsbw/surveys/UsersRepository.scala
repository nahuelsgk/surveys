package dsbw.surveys

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._
import dsbw.mongo.MongoDao
import scala.collection.mutable.ListBuffer
import com.mongodb.casbah.commons.TypeImports.ObjectId
import dsbw.domain.survey.StatesSurvey

case class UserRecord(
                       _id: ObjectId = new ObjectId(),
                       userName: String,
                       password: String,
                       email: String,
                       surveys: List[String] = List()
                       )

class UsersRepository(dao: UsersDao) {

      def createUser(userRecord: UserRecord) {
            dao.save(userRecord)
      }

      def getUser(id: String): UserRecord = {
            dao.findOneByID(new ObjectId(id)).get;
      }

      def getUserOption(id: String): Option[UserRecord] = {
            dao.findOneByID(new ObjectId(id));
      }

      def loginUser(userName: String, pass: String): Option[UserRecord] = {
            val query = Map("userName" -> userName, "password" -> pass)
            dao.findOne(query)
      }

      def existsUserName(userName: String): Boolean = {
            val query = Map("userName" -> userName)
            val f = dao.findOne(query)
            println("User " + userName + " exists " + f.isDefined)
            f.isDefined
      }

    def pushSurvey(idUser:String, idSurvey: String) {
        dao.updateById(
            new ObjectId(idUser),
            MongoDBObject("$push" -> (MongoDBObject("surveys" -> idSurvey)))
        )
        println("Survey pushed to user: " + getUser(idUser))
    }
}

class UsersDao(db: DB) extends MongoDao[UserRecord](db.users) {


}


