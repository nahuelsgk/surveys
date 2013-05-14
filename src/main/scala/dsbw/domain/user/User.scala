package dsbw.domain.user

import dsbw.domain.survey.Survey
import dsbw.surveys.UserRecord

case class User(id: String = "",
                userName: String,
                password: String,
                email: String = "",
                surveys: List[String] = List()) {

    def toRecord(): UserRecord = {
        val userRecord = new UserRecord(
            userName = this.userName,
            password = this.password,
            email = this.email
        )
        userRecord
    }
}

object User {
    def fromRecord(record: UserRecord): User = {
        new User(
            id = record._id.toString,
            userName = record.userName,
            password = record.password,
            email = record.email,
            surveys = record.surveys
        )
    }
}
