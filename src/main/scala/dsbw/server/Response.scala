package dsbw.server


object HttpStatusCode extends Enumeration{

  val Ok = Value(200)
  val Created = Value(201)
  val Accepted = Value(202)
  val NoContent = Value(204)
  val PartialContent = Value(206)

  val BadRequest = Value(400)
  val Unauthorized = Value(401)
  val Forbidden = Value(403)
  val NotFound = Value(404)
  val NotAcceptable = Value(406)

}

case class Response private (status: HttpStatusCode.Value, headers:Map[String, String], body:Option[Any]=None)

object Response{
  def apply(status:HttpStatusCode.Value, headers:Map[String, String]) = new Response(status, headers)
  def apply(status:HttpStatusCode.Value, headers:Map[String, String], body:Any) = new Response(status,headers,Some(body))
}


