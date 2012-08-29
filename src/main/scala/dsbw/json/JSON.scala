package dsbw.json

import net.liftweb.json.JsonAST.render
import net.liftweb.json.Extraction.decompose
import net.liftweb.json._
import net.liftweb.json


case class JSON(value: String) {}

object JSON {

  class EnumerationsSerializer(enums: Enumeration*) extends Serializer[Enumeration#Value] {

    val EnumerationClass = classOf[Enumeration#Value]

    val formats = Serialization.formats(NoTypeHints)

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Enumeration#Value] = {
      case (TypeInfo(EnumerationClass, _), json) => json match {
        case JString(value) => enums.find(_.values.exists(_.toString == value)).get.withName(value)
        case value => throw new MappingException("Can't convert " + value + " to " + EnumerationClass)
      }
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case i: Enumeration#Value => JString(i.toString)
    }
  }


  implicit val formats = Serialization.formats(NoTypeHints) //+ new EnumerationsSerializer(ErrorCode) + new EnumerationsSerializer(ParameterOrFieldErrorCode)

  //  def toJSON(a: Any) = MongoDBObject("obj"->a).get("obj").
  implicit def toJSON(a: Any, prettyPrint: Boolean = true): JSON = {
    if (prettyPrint) {
      JSON(pretty(render(decompose(a))))
    } else {
      JSON(compact(render(decompose(a))))
    }
  }

  private def cleanStrings(jvalue: JValue) = {
    jvalue.map(jv => jv match {
      case o: JObject => {
        val cleanFields = o.obj.map(field => field match {
          case JField(name, JString(s)) => JField(name, JString(s.trim))
          case f => f
        })
        JObject(cleanFields)
      }
      case x => x
    })
  }


  implicit def fromJSON[T](json: JSON)(implicit mf: scala.reflect.Manifest[T]): T = {
    val jvalue = parse(json.value)
    try {
      cleanStrings(jvalue).extract[T]
    } catch {
      case me: MappingException => {
//        val missingValue = """(?s)No usable value for (\S+)\nDid not find value.*""".r
//        val illegalValue = """(?s)No usable value for (\S+)\n.*""".r
        me.msg match {
//          case missingValue(path) => throw ApiException.illegalField(path, ParameterOrFieldErrorCode.Missing)
//          case illegalValue(path) => throw ApiException.illegalField(path, ParameterOrFieldErrorCode.IllegalFormat)
          case _ => throw me
        }
      }
      case e => throw e
    }
  }
}
