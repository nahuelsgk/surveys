package dsbw.domain.user

import dsbw.domain.survey.Survey

case class User(id: String,
                userName: String,
                password: String,
                email: String,
                surveys: List[Survey] = List()) {


}
