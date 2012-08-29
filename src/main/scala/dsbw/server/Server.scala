package dsbw.server

import util.Properties
import org.eclipse.jetty.server.{Server => JettyServer}
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import org.eclipse.jetty.server.handler.{HandlerList, ResourceHandler}
import scala.Array
import dsbw.json.JSON
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source
import collection.JavaConversions.{enumerationAsScalaIterator, mapAsScalaMap}

trait Api{
  def service(method:String, uri:String, parameters:Map[String, List[String]] = Map(), headers:Map[String, String]=Map(), body:Option[JSON]=None):Response
}


class Servlet(api:Api) extends HttpServlet {

  override def service(request: HttpServletRequest, response: HttpServletResponse) {
    val initialTimeMillis = System.currentTimeMillis()
    response.setContentType("application/json")
    response.setCharacterEncoding("utf-8")
    response.setHeader("Access-Control-Allow-Origin","*")
    val out: java.io.PrintWriter = response.getWriter
    try {
      if (request.getMethod == "OPTIONS") {
        response.setHeader("Access-Control-Allow-Headers","Content-Type, Accept, X-Auth-Token, X-Requested-With")
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Max-Age","1728000");
        //response.setHeader("Access-Control-Allow-Origin", "*")
        response.setStatus(200)
        return
      }
      val sBody = Source.fromInputStream(request.getInputStream,"UTF-8").getLines().mkString("\n")
      val body = if (sBody == "") { None } else {Some(JSON(sBody))}
      val parameters:Map[String, List[String]] = request.getParameterMap.toMap.mapValues(_.toList)
      val headers = request.getHeaderNames.map((h: String) => (h, request.getHeader(h))).toMap
      // Perform request
      val r = api.service(request.getMethod,request.getRequestURI, parameters, headers, body)
      response.setStatus(r.status.id)
      if (r.body.nonEmpty) {
        val json = JSON.toJSON(r.body.get)
        out println json.value
      }
      logRequest(request,response,initialTimeMillis)
    } catch {
      //      case e: ApiException => {
      //        response.setStatus(e.code.id)
      //        logRequest(request,response,initialTimeMillis)
      //        e.printStackTrace()
      //        out.println(e.getMessage)
      //      }
      case e: Throwable => {
        response.setStatus(500)
        logRequest(request,response,initialTimeMillis)
        e.printStackTrace()
        out.println("""{"error":"Internal error"}""")
      }
    }
  }

  private def logRequest(request:HttpServletRequest, response:HttpServletResponse, initialTimeMillis:Long){
    println("%s %s => %s (%s ms)".format(request.getMethod,request.getRequestURI,response.getStatus,System.currentTimeMillis-initialTimeMillis))
  }

}

class Server(api:Api, port:Int) {
  val server: JettyServer = new JettyServer(port)

  val context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS)
  context.setContextPath("/")
  context.addServlet(new ServletHolder(new Servlet(api)), "/")

  val resourceHandler = new ResourceHandler
  resourceHandler.setDirectoriesListed(true)
  resourceHandler.setWelcomeFiles(Array("index.html"))
  resourceHandler.setResourceBase("src/main/docroot")

  val handlers = new HandlerList()
  handlers.setHandlers(Array(resourceHandler,context))

  server.setHandler(handlers)

  def start() {
    server.start()
  }
}
