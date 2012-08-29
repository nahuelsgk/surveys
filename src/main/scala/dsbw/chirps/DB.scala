package dsbw.chirps

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject

class DB(hostName: String, port: Int, dbName: String, username: String, pwd: String){
  val db = MongoConnection(hostName, port)(dbName)
  db.authenticate(username, pwd)
  val chirps = db("chirps")
  val chirpers = db("chirpers")

  def init() {
    chirps.ensureIndex(MongoDBObject("date" -> -1))
  }

  def reset() {
    drop()
    init()
  }

  def drop() {
    chirps.drop()
  }
}
