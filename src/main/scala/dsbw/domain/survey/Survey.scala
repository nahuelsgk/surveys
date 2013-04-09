package dsbw.domain.survey
import util.parsing.json.JSONObject


object StatesSurvey {
    val Creating = "creating"
    val Pending = "pending"
    val Accepted = "accepted"
    val Rejected = "rejected"
}

case class Survey(title: String,
                  since: String,
                  until: String,
                  id: String = "",
                  state: String = StatesSurvey.Creating,
                  questions: Map[Int, Question] = Map(),
                  answers: Map[Int, Answer] = Map()
                   ) {

}