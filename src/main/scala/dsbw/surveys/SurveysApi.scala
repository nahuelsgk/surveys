package dsbw.surveys

import Config.{dbHostName, dbPort, dbName, username, pwd, webServerPort}
import dsbw.json.JSON
import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.domain.survey.Survey

/* Surveys API */
class SurveysApi(surveysService: SurveysService) extends Api {

    val PatternGetSurveyId = "GET /api/survey/(\\w+)".r
    val PatternPutSurveyId = "PUT /api/survey/(\\w+)".r

    def service(
        method: String,
        uri: String,
        parameters: Map[String, List[String]] = Map(),
        headers: Map[String, String] = Map(),
        body: Option[JSON] = None
    ): Response = {
        (method + " " + uri) match {
            case "POST /api/survey" => postSurvey(body)
            case PatternGetSurveyId(id) => getSurveyById(id)
            case PatternPutSurveyId(id) => putSurvey(id, body)
            case "GET /api/surveys" => getAllSurveys
            case _ => Response(HttpStatusCode.NotFound)
        }
    }


    private def postSurvey(body: Option[JSON]): Response = {
        println("*** SurveysApi.postSurvey()")
        try {
            if (body.nonEmpty) {

                val surveyInfo = surveysService.createSurvey(JSON.fromJSON[Survey](body.get))

                //Es construeix la resposta amb la nova URI amb la id que ha proporcionat la BD
                val uri = "/api/survey/" + surveyInfo("id")
                val headers = Map("Location" -> uri)
                Response(HttpStatusCode.Created, headers = headers, body = JSON.toJSON[Map[String, String]](surveyInfo))
            } else {
                Response(HttpStatusCode.BadRequest)
            }
        }
        catch {
            case e: Throwable => {
                println(e)
                println(e.getStackTraceString)
            }
            Response(HttpStatusCode.BadRequest)
        }
    }

    private def putSurvey(id: String, body: Option[JSON]): Response = {
        println("*** SurveysApi.putSurvey()")
        println("Request body: " + body)
        try {
            if (body.nonEmpty) {
                //Es parseja el body
                val survey = JSON.fromJSON[Survey](body.get)
                println("Parsed body: "+ survey)
                surveysService.updateSurvey(id, survey)

                //Es retorna OK si tot ha anat be
                Response(HttpStatusCode.NoContent)
            } else {
                Response(HttpStatusCode.BadRequest)
            }
        }
        catch {
            case e: Throwable => {
                println(e)
                println(e.getStackTraceString)
            }
            Response(HttpStatusCode.BadRequest)
        }
    }

    private def getSurveyById(id: String): Response = {
        val myenq= surveysService.getSurvey(id);
        val tmp1= JSON.toJSON(myenq);
        Response(HttpStatusCode.Ok, null, tmp1);
    }

    private def getAllSurveys: Response = {
        println("*** SurveysApi.getAllSurveys()")
        val json = JSON.toJSON(surveysService.listSurveys()).value
        println("body response: " + json)
        Response(HttpStatusCode.Ok, null, json)
    }
}

object SurveysApp extends App {
    val db = new DB(dbHostName, dbPort, dbName, username, pwd)
    val surveysRepository = new SurveysRepository(new SurveysDao(db))
    val surveysService = new SurveysService(surveysRepository)

    val server = new Server(new SurveysApi(surveysService), webServerPort)
    server.start()
}
