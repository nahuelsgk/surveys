resolvers += "repo.novus snaps" at "http://repo.novus.com/snapshots/"

resolvers += "scalatools" at "https://oss.sonatype.org/content/groups/scala-tools/"

organization := "edu.upc.fib"

name := "dsbw-1213T"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.7.1" % "test",
	"org.mockito" % "mockito-all" % "1.9.0" % "test",
	"org.eclipse.jetty" % "jetty-servlet" % "8.1.0.RC5",
	"javax.servlet" % "servlet-api" % "2.5",
    "org.mongodb" %% "casbah" % "2.3.0",
	"com.novus" %% "salat" % "1.9.1"
)

net.virtualvoid.sbt.graph.Plugin.graphSettings