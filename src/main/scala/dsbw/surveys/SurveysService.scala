package dsbw.surveys

import org.bson.types.ObjectId
import java.util.Date
import dsbw.json.JSON

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys() {
    println("Hay que devolver una lista de encuestas")
  }

  def createSurvey(body: Option[JSON]){
    println("We must create a survey with this parameterers. We need to know how to deserialize it and catch the values")
    println(body.toString)
  }
}
