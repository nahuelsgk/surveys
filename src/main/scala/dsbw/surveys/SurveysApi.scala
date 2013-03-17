package dsbw.surveys

import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.json.JSON
import Config.{dbHostName, dbPort, dbName, username, pwd, webServerPort}

/* Surveys API */
class SurveysApi(surveysService:SurveysService) extends Api {

  def service(method: String, uri: String, parameters: Map[String, List[String]] = Map(), headers: Map[String, String] = Map(), body: Option[JSON] = None): Response = {
    println("--Called Service--");
    (method + " " + uri) match {
      case "POST /api/survey" => Response(HttpStatusCode.Ok, surveysService.listSurveys)
      case _ => Response(HttpStatusCode.Ok, "Hello world!")
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
