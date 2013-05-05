package dsbw.domain.survey

import util.parsing.json.JSONObject
import dsbw.surveys.{QuestionRecord, SurveysRecord, SurveyAnswerRecord}
import org.bson.types.ObjectId
import collection.mutable.ListBuffer

case class SurveyAnswer(
                     id: String = ""
                     , idClient: String
		     , answered: Option[List[Answer]]= None
                     ) {
    /* Recupera el ID o el genera si cal */
    private def getId(): ObjectId = {
       if (this.id.isEmpty)
           return new ObjectId()
       else
           return new ObjectId(this.id)
    }

    def toRecord(): SurveyAnswerRecord = {
	new SurveyAnswerRecord(
	  _id = this.getId()
	  , idClient = this.idClient
	)
    }
}
