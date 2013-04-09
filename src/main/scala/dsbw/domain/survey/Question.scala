package dsbw.domain.survey

class Question(id: Int) {

}

case class QuestionText(id: Int, question: String) extends Question (id) {

}

case class QuestionChoice (id: Int, options: List[String]) extends Question (id) {

}

case class QuestionMultiChoice (id: Int, options: List[String]) extends Question (id) {

}


