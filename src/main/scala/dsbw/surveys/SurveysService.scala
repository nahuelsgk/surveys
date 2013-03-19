package dsbw.surveys

import org.bson.types.ObjectId
import java.util.Date

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys() {
    println("Hay que devolver una lista de encuestas")
  }
}
