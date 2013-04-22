package dsbw.domain.survey

import org.bson.types.ObjectId
import dsbw.surveys.QuestionRecord

object TypeQuestion {
    val Text = "text"
    val Choice = "choice"
    val MultiChoice = "multiChoice"
}

object Question {
    /* Metode static per convertir QuestionRecord a Question */
    def fromRecord(record: QuestionRecord) : Question = {
         new Question(
            id = record._id.toString,
            questionType = record.questionType,
            order = record.order,
            text = record.text
         )
    }
}

class Question(
                  id: String= ""
                  , questionType: String= TypeQuestion.Text
                  , order: Int= 1
                  , text: String
                  /*, options: Option[List[String]]= None*/
                  ) {

    /* Recupera el ID o el genera si cal */
    private def getId(): ObjectId = {
        if (this.id.isEmpty || this.id== "")
            return new ObjectId()
        else
            return new ObjectId(this.id)
    }

    /* Genera el record */
    def toRecord(): QuestionRecord= {
        new QuestionRecord(
            _id = this.getId()
            , questionType = this.questionType
            , order = this.order
            , text = this.text)
    }

    /* Print addicional */
    def toString2(){
        print("Q{"+ this.questionType+ ", "+ this.text+"}")
    }

}


