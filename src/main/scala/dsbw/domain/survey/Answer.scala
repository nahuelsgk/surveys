package dsbw.domain.survey


case class Answer(idQuestion: Int) {

}

class AnswerText(idQuestion: Int, answer: String) extends Answer(idQuestion) {

}

class AnswerChoice(idQuestion: Int, answer: String) extends Answer(idQuestion) {

}

class AnswerMultiChoice(idQuestion: Int, answer: List[String]) extends Answer(idQuestion) {

}
