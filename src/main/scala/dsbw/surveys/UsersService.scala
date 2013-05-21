package dsbw.surveys

import dsbw.domain.user.User

class UsersService(usersRepository: UsersRepository) {

    def existsUserName(user: User): Boolean = {
          println("existsUserName " + user.userName)
          usersRepository.existsUserName(user.userName)
    }

    def createUser(user: User) : String = {
        println("User to create: " + user)
        val userRecord = user.toRecord()
        usersRepository.createUser(userRecord)
        userRecord._id.toString
    }

    def getUser(id: String): User = {
        val user = usersRepository.getUser(id)
        User.fromRecord(user)
    }

    def login(user: User): String = {
        val u = usersRepository.loginUser(user.userName, user.password)
        println("User logged: " + u)
        if (u.isDefined) {
            u.get._id.toString
        }
        else {
            return null
        }
    }

    def putSurvey(idUser: String, idSurvey: String) {
        if (!idUser.equals("-1")) {
            usersRepository.pushSurvey(idUser, idSurvey)
        }
    }

    def getSurveys(idUser: String): List[String] = {
       usersRepository.getUser(idUser).surveys
    }
}
