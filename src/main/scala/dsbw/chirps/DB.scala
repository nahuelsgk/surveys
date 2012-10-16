package dsbw.chirps

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject

/** A class representing the Mongo DB used. Each collection is a value of the class. */

class DB(hostName: String, port: Int, dbName: String, username: String, pwd: String){
  val db = MongoConnection(hostName, port)(dbName)
  db.authenticate(username, pwd)
  val chirps = db("chirps")
  val chirpers = db("chirpers")

  /** Initialize the DB here, mostly, ensure indexes */
  def init() {
    chirps.ensureIndex(MongoDBObject("date" -> -1))
  }

  /** In case you want to reset the DB. Warning! This will remove all the data! */
  def reset() {
    drop()
    init()
  }

  /** Drop all the DB destroying all the data */
  def drop() {
    chirps.drop()
    chirpers.drop()
  }
}
