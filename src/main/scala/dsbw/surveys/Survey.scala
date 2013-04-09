package dsbw.surveys

import util.parsing.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: manye
 * Date: 26/03/13
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
case class Survey(title: String, since: String, until: String, id : String = "") {
  def writes(survey: Survey) = JSONObject (Map(
    "id" -> id,
    "title" -> title,
    "since" -> since,
    "until" -> until
  ))
}
