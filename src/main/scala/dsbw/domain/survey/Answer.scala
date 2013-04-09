package dsbw.domain.survey


case class Answer(id: Int,
                  idQuestion: Int,
                  idClient: String,
                  typeQ: String) {

}

class AnswerText (id: Int,
                  idQuestion: Int,
                  idClient: String,
                  typeQ: String,
                  answer: String)
    extends Answer(id, idQuestion, idClient, typeQ){

}

class AnswerChoice (id: Int,
                    idQuestion: Int,
                    idClient: String,
                    typeQ: String,
                    answer: String)
    extends Answer(id, idQuestion, idClient, typeQ){

}

class AnswerMultiChoice (id: Int,
                         idQuestion: Int,
                         idClient: String,
                         typeQ: String,
                         answer: List[String])
    extends Answer(id, idQuestion, idClient, typeQ){

}
