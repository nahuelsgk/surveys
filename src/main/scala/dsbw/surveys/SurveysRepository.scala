package dsbw.surveys

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._
import dsbw.mongo.MongoDao
import scala.collection.mutable.ListBuffer
import com.mongodb.casbah.commons.TypeImports.ObjectId
import dsbw.domain.survey.StatesSurvey

/** A record representing the scheme of Surveys stored in the surveys collection */
case class SurveysRecord(
    _id: ObjectId = new org.bson.types.ObjectId(),
    title: String,
    since: String,
    until: String,
    idCreator: String = "-1",
    secret: String,
    state: String = StatesSurvey.Creating,
    questions: List[QuestionRecord] = List(),
    answers: List[SurveyAnswerRecord] = List()
)

case class QuestionRecord(
    _id: ObjectId = new org.bson.types.ObjectId(),
    questionType: String = "",
    order: Int = 1,
    text: String = "",
    options: List[String] = List()
)

case class SurveyAnswerRecord(
    idClient: ObjectId = new ObjectId(),
    dateAnswer: String = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Calendar.getInstance().getTime()),
    stateAnswer: String,
    answered: List[AnswerRecord] = List()
)

case class AnswerRecord(
   idQuestion: ObjectId = new ObjectId(),
   typeAnswer: String = "",
   options: List[String] = List()
)

/** Surveys Data Access Object */
class SurveysDao(db: DB) extends MongoDao[SurveysRecord](db.surveys) {
    def getSecretById(id: String): String = {
        val survey = this.findOneByID(new ObjectId(id)).get;

        survey.secret
    }
}



/**
 * Surveys Repository
 * A repository uses a DAO but doesn't expose all the DB centric API of the DAO
 */
class SurveysRepository(dao: SurveysDao) {

    def listSurveys(): ListBuffer[SurveysRecord] = {
        println("*** SurveysRepository.listSurveys()")
        val surveysCursor = dao.findAll
        var surveysList = new ListBuffer[SurveysRecord]()
        while (surveysCursor.hasNext) {
            surveysList += surveysCursor.next()
        }

        surveysList
    }

    def listSurveys(ids: List[String]): Set[SurveysRecord] = {
        println("*** SurveysRepository.listSurveys() ids: " + ids)
        var setIDS = Set[ObjectId]()
        ids.foreach(a => setIDS += new ObjectId(a))
        dao.findByIds(setIDS)
    }

    def createSurvey(survey: SurveysRecord) {
        dao.save(survey)
    }

    def updateSurvey(survey: SurveysRecord): Boolean = {
        var query = Map[String, ObjectId]()
        query += "_id" -> survey._id
        dao.update(
            query,
            MongoDBObject(
                "$set" -> (MongoDBObject("title" -> survey.title)
                    ++ MongoDBObject("since" -> survey.since)
                    ++ MongoDBObject("until" -> survey.until))
            ),
            false
        )

        if (survey.questions.nonEmpty) {
            println("   - HIHA: " + survey.questions + "| " + survey.questions.size + "| " + survey.questions)
            insertQuestion(survey._id, survey.questions)
        }
        else {
            println("   - NO hiha Questions")
        }

        true
    }

    def getSurveySecret(id: String): String = {
        dao.getSecretById(id)
    }

    def getSurvey(id: String): SurveysRecord = {
        dao.findOneByID(new ObjectId(id)).get;
    }

    def removeAllSurveys() {
        dao.removeAll()
    }

    def removeSurvey(id: String) {
        dao.remove(new ObjectId(id))
    }

    def insertQuestion(id: ObjectId, questionList: List[QuestionRecord]) {
        println("insertQuestion()" + questionList.size)
        var query = Map[String, ObjectId]()
        query += "_id" -> id
        // Delete all questions
        dao.update(query, MongoDBObject("$unset" -> (MongoDBObject("questions" -> ""))), false)
        println("insertQuestion().update")
        // Insert questions
        questionList.foreach(question => pushQuestion(question, query))
    }

    def pushQuestion(q: QuestionRecord, query: Map[String, ObjectId]) {
        println("q: " + q + " | " + q.questionType + "," + q.text)
        dao.update(
            query,
            MongoDBObject(
                "$push" -> (MongoDBObject(
                    "questions" -> (MongoDBObject("questionType" -> q.questionType)
                                    ++ MongoDBObject("text" -> q.text)
                                    ++ MongoDBObject("options" -> q.options))
                    )
                )
            ),
            false
        )
    }

    def updateAnswers(surveyId: ObjectId, answer: SurveyAnswerRecord) {
        println("*** SurveysRepository.putAnswers()")
        val query = Map("_id" -> surveyId, "answers.idClient" -> answer.idClient)
        dao.update(
            query,
            MongoDBObject(
                "$pop" -> (MongoDBObject("answers" -> 1))
            ),
            false
        )

        saveAnswers(surveyId, answer)
    }

    def saveAnswers(surveyId: ObjectId, answer: SurveyAnswerRecord) {
        println("*** SurveysRepository.saveAnswers()")
        var query = Map[String, ObjectId]()
        query += "_id" -> surveyId
        dao.update(
            query,
            MongoDBObject(
                "$push" -> (MongoDBObject(
                    "answers" -> (MongoDBObject("idClient" -> answer.idClient)
                        ++ MongoDBObject("dateAnswer" -> answer.dateAnswer)
                        ++ MongoDBObject("stateAnswer" -> answer.stateAnswer))
                    )
                )
            ),
            false
        )

        // Insert answers
        try {
            val subquery = Map("_id" -> surveyId, "answers.idClient" -> answer.idClient)
            answer.answered.foreach(
                a => dao.update(
                    subquery,
                    MongoDBObject(
                        "$push" -> (MongoDBObject(
                            "answers.$.answered" -> (MongoDBObject("idQuestion" -> a.idQuestion)
                                ++ MongoDBObject("typeAnswer" -> a.typeAnswer)
                                ++ MongoDBObject("options" -> a.options)
                                )
                            )
                        )
                    ),
                    false
                )
            )

            println(subquery)
        } catch {
            case e: Throwable => {
                println(e)
                println(e.getStackTraceString)
            }
        }
    }
}


