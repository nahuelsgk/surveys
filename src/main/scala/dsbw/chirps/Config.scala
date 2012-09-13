package dsbw.chirps

import util.Properties

class MissingPropertyException(propertyName:String) extends RuntimeException{
  override def getMessage = "Missing environment property %s".format(propertyName)
}

object Config {

  private def envOrFail(propertyName:String) = Properties.envOrNone(propertyName).getOrElse(throw new MissingPropertyException(propertyName))
  private def intEnvOrElse(propertyName:String, defaultValue:Int) = Properties.envOrElse(propertyName, defaultValue.toString).toInt

  val webServerPort = intEnvOrElse("WEB_SERVER_PORT",8080)
  val dbHostName = Properties.envOrElse("MONGO_HOST_NAME", "ds035997-a.mongolab.com")
  val dbPort = intEnvOrElse("MONGO_PORT",35997)
  val dbName = Properties.envOrElse("MONGO_DB_NAME","dsbw-1213t")
  val username = Properties.envOrElse("MONGO_USERNAME","dsbw2")
  val pwd = envOrFail("MONGO_PASSWORD")

}
