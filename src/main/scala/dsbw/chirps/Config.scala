package dsbw.chirps

import util.Properties

class MissingPropertyException(propertyName:String) extends RuntimeException{
  override def getMessage = "Missing environment property %s".format(propertyName)
}

object Config {

  private def envOrFail(propertyName:String) = Properties.envOrNone(propertyName).getOrElse(throw new MissingPropertyException(propertyName))
  private def intEnvOrElse(propertyName:String, defaultValue:Int) = Properties.envOrElse(propertyName, defaultValue.toString).toInt

  val webServerPort = intEnvOrElse("PORT",8080)
  val dbHostName = Properties.envOrElse("MONGO_HOST_NAME", "ds039467.mongolab.com")
  val dbPort = intEnvOrElse("MONGO_PORT",39467)
  val dbName = Properties.envOrElse("MONGO_DB_NAME","dbsw-lab-grup4")
  val username = Properties.envOrElse("MONGO_USERNAME","dsbw-lab-admin")
  val pwd = Properties.envOrElse("MONGO_PASSWORD","R5ty62Xl")

}
