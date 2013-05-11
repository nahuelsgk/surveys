package dsbw.domain.survey
import dsbw.surveys.AnswerRecord
import org.bson.types.ObjectId


case class Answer(
		     idQuestion     : String,
		     typeAnswer     : String,
		     text           : String
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
	    text       = this.text
	)
    }

}
