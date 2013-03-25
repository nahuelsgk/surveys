package dsbw.surveys

import org.bson.types.ObjectId
import java.util.Date
import dsbw.json.JSON

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys() {
    println("We must return a list of surveys")
  }

  def createSurvey(body: Option[JSON]){
    println("We must create a survey with this parameterers. We need to know how to deserialize it and catch the values")
    println(body.toString)
  }

  def getSurvey(id: String){
    println("We must return a survey "+id)
  }

}
