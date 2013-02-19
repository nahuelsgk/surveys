package dsbw.chirps

import org.bson.types.ObjectId
import java.util.Date

case class Author(name:String, username:String, avatar:String)

case class Chirp(author:Author, date:Date, message:String)

class ChirpsService(chirpsRepository: ChirpsRepository,chirpersRepository: ChirpersRepository) {

  private def getChirperById(id:ObjectId) = chirpersRepository.findById(id).map(ar=>Author(ar.name,ar.username,ar.avatar))

  def listChirps = chirpsRepository.findAll.map(cr => Chirp(getChirperById(cr.author).get,cr.date,cr.message))

}
