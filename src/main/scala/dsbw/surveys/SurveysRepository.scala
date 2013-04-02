package dsbw.surveys

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.Imports._
import dsbw.mongo.MongoDao
import scala.collection.mutable.ListBuffer

/** A record representing the scheme of Surveys stored in the surveys collection */
case class SurveysRecord(_id: ObjectId = new org.bson.types.ObjectId(), title: String, since: String, until: String)

/** Surveys Data Access Object */
class SurveysDao(db: DB) extends MongoDao[SurveysRecord](db.surveys) {


}

/**
 * Surveys Repository
 * A repository uses a DAO but doesn't expose all the DB centric API of the DAO
 */
class SurveysRepository(dao: SurveysDao) {
    def listSurveys(): ListBuffer[SurveysRecord] = {
        val surveysCursor = dao.findAll
        var surveysList = new ListBuffer[SurveysRecord]()
        while (surveysCursor.hasNext) {
            surveysList += surveysCursor.next()
        }
        return surveysList
    }

    def createSurvey(survey: SurveysRecord) {
        dao.save(survey)
    }

    def updateSurvey(survey: SurveysRecord) {
        var query = Map[String, ObjectId]()
        query += "_id" -> survey._id
        dao.update(query, MongoDBObject("$set" -> (MongoDBObject("title" -> survey.title) ++ MongoDBObject("since" -> survey.since) ++ MongoDBObject("until" -> survey.until))), false)
    }
}
