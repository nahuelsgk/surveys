package dsbw.surveys

import collection.mutable.ListBuffer
import org.bson.types.ObjectId

case class Question(question: String)

class SurveysService(surveysRepository: SurveysRepository) {

    def listSurveys(): ListBuffer[Survey] = {
        val list = surveysRepository.listSurveys()
        val listSurvey = new ListBuffer[Survey]

        list.foreach((sur: SurveysRecord) => {
            val aux = new Survey(sur.title, sur.since, sur.until, sur._id.toString);
            listSurvey += aux
        })

        listSurvey
    }

    def createSurvey(survey: Survey): String = {
        val surveyRecord = new SurveysRecord(title = survey.title, since = survey.since, until = survey.until)
        surveysRepository.createSurvey(surveyRecord)
        println("Survey created: " + surveyRecord)
        surveyRecord._id.toString
    }

    def updateSurvey(id: String, survey: Survey) {
        val objectId = new org.bson.types.ObjectId(id)
        val surveyRecord = new SurveysRecord(_id = objectId, title = survey.title, since = survey.since, until = survey.until)
        println("Survey updated: " + surveyRecord)
        surveysRepository.updateSurvey(surveyRecord)

    }

    def getSurvey(id: String) : Survey = {
        println("Survey getted: "+ id);
        val sur= surveysRepository.getSurvey(id);
        new Survey(sur.title, sur.until, sur.since, id);
    }
}
