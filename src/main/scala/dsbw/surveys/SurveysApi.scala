package dsbw.surveys

import Config.{dbHostName, dbPort, dbName, username, pwd, webServerPort}
import dsbw.json.JSON
import dsbw.server.{Server, HttpStatusCode, Response, Api}
import dsbw.domain.survey.{StatesSurvey, Survey, SurveyAnswer, Answer}
import org.bson.types.ObjectId
import dsbw.domain.user.User
import javax.xml.ws

/* Surveys API */
class SurveysApi(surveysService: SurveysService, usersService: UsersService) extends Api {

    val PatternGetSurveyId      = "GET /api/survey/(\\w+)".r
    val PatternPutSurveyId      = "PUT /api/survey/(\\w+)".r
    val PatternGetAnswers       = "GET /api/survey/(\\w+)/answers/".r
    val PatternGetAnswersUser   = "GET /api/survey/(\\w+)/answers/(\\w+)".r
    val PatternPutAnswers       = "PUT /api/survey/(\\w+)/answers/(\\w+)".r
    val PatternPostAnswers      = "POST /api/survey/(\\w+)/answers/".r

    val PatternGetUserId  = "GET /api/user/(\\w+)".r

    def service(
        method: String,
        uri: String,
        parameters: Map[String, List[String]] = Map(),
        headers: Map[String, String] = Map(),
        body: Option[JSON] = None
    ): Response = {
        (method + " " + uri) match {
            case "POST /api/survey" => postSurvey(getIdCreator(headers), body)
            case PatternGetAnswersUser(idSurvey, idUser) => getAnswersUser(idSurvey, idUser, body)
            case PatternGetAnswers(id) => getAnswers(id)
            case PatternGetSurveyId(id) => getSurveyById(id)
            case PatternPutAnswers(idSurvey, idUser) => putAnswers(idSurvey, idUser, body)
            case PatternPostAnswers(idSurvey)=> postAnswers(idSurvey, body)
            case PatternPutSurveyId(id) => putSurvey(id, body)
            case "GET /api/surveys" => getUserSurveys(getIdCreator(headers))
            case "POST /api/user" => postUser(body)
            case PatternGetUserId(id) => getUser(id)
            case "POST /api/login" => loginUser(body)
            case _ => Response(HttpStatusCode.NotFound)
        }
    }


    private def postSurvey(idCreator: String, body: Option[JSON]): Response = {
        println("*** SurveysApi.postSurvey()")
        try {
            if (body.nonEmpty) {

                val surveyInfo = surveysService.createSurvey(JSON.fromJSON[Survey](body.get), idCreator)
                usersService.putSurvey(idCreator, surveyInfo("id"))
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

    private def getAnswers(id: String): Response = {
        println("*** SurveysApi.getAnswers()")

        val myenq= surveysService.getSurvey(id);
        if (!checkSurveyAvailability(myenq)){
            Response(HttpStatusCode.Ok);
                //, null, JSON.toJSON[Map[String, String]](Map("error" -> "Survey not available")))
        }
        else{
            val tmp1= JSON.toJSON(myenq);
            Response(HttpStatusCode.Ok, null, tmp1);
        }
    }

    private def getAnswersUser(idSurvey: String, idUser: String, body: Option[JSON]): Response = {
        println("*** SurveysApi.getSurveyUser()")
        println("Survey id: "+ idSurvey+ "; User id: "+ idUser)
        println("Request body: " + body)
        try{
             val myans = surveysService.getAnswersUser(idSurvey, idUser)
             val tmpl = JSON.toJSON(myans)
             Response(HttpStatusCode.Ok,null, tmpl )
        }
        catch {
            case e: IllegalStateException => println("Error")
            Response(HttpStatusCode.BadRequest)
        }
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
            if(surveysService.updateAnswers(idSurvey, surveyAnswers)){
                Response(HttpStatusCode.NoContent)
            }
            else{
                Response(HttpStatusCode.BadRequest)
            }
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

    private def getAllSurveys(): Response = {
        println("*** SurveysApi.getAllSurveys()")
        val json = JSON.toJSON(surveysService.listSurveys()).value
        println("body response: " + json)
        Response(HttpStatusCode.Ok, null, json)
    }

    /** Funcions auxiliars **/
    private def checkSurveyAvailability(myenq: Survey): Boolean = {
        println("*** SurveysApi.checkSurveyDates()")
        val df= new java.text.SimpleDateFormat("yyyy-MM-dd")
        // Check dates
        val today = df.format(java.util.Calendar.getInstance().getTime())
        println("since: "+ myenq.since + " - until: "+ myenq.until+ " - today: "+ today)
        if(today< myenq.since || today> myenq.until){
            return false
        }
        // Check state
        /*if (myenq.state!= StatesSurvey.Accepted){
            return false
        }*/
        return true
    }

    private def postUser(body: Option[JSON]): Response = {
        if (body.isDefined) {
            val user = JSON.fromJSON[User](body.get)
            if(!usersService.existsUserName(user)) {
                val id = usersService.createUser(user)
                println("userCreated: " + user)
                val uri = "/api/user/" + id
                val headers = Map("Location" -> uri)
                Response(HttpStatusCode.Created, headers, "{}")
            }
            else {
                println("user " + user + " already exists")
                Response(HttpStatusCode.Forbidden)
            }
        }
        else {
            Response(HttpStatusCode.BadRequest)
        }
    }

    private def getUser(id: String): Response = {
        val user = usersService.getUser(id)
        val json = JSON.toJSON(user)
        Response(HttpStatusCode.Ok, null, json)
    }

    private def loginUser(body: Option[JSON]): Response = {
        if (body.isDefined) {
            val user = JSON.fromJSON[User](body.get)
            val id = usersService.login(user)

            if(id != null)  {
                val uri = "/api/user/" + id
                val headers = Map("Location" -> uri)
                Response(HttpStatusCode.Ok, headers, "{}")
            }
            else Response(HttpStatusCode.Unauthorized)
        }
        else {
            Response(HttpStatusCode.BadRequest)
        }
    }

    def getIdCreator(headers: Map[String, String]): String = {
        var idCreator = "-1";
        println("headers " + headers)
        println("Contains cookie " + headers.contains("cookie"))
        if(headers.contains("Cookie")) {
            val cookie = headers.get("Cookie")
            println("Cookie received " + cookie)
            if (cookie.isDefined) {
                val json = new JSON(cookie.get)
                val cookieJSON = JSON.fromJSON[CookieJSON](json)
                if(cookieJSON.id != null) idCreator = cookieJSON.id
            }
        }
        idCreator
    }

    def getUserSurveys(idCreator: String): Response = {
        val userSurveys = usersService.getSurveys(idCreator)
        val listSurveys = surveysService.listSurveys(userSurveys)
        val json = JSON.toJSON(listSurveys).value
        println("body response: " + json)
        Response(HttpStatusCode.Ok, null, json)
    }
}

case class CookieJSON(id: String, username: String, expires: String) {

}

object SurveysApp extends App {
    val db = new DB(dbHostName, dbPort, dbName, username, pwd)
    val surveysRepository = new SurveysRepository(new SurveysDao(db))
    val usersRepository = new UsersRepository(new UsersDao(db))
    val surveysService = new SurveysService(surveysRepository)
    val usersService = new UsersService(usersRepository)

    val server = new Server(new SurveysApi(surveysService, usersService), webServerPort)
    server.start()
}
