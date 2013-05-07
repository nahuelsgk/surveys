package dsbw.domain.survey

import util.parsing.json.JSONObject
import dsbw.surveys.{QuestionRecord, SurveysRecord, SurveyAnswerRecord, AnswerRecord}
import org.bson.types.ObjectId
import collection.mutable.ListBuffer


object TypeStateAnswer {
    val Responding = "responding"
    val Done = "done"
}

case class SurveyAnswer(
                     var idClient   : String               = ""
                     , stateAnswer : String= TypeStateAnswer.Responding
		             , answered : Option[List[Answer]] = None
                     ) {

    /* Recupera el ID o el genera si cal */
    private def getIdClient(): ObjectId = {
       if (this.idClient.isEmpty)
           return new ObjectId()
       else
           return new ObjectId(this.idClient)
    }

    private def getAnswersRecordList(): List[AnswerRecord] = {
        if (this.answered.nonEmpty){
            val l= this.answered.get
	        val ll= ListBuffer[AnswerRecord]()
	        l.foreach(a => {
	           ll+= a.toRecord()
	        })
	        println("LL: ("+ ll.toList.size+ ") "+ ll.toList)
	         return ll.toList
	    }
	    else{
	        return List()
	    }
    }

    def toRecord(): SurveyAnswerRecord = {
	  new SurveyAnswerRecord(
	    idClient = this.getIdClient(),
        stateAnswer = this.stateAnswer,
	    answered = getAnswersRecordList()
	  )
    }

    def setId(id: ObjectId){
        this.idClient= id.toString()
    }
}
