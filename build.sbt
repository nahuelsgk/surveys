import com.typesafe.startscript.StartScriptPlugin

organization := "edu.upc.fib"

name := "dsbw-1213T"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
	"org.eclipse.jetty" % "jetty-servlet" % "8.1.0.RC5",
	"javax.servlet" % "servlet-api" % "2.5"
)

seq(StartScriptPlugin.startScriptForClassesSettings: _*)
