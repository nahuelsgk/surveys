package dsbw.domain.survey

object TypeQuestion {
    val Text = "text"
    val Choice = "choice"
    val MultiChoice = "multiChoice"
}

class Question(id: Int,
                    typeQ: String,
                    order: Int) {

}

case class QuestionText(id: Int,
                        typeQ: String,
                        order: Int,
                        question: String)
    extends Question (id, typeQ, order) {

}

class QuestionChoice (id: Int,
                      typeQ: String,
                      order: Int,
                      options: List[String])
    extends Question (id, typeQ, order) {

}

class QuestionMultiChoice (id: Int,
                           typeQ: String,
                           order: Int,
                           options: List[String])
    extends Question (id, typeQ, order) {

}


