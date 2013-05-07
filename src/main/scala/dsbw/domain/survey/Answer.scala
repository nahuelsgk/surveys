package dsbw.domain.survey
import dsbw.surveys.AnswerRecord


case class Answer(
		     idQuestion     : String,
		     typeAnswer     : String,
		     text           : String
                 ) {

    def toRecord(): AnswerRecord = {
        new AnswerRecord(
	    idQuestion = this.idQuestion,
	    typeAnswer = this.typeAnswer,
	    text       = this.text
	)
    }

}
