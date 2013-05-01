package dsbw.domain.survey

import util.parsing.json.JSONObject
import dsbw.surveys.{QuestionRecord, SurveysRecord}
import org.bson.types.ObjectId
import collection.mutable.ListBuffer

case class SurveyAnswer(
                     id: String = ""
                     , idClient: String
		     , answersQuestions: Option[List[Answer]]= None
                     ) {
}
