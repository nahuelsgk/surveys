package dsbw.domain.survey
import dsbw.surveys.{QuestionRecord, AnswerRecord}
import org.bson.types.ObjectId


case class Answer(
		     idQuestion : String,
		     typeAnswer : String,
		     options    : List[String] = List()
                 ) {

    /* Recupera el ID o el genera si cal */
    private def getIdQuestion(): ObjectId = {
        if (this.idQuestion.isEmpty)
            return new ObjectId()
        else
            //return new ObjectId(this.idQuestion)
            return new ObjectId()
    }

    def toRecord(): AnswerRecord = {
        new AnswerRecord(
	    idQuestion = this.getIdQuestion(),
	    typeAnswer = this.typeAnswer,
	    options    = this.options
	)
    }

}

object Answer {
    /* Metode static per convertir QuestionRecord a Question */
    def fromRecord(record: AnswerRecord) : Answer = {
        new Answer(
            idQuestion = record.idQuestion.toString,
            typeAnswer = record.typeAnswer,
            options = record.options
        )
    }
}