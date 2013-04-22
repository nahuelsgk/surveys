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
                     id: String = ""
                     , title: String
                     , since: String
                     , until: String
                     , state: String = StatesSurvey.Creating
                     , questions: Option[List[Question]]= None
                     , answers: Option[Map[Int, List[Answer]]]= None
                     ) {

    /* Recupera el ID o el genera si cal */
    private def getId(): ObjectId = {
        if (this.id.isEmpty)
            return new ObjectId()
        else
            return new ObjectId(this.id)
    }

    /* Recupera el List de QuestionRecords */
    private def getQuestionRecordList(): Option[List[QuestionRecord]]= {
        if (this.questions.nonEmpty){
            val l= this.questions.get
            val ll= ListBuffer[QuestionRecord]()
            l.foreach(q => {
                ll+= q.toRecord()
            })
            println("LL: ("+ ll.toList.size+ ") "+ ll.toList)
            return Option.apply(ll.toList)
        }
        else{
            return None
        }
    }

    /* Genera el record */
    def toRecord(): SurveysRecord= {
        new SurveysRecord(
            _id = this.getId()
            , title = this.title
            , since = this.since
            , until = this.until
            , state= this.state
            , questions= getQuestionRecordList())
    }

    /* Print addicional */
    def toString2(){
        println("Survey(" + this.getId()+ ", "+ this.title+ ", "+ this.since+ ", "+ this.until+ ", "+ this.state)
        print("      , questions[")
        if (this.questions.nonEmpty){
            this.questions.get.foreach(q=> print(q.toString2+ ", "))
        }
        println("]")
        println("}")
    }

}