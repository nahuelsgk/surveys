package dsbw.surveys

import collection.mutable.ListBuffer
import org.bson.types.ObjectId
import dsbw.domain.survey.Survey
import dsbw.domain.survey.SurveyAnswer
import dsbw.domain.user.User

class SurveysService(surveysRepository: SurveysRepository, usersRepository: UsersRepository) {

    def listSurveys(): ListBuffer[Survey] = {
        println("*** SurveysService.listSurveys()")
        val list = surveysRepository.listSurveys()
        val listSurvey = new ListBuffer[Survey]

        list.foreach((sur: SurveysRecord) => {
            val aux = new Survey(id = sur._id.toString, title = sur.title, since = sur.since, until = sur.until, secret = sur.secret)
            listSurvey += aux
        })

        listSurvey
    }

    def getSurveysAnswered(userId: String) : ListBuffer[Survey] = {
        val list = surveysRepository.listSurveys()
        val listSurvey = new ListBuffer[Survey]

        list.foreach((sur: SurveysRecord) => {
            sur.answers.foreach(answer => {
                println("inside foreach, answerIdClient = " + answer.idClient + " userId = " + userId)
                if (answer.idClient.toString == userId) {
                    val auxSurvey = Survey.fromRecord(sur)
                    listSurvey += auxSurvey.setAnswers(Option(List(SurveyAnswer.fromRecord(answer))))
                }
            })

        })

        listSurvey
    }

    def listSurveys(idSurveys: List[String]): Set[Survey] =  {
        val surveysRecord = surveysRepository.listSurveys(idSurveys)
        var surveys = Set[Survey]()
        surveysRecord.foreach((sur: SurveysRecord) => surveys += Survey.fromRecord(sur))
        surveys
    }

    def getUsersName(survey: Survey) : Map[String, String] = {
        var reto = Map[String,String]()
        survey.answers.get.foreach(answer => {
                                    //println("PRINTANDO" + answer.idClient)
                                    //println(usersRepository.getUser(answer.idClient))
                                    val userRecord = usersRepository.getUserOption(answer.idClient)
                                    if(userRecord.isEmpty == false){
                                      val user = User.fromRecord(userRecord.get)
                                      reto +=  user.id -> user.userName
                                    }
                                })
        reto

    }

    def createSurvey(survey: Survey, idCreator: String): Map[String, String] = {
        println("*** SurveysService.createSurvey() idCreator: " + idCreator )

        val surveyRecord = survey.toRecord(idCreator)
        surveysRepository.createSurvey(surveyRecord)
        println("Survey created: " + surveyRecord)

        Map("id" -> surveyRecord._id.toString, "secret" -> surveyRecord.secret)
    }

    def updateAnswers(id: String, answers: SurveyAnswer) : Boolean = {
        println("*** SurveyService.putAnswers()")

        println("Try to put survey answers: " + answers.toRecord())
        val s= getSurvey(id)
        s.answers.get.foreach(a=> {
            if(a.idClient== answers.idClient){
                println("Client trobat: "+ answers.idClient+ "= "+ a.idClient)
                println("****** El seu estat es ***"+ a.stateAnswer)
                if (a.stateAnswer == "done" ) {
                    println("Ja estava salvat. No es pot salvar si ja li has havies finalitzat");
                    return false
                }
                surveysRepository.updateAnswers(new ObjectId(id), answers.toRecord())
                return true
            }
            else{
                println("Client NO: "+ answers.idClient+ "= "+ a.idClient)
            }
        })
        println("Client no trobat -> NO es fa el PUT")
        return false
    }

    def saveAnswers(id: String, answers: SurveyAnswer) {
        println("*** SurveyService.saveAnswers()")
        println("Try to save survey answers: " + answers.toRecord())
        surveysRepository.saveAnswers(new ObjectId(id), answers.toRecord())
    }

    def updateSurvey(survey: Survey): Boolean =  {
        println("*** SurveysService.updateSurvey()")
        println("Survey updated: " + survey.toRecord())

        if (surveysRepository.getSurveySecret(survey.id) == survey.secret) {
            val dbSurveyRecord = surveysRepository.getSurvey(survey.id)
            val dbSurvey = Survey.fromRecord(dbSurveyRecord)
            println("**** Size del list" + dbSurvey.answers.get.size)
            if (dbSurvey.answers.get.size > 0) return false
            surveysRepository.updateSurvey(survey.toRecord())
            true
        } else {
            false
        }
    }

    def getAnswersUser(idSurvey: String, idClient: String) : Survey = {
        println("Survey answers from survey "+idSurvey+" of the user "+idClient)

        val sur    = surveysRepository.getSurvey(idSurvey)
        val survey = Survey.fromRecord(sur)

        val listFiltered = survey.answers.get.filter( e => e.idClient == idClient)
        println("*****l.estat es " + listFiltered(0).stateAnswer)
        if (listFiltered(0).stateAnswer == "done") throw new IllegalStateException("Exception state")

        new Survey(
                    id = survey.id,
                    title = survey.title,
                    since = survey.since,
                    until = survey.until,
                    secret = survey.secret,
                    state = survey.state,
                    questions = survey.questions,
                    answers= Some(listFiltered)
                )
    }

    def getSurvey(id: String) : Survey = {
        println("Survey gotten: "+ id);
        val sur= surveysRepository.getSurvey(id);
        Survey.fromRecord(sur)
    }

    def existsUserName(user: User): Boolean = {
        println("existsUserName " + user.userName)
        usersRepository.existsUserName(user.userName)
    }

    def createUser(user: User) : String = {
        println("User to create: " + user)
        val userRecord = user.toRecord()
        usersRepository.createUser(userRecord)
        userRecord._id.toString
    }

    def getUser(id: String): User = {
        val user = usersRepository.getUser(id)
        User.fromRecord(user)
    }

    def login(user: User): String = {
        val u = usersRepository.loginUser(user.userName, user.password)
        println("User logged: " + u)
        if (u.isDefined) {
            u.get._id.toString
        }
        else {
            return null
        }
    }

    def putSurvey(idUser: String, idSurvey: String) {
        if (!idUser.equals("-1")) {
            usersRepository.pushSurvey(idUser, idSurvey)
        }
    }

    def getSurveys(idUser: String): List[String] = {
        val surveys = usersRepository.getUser(idUser).surveys
        surveys
    }
}

