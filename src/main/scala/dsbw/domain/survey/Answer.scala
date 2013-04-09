package dsbw.domain.survey


class Answer(idQuestion: Int, idClient: String) {

}

case class AnswerText(idQuestion: Int, idClient: String, answer: String) extends Answer(idQuestion, idClient) {

}

case class AnswerChoice(idQuestion: Int, idClient: String, answer: String) extends Answer(idQuestion, idClient) {

}

case class AnswerMultiChoice(idQuestion: Int, idClient: String, answer: List[String]) extends Answer(idQuestion, idClient) {

}
