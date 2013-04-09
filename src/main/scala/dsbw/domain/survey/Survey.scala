package dsbw.domain.survey
import util.parsing.json.JSONObject


object StatesSurvey {
  val Pending = "pending"
  val Accepted = "accepted"
  val Rejected = "rejected"

}

/*case class Survey(title: String, since: String, until: String,
                  id: String = "",
                  state: String = StatesSurvey.Pending,
                  questions: Map[Int, Question] = Map(),
                  answers: Map[Int, Answer] = Map()
                   ) {
}*/

case class Survey(title: String, since: String, until: String, id : String = "") {
  def writes(survey: Survey) = JSONObject (Map(
    "id" -> id,
    "title" -> title,
    "since" -> since,
    "until" -> until
  ))
}