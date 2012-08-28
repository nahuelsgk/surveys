package dsbw

import scala.util.Properties

import org.eclipse.jetty.server.{Server => JettyServer}
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.handler.{HandlerList, ResourceHandler}

class Servlet extends HttpServlet {

  override def service(request: HttpServletRequest, response: HttpServletResponse) {
        response.setContentType("text/plain")
        response.setStatus(200)
        response.getWriter.print("Hello, world!!")
  }

}

object Server extends App {
  val port = Properties.envOrElse("PORT", "8080").toInt
  val server: JettyServer = new JettyServer(port)

  val context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS)
  context.setContextPath("/")
  context.addServlet(new ServletHolder(new Servlet()), "/")

  val resourceHandler = new ResourceHandler
  resourceHandler.setDirectoriesListed(true)
  resourceHandler.setWelcomeFiles(Array("index.html"))
  resourceHandler.setResourceBase("src/main/docroot")

  val handlers = new HandlerList()
  handlers.setHandlers(Array(resourceHandler,context))

  server.setHandler(handlers)
  server.start()
}