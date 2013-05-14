package dsbw.surveys

import collection.mutable.ListBuffer
import org.bson.types.ObjectId
import dsbw.domain.survey.Survey
import dsbw.domain.survey.SurveyAnswer

class SurveysService(surveysRepository: SurveysRepository) {

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

    def createSurvey(survey: Survey): Map[String, String] = {
        println("*** SurveysService.createSurvey()")
        //val surveyRecord = new SurveysRecord(title = survey.title, since = survey.since, until = survey.until, state = survey.state)
        val surveyRecord = survey.toRecord()
        surveysRepository.createSurvey(surveyRecord)
        println("Survey created: " + surveyRecord)

        Map("id" -> surveyRecord._id.toString, "secret" -> surveyRecord.secret)
    }

    def putAnswers(id: String, answers: SurveyAnswer) : Boolean = {
        println("*** SurveyService.putAnswers()")
        println("Try to put survey answers: " + answers.toRecord())
        val s= getSurvey(id)
        s.answers.get.foreach(a=> {
            if(a.idClient== answers.idClient){
                println("Client trobat: "+ answers.idClient+ "= "+ a.idClient)
                surveysRepository.putAnswers(new ObjectId(id), answers.toRecord())
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
            surveysRepository.updateSurvey(survey.toRecord())
            true
        } else {
            false
        }
    }

    def getAnswersUser(idSurvey: String, idClient: String) : SurveyAnswer = {
        println("Survey answers from survey "+idSurvey+" of the user "+idClient)

        var sur    = surveysRepository.getSurvey(idSurvey)
        var survey = Survey.fromRecord(sur)

        var toReturn = new SurveyAnswer()
        val l = survey.answers.get
        l.foreach( e => {
          if(e.idClient == idClient) toReturn = e
        })
        toReturn
    }

    def getSurvey(id: String) : Survey = {
        println("Survey gotten: "+ id);
        val sur= surveysRepository.getSurvey(id);
        Survey.fromRecord(sur)
    }
}
