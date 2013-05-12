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
            val aux = new Survey(sur._id.toString, sur.title, sur.since, sur.until);
            listSurvey += aux
        })

        listSurvey
    }

    def createSurvey(survey: Survey): Map[String, String] = {
        println("*** SurveysService.createSurvey()")
        //val surveyRecord = new SurveysRecord(title = survey.title, since = survey.since, until = survey.until, state = survey.state)
        val surveyRecord= survey.toRecord()
        surveysRepository.createSurvey(surveyRecord)
        println("Survey created: " + surveyRecord)

        Map("id" -> surveyRecord._id.toString, "secret" -> "bigotiSecret");
    }

    def putAnswers(id: String, answers: SurveyAnswer) {
        println("*** SurveyService.putAnswers()")
        println("Try to put survey answers: " + answers.toRecord())
        surveysRepository.putAnswers(new ObjectId(id), answers.toRecord());
    }

    def saveAnswers(id: String, answers: SurveyAnswer) {
        println("*** SurveyService.saveAnswers()")
        println("Try to save survey answers: " + answers.toRecord())
        surveysRepository.saveAnswers(new ObjectId(id), answers.toRecord());
    }

    def updateSurvey(id: String, survey: Survey) {
        println("*** SurveysService.updateSurvey()")
        val objectId = new org.bson.types.ObjectId(id)
        //val surveyRecord = new SurveysRecord(_id = objectId, title = survey.title, since = survey.since, until = survey.until)
        println("Survey updated: " + survey.toRecord())
        surveysRepository.updateSurvey(survey.toRecord())
    }

    def getSurvey(id: String) : Survey = {
        println("Survey getted: "+ id);
        val sur= surveysRepository.getSurvey(id);
        Survey.fromRecord(sur)
    }
}
