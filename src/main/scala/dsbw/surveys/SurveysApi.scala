package dsbw.surveys

import Config.{dbHostName, dbPort, dbName, username, pwd, webServerPort}
import dsbw.json.JSON
import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.domain.survey.Survey
import dsbw.domain.survey.SurveyAnswer
import dsbw.domain.survey.Answer
import org.bson.types.ObjectId

/* Surveys API */
class SurveysApi(surveysService: SurveysService) extends Api {

    val PatternGetSurveyId  = "GET /api/survey/(\\w+)".r
    val PatternPutSurveyId  = "PUT /api/survey/(\\w+)".r
    val PatternGetAnswers   = "GET /api/survey/(\\w+)/answers/(\\w+)/".r
    val PatternPutAnswers   = "PUT /api/survey/(\\w+)/answers/(\\w+)/".r
    val PatternPostAnswers  = "POST /api/survey/(\\w+)/answers/".r

    def service(
        method: String,
        uri: String,
        parameters: Map[String, List[String]] = Map(),
        headers: Map[String, String] = Map(),
        body: Option[JSON] = None
    ): Response = {
        (method + " " + uri) match {
            case "POST /api/survey" => postSurvey(body)
            case PatternGetAnswers(idSurvey, idUser) => getSurveyUser(idSurvey, idUser, body)
            case PatternGetSurveyId(id) => getSurveyById(id)
            case PatternPutAnswers(idSurvey, idUser) => putAnswers(idSurvey, idUser, body)
            case PatternPostAnswers(idSurvey)=> postAnswers(idSurvey, body)
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

    private def getSurveyUser(idSurvey: String, idUser: String, body: Option[JSON]): Response = {
        println("*** SurveysApi.getSurveyUser()")
        println("Survey id: "+ idSurvey+ "; User id: "+ idUser)
        println("Request body: " + body)

        println("Not implemented yet!")
        Response(HttpStatusCode.Ok)
    }

    private def putAnswers(idSurvey: String, idUser: String, body: Option[JSON]): Response = {
        println("*** SurveysApi.putAnswers()")
        println("Survey id: "+ idSurvey+ "; User id: "+ idUser)
        println("Request body: " + body)

        try{
        if(body.nonEmpty) {
            val surveyAnswers = JSON.fromJSON[SurveyAnswer](body.get)
            surveyAnswers.setId(idUser)
            println("Survey Answer: " + surveyAnswers)
            surveysService.putAnswers(idSurvey, surveyAnswers)
            Response(HttpStatusCode.NoContent)
        } else {
            Response(HttpStatusCode.BadRequest)
        }
        }catch {
            case e: Throwable => {
                println(e)
                println(e.getStackTraceString)
            }
            Response(HttpStatusCode.BadRequest)
        }
    }

    private def postAnswers(idSurvey: String, body: Option[JSON]): Response = {
        println("*** SurveysApi.postAnswers()")
        println("Survey id: "+idSurvey)
        println("Request body: " + body)

        try{
            if(body.nonEmpty) {
                val surveyAnswers = JSON.fromJSON[SurveyAnswer](body.get)
                val userId= new ObjectId().toString
                println("Generated User id: "+ userId)
                surveyAnswers.setId(userId)
                println("Survey Answer: " + surveyAnswers)
                surveysService.saveAnswers(idSurvey, surveyAnswers)
                Response(HttpStatusCode.Ok, null, JSON.toJSON[Map[String, String]](Map("userId" -> userId)))
            }
            else {
                Response(HttpStatusCode.BadRequest)
            }
        }catch {
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
                val allowed = surveysService.updateSurvey(survey)

                //Es retorna OK si tot ha anat be
                Response(if (allowed)  HttpStatusCode.NoContent else HttpStatusCode.Forbidden)
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
