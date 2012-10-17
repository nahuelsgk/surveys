package dsbw.chirps

import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.json.JSON
import java.util.Date
import org.bson.types.ObjectId
import Config.{dbHostName, dbPort, dbName, username, pwd, webServerPort}

/** Case class representing the scheme of the author JSON objects returned by the API */
case class Author(name:String, username:String, avatar:String)

/** Case class representing the scheme of the chirp JSON objects returned by the API */
case class Chirp(author:Author, date:Date, message:String)

/** Chirps API */
class ChirpsApi(chirpsRepository: ChirpsRepository,chirpersRepository: ChirpersRepository) extends Api {

  private def getChirperById(id:ObjectId) = chirpersRepository.findById(id).map(ar=>Author(ar.name,ar.username,ar.avatar))

  def listChirps = chirpsRepository.findAll.map(cr => Chirp(getChirperById(cr.author).get,cr.date,cr.message))

  def service(method: String, uri: String, parameters: Map[String, List[String]] = Map(), headers: Map[String, String] = Map(), body: Option[JSON] = None): Response = {
    (method + " " + uri) match {
      case "GET /api/chirps" => Response(HttpStatusCode.Ok, listChirps)
      case _ => Response(HttpStatusCode.Ok, "Hello world!")
    }
  }

}

object ChirpsApp extends App {

  val db = new DB(dbHostName, dbPort, dbName, username, pwd)
  val chirpsRepository = new ChirpsRepository(new ChirpsDao(db))
  val chirpersRepository = new ChirpersRepository(new ChirpersDao(db))

  val server = new Server(new ChirpsApi(chirpsRepository,chirpersRepository), webServerPort)
  server.start()

}
