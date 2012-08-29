package dsbw.chirps

import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.json.JSON
import java.util.Date
import org.bson.types.ObjectId
import Config._

case class Author(name:String, username:String, avatar:String)
case class Chirp(author:Author, date:Date, message:String)

class ChirpsApi extends Api {
  val db = new DB(dbHostName, dbPort, dbName, username, pwd)
  val chirpsRepository = new ChirpsRepository(new ChirpsDao(db))
  val chirpersRepository = new ChirpersRepository(new ChirpersDao(db))

  def sayHello = "Hello world!"

  private def getChirperById(id:ObjectId) = chirpersRepository.findById(id).map(ar=>Author(ar.name,ar.username,ar.avatar))

  def listChirps = chirpsRepository.findAll.map(cr => Chirp(getChirperById(cr.author).get,cr.date,cr.message))

  def service(method: String, uri: String, parameters: Map[String, List[String]] = Map(), headers: Map[String, String] = Map(), body: Option[JSON] = None): Response = {
    (method + " " + uri) match {
      case "GET /api/chirps" => Response(HttpStatusCode.Ok, listChirps)
      case _ => Response(HttpStatusCode.Ok, sayHello)
    }
  }

}

object ChirpsApp extends App {

  val server = new Server(new ChirpsApi(), webServerPort)
  server.start()

}
