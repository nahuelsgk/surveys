package dsbw.surveys

import org.bson.types.ObjectId
import java.util.Date
import dsbw.json.JSON
import dsbw.server.{HttpStatusCode, Response}

case class Question(question:String)

class SurveysService(surveysRepository: SurveysRepository) {

  def listSurveys() {
    println("We must return a list of surveys")
  }

  def createSurvey(body: Option[JSON]) : Response = {
    println("Request body: " + body)
    try {
        if (body.nonEmpty) {
            //Es parseja el body
            val survey = JSON.fromJSON[Survey](body.get)

            //S'emmagatzema la nova Survey i s'obte la id que li ha assignat la BD
            val id = "newSurvey"
            println("Survey parsed: " + survey)

            //Es construeix la resposta amb la nova URI amb la id que ha proporcionat la BD
            val uri = "/api/survey/" + id
            val headers = Map("Location" -> uri)
            Response(HttpStatusCode.Created, headers)
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

  def putSurvey(body: Option[JSON]) : Response = {
      println("Request body: " + body)
      try  {
        if (body.nonEmpty) {

            //Es parseja el body
            val survey = JSON.fromJSON[Survey](body.get)

            //Es fa un update de la survey
            println("Survey parsed: " + survey)

            //Es retorna OK si tot ha anat be
            Response(HttpStatusCode.Ok, null)
        }
        else {
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
