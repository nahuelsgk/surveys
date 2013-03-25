package dsbw.surveys

import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.json.JSON
import Config.{dbHostName, dbPort, dbName, username, pwd, webServerPort}

/* Surveys API */
class SurveysApi(surveysService:SurveysService) extends Api {

  def service(method: String, uri: String, parameters: Map[String, List[String]] = Map(), headers: Map[String, String] = Map(), body: Option[JSON] = None): Response = {

    val patternGetSurveyId = "GET /api/survey/(\\d+)".r
    
    (method + " " + uri) match {
      case "POST /api/survey" => Response(HttpStatusCode.Ok, "hi dude!", surveysService.createSurvey(body))
      case patternGetSurveyId(id) => Response(HttpStatusCode.Ok, null, surveysService.getSurvey(id))
      case "GET /api/surveys" => Response(HttpStatusCode.Ok, null, surveysService.listSurveys())
      case _ => Response(HttpStatusCode.Ok, null,"Hello world!")
    }
  }

}

object SurveysApp extends App {

  val db = new DB(dbHostName, dbPort, dbName, username, pwd)
  val surveysRepository = new SurveysRepository(new SurveysDao(db))
  val surveysService = new SurveysService(surveysRepository)

  val server = new Server(new SurveysApi(surveysService), webServerPort)
  server.start()

}
