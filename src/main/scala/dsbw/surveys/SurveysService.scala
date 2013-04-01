package dsbw.surveys

import org.bson.types.ObjectId
import java.util.Date
import dsbw.json.JSON
import dsbw.server.{HttpStatusCode, Response}
import collection.mutable.ListBuffer

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys() : Response = {
      val list = surveysRepository.listSurveys()
      val listSurvey = new ListBuffer[Survey]

      list.foreach((sur: SurveysRecord) =>  {
          val aux = new Survey(sur.title, sur.since, sur.until, sur._id.toString);
          listSurvey += aux }
      )
      val json = JSON.toJSON(listSurvey).value
      println("body response: " + json)
      Response(HttpStatusCode.Ok, null, json)
  }

  def createSurvey(body: Option[JSON]) : Response = {
    println("Request body: " + body)
    try {
        if (body.nonEmpty) {
            //Es parseja el body
            val survey = JSON.fromJSON[Survey](body.get)
            println("Survey parsed: " + survey)
            //S'emmagatzema la nova Survey i s'obte la id que li ha assignat la BD

            val surveyRecord = new SurveysRecord(title = survey.title, since = survey.since, until = survey.until)
            println("Survey created: " + surveyRecord)
            surveysRepository.createSurvey(surveyRecord)
            val id = surveyRecord._id

            //Es construeix la resposta amb la nova URI amb la id que ha proporcionat la BD
            val uri = "/api/survey/" + id
            val headers = Map("Location" -> uri)
            Response(HttpStatusCode.Created, headers, "{}")
        }
        else {
          Response(HttpStatusCode.BadRequest, null)
        }
    }
    catch {
        case e : Throwable => {
          println(e)
        }
        Response(HttpStatusCode.BadRequest, null)
    }
  }

  def putSurvey(id: String, body: Option[JSON]) : Response = {
      println("Request body: " + body)
      try  {
        if (body.nonEmpty) {

            //Es parseja el body
            val survey = JSON.fromJSON[Survey](body.get)

            //Es fa un update de la survey
            val objectId = new org.bson.types.ObjectId(id)
            val surveyRecord = new SurveysRecord(_id = objectId, title = survey.title, since = survey.since, until = survey.until)
            surveysRepository.updateSurvey(surveyRecord)
            println("Survey updated: " + surveyRecord)

            //Es retorna OK si tot ha anat be
            Response(HttpStatusCode.NoContent, null)
        } else {
          Response(HttpStatusCode.BadRequest, null)
        }
      }
      catch {
        case e: Throwable => {
          println(e)
        }
        Response(HttpStatusCode.BadRequest, null)
      }
  }

  def getSurvey(id: String){
    println("We must return a survey "+id)
  }

}
