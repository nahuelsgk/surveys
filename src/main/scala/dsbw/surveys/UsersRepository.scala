package dsbw.surveys

import org.bson.types.ObjectId
import dsbw.mongo.MongoDao
import com.mongodb.casbah.commons.TypeImports._
import dsbw.surveys.UserRecord

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
}

class UsersDao(db: DB) extends MongoDao[UserRecord](db.users) {


}


