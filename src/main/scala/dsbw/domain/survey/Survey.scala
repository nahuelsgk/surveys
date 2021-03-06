package dsbw.domain.survey

import util.parsing.json.JSONObject
import dsbw.surveys.{QuestionRecord, SurveysRecord}
import org.bson.types.ObjectId
import collection.mutable.ListBuffer


object StatesSurvey {
    val Creating = "creating"
    val Pending = "pending"
    val Accepted = "accepted"
    val Rejected = "rejected"
}

case class Survey(
    id: String = "",
    title: String,
    since: String,
    until: String,
    idCreator: String = "-1",
    secret: String = "",
    state: String = StatesSurvey.Creating,
    questions: Option[List[Question]]= None,
    answers: Option[List[SurveyAnswer]]= None
 )  {


    /* Recupera el ID o el genera si cal */
    private def getId: ObjectId = {
        if (this.id.isEmpty)
            new ObjectId()
        else
            new ObjectId(this.id)
    }

    /* Recupera el List de QuestionRecords */
    private def getQuestionRecordList(): List[QuestionRecord]= {
        if (this.questions.nonEmpty){
            val l  = this.questions.get
            val ll = ListBuffer[QuestionRecord]()
            l.foreach(q => {
                ll+= q.toRecord()
            })
            println("LL: ("+ ll.toList.size+ ") "+ ll.toList)
            ll.toList
        }
        else{
            List()
        }
    }

    def setAnswers(ans: Option[List[SurveyAnswer]]): Survey = {
        new Survey(
            id= this.id,
            title = this.title,
            since = this.since,
            until = this.until,
            idCreator = this.idCreator,
            secret = this.secret,
            state = this.state,
            questions = this.questions,
            answers = ans
        )
    }

    /* Genera el record */
    def toRecord(): SurveysRecord= {
        new SurveysRecord(
            _id = this.getId
            , title = this.title
            , since = this.since
            , until = this.until
            , idCreator = this.idCreator
            , secret = this.secret
            , state= this.state
            , questions= getQuestionRecordList())
    }

    def toRecord(idC: String): SurveysRecord= {
        new SurveysRecord(
            _id = this.getId
            , title = this.title
            , since = this.since
            , until = this.until
            , idCreator = idC
            , secret = this.secret
            , state= this.state
            , questions= getQuestionRecordList())
    }

    /* Print addicional */
    def toString2(){
        println("Survey(" + this.getId + ", "+ this.title+ ", "+ this.since+ ", "+ this.until+ ", "+ this.state)
        print("      , questions[")
        if (this.questions.nonEmpty){
            this.questions.get.foreach(q=> print(q.toString2+ ", "))
        }
        println("]")
        println("}")
    }

}

object Survey {
    /* Metode static per convertir SurveyRecord a Survey */
    def fromRecord(record: SurveysRecord) : Survey = {
        val questions = new ListBuffer[Question]()
        if(record.questions.nonEmpty) {
            val listQ = record.questions
            println("List of QUESTIONS: " + listQ)
            var i= 0;
            listQ.foreach(q => {
                questions += Question.fromRecord(q)
                println("   - ["+ i +"] "+ q)
                i= i+ 1
            })
        }
        val answers = new ListBuffer[SurveyAnswer]()
        if(record.answers.nonEmpty) {
            val listA = record.answers
            println("List of SURVEYANSWERS: ")
            var i= 0;
            listA.foreach(a => {
                answers += SurveyAnswer.fromRecord(a)
                println("   - ["+ i +"] "+ a)
                i= i+ 1
            })
        }
        new Survey(
            id = record._id.toString,
            title = record.title,
            since = record.since,
            until = record.until,
            idCreator = record.idCreator,
            secret = record.secret,
            state = record.state,
            questions = Some(questions.toList),
            answers= Some(answers.toList)
        )
    }
}
