package dsbw.server

import org.eclipse.jetty.server.{Server => JettyServer}
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import org.eclipse.jetty.server.handler.{HandlerList, ResourceHandler}
import scala.Array
import dsbw.json.JSON
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source
import collection.JavaConversions.{enumerationAsScalaIterator, mapAsScalaMap}
import java.io.PrintWriter

/** Trait to be implemented by HTTP apis */
trait Api{
  def service(method:String, uri:String, parameters:Map[String, List[String]] = Map(), headers:Map[String, String]=Map(), body:Option[JSON]=None):Response
}

/** Main servlet interfacing between Java Servlet APIs and the Api trait */
class Servlet(api:Api) extends HttpServlet {

  override def service(request: HttpServletRequest, response: HttpServletResponse) {

    def parseRequest(request: HttpServletRequest): Tuple3[Option[JSON],Map[String, List[String]],Map[String, String]] = {

      def parseBody(request: HttpServletRequest): Option[JSON] = {
        val sBody = Source.fromInputStream(request.getInputStream, "UTF-8").getLines().mkString("\n")
        val body = if (sBody == "") {
          None
        } else {
          Some(JSON(sBody))
        }
        body
      }

      (parseBody(request),
        request.getParameterMap.toMap.mapValues(_.toList),
        request.getHeaderNames.map((h: String) => (h, request.getHeader(h))).toMap)

    }

    def setAccessControlHeaders(response: HttpServletResponse){
      response.setHeader("Access-Control-Allow-Headers","Content-Type, Accept, X-Auth-Token, X-Requested-With")
      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
      response.setHeader("Access-Control-Max-Age","1728000")
      //response.setHeader("Access-Control-Allow-Origin", "*")
      response.setStatus(200)
    }

    def initializeResponse(response:HttpServletResponse): PrintWriter = {
      response.setContentType("application/json")
      response.setCharacterEncoding("utf-8")
      response.setHeader("Access-Control-Allow-Origin","*")
      response.getWriter
    }

    def writeResponse(r: Response, out:PrintWriter) {
      response.setStatus(r.status.id)
      if (r.body.nonEmpty) {
        val json = JSON.toJSON(r.body.get.asInstanceOf[AnyRef])
        out println json.value
      }
      if (r.headers != null) {
        r.headers.keys.foreach { h =>
            val s = h
            val s2 = r.headers(h)
           response.setHeader(s, s2)
        }
      }
    }


    val initialTimeMillis = System.currentTimeMillis()

    val out = initializeResponse(response)

    try {

      if (request.getMethod == "OPTIONS") {
        setAccessControlHeaders(response)
        return
      }

      val (body, parameters, headers) = parseRequest(request)

      val r = api.service(request.getMethod,request.getRequestURI, parameters, headers, body)

      writeResponse(r,out)

    } catch {
      case e: Throwable => {
        response.setStatus(500)
        e.printStackTrace()
        out.println("""{"error":"Internal error"}""")
      }
    } finally {
      logRequest(request,response,initialTimeMillis)
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
