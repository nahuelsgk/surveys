package dsbw.surveys

import collection.mutable.ListBuffer
import org.bson.types.ObjectId
import dsbw.domain.survey.Survey
import dsbw.domain.survey.SurveyAnswer
import scala.util.Random

class SurveysService(surveysRepository: SurveysRepository) {

    def listSurveys(): ListBuffer[Survey] = {
        println("*** SurveysService.listSurveys()")
        val list = surveysRepository.listSurveys()
        val listSurvey = new ListBuffer[Survey]

        list.foreach((sur: SurveysRecord) => {
            val aux = new Survey(id = sur._id.toString, title = sur.title, since = sur.since, until = sur.until, secret = sur.secret);
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

    def putAnswers(id: String, answers: SurveyAnswer) {
        println("*** SurveyService.putAnswers()")
        println("Try to put survey answers: " + answers.toRecord())
        surveysRepository.putAnswers(new ObjectId(id), answers.toRecord())
    }

    def saveAnswers(id: String, answers: SurveyAnswer) {
        println("*** SurveyService.saveAnswers()")
        println("Try to save survey answers: " + answers.toRecord())
        surveysRepository.saveAnswers(new ObjectId(id), answers.toRecord())
    }

    def updateSurvey(survey: Survey): Boolean =  {
        println("*** SurveysService.updateSurvey()")
        println("Survey updated: " + survey.toRecord())

        if (surveysRepository.getSurveySecret(survey.id) == survey.id) {
            surveysRepository.updateSurvey(survey.toRecord())
            true
        } else {
            false
        }
    }

    def getSurvey(id: String) : Survey = {
        println("Survey gotten: "+ id);
        val sur= surveysRepository.getSurvey(id);
        Survey.fromRecord(sur)
    }
}
