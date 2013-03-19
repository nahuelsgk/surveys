package dsbw.surveys

import org.bson.types.ObjectId
import java.util.Date
import com.mongodb.casbah.query.Imports._
import dsbw.surveys.SurveysRecord

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys(){
    println("--- listSurveys INIT")

    /* creacio db */
    val db= new DB(Config.dbHostName, Config.dbPort, Config.dbName, Config.username, Config.pwd)
    /* proves */
    val tmp= db.surveys.find()
    println("enquestes a la db: " + tmp.size)

    /* fem servir els daos */
    val s= new SurveysDao(db)
    val l = s.findAll
    println(l.length)
    for (el <- l) {
      println(el._id)
    }

    val l2= s.findOne(Map("title"-> "Enquesta1"))
    println(l2.get._id + ": "+ l2.get.title)

    var ns= SurveysRecord(new ObjectId(), "Enquesta 3", "init", "fi")
    s.save(ns)
    println("--- listSurveys FI")
  }
}
