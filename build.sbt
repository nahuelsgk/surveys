import com.typesafe.startscript.StartScriptPlugin

resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

organization := "edu.upc.fib"

name := "dsbw-1213T"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
	"org.eclipse.jetty" % "jetty-servlet" % "8.1.0.RC5",
	"javax.servlet" % "servlet-api" % "2.5",
	"com.mongodb.casbah" %% "casbah" % "2.1.5-1",
	"com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
)

seq(StartScriptPlugin.startScriptForClassesSettings: _*)
