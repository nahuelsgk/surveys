package dsbw.surveys

import com.mongodb.casbah.query.Imports._
import collection.mutable.ListBuffer

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys(){
    var surveysList = new ListBuffer[SurveysRecord]()
    surveysList = surveysRepository.listSurveys()
    println(surveysList)
  }

  def createSurveys(title: String, start: String, end: String){
    var survey = SurveysRecord(new ObjectId(), title, start, end)
    surveysRepository.createSurvey(survey)
  }
}
