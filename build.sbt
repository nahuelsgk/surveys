import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "scalatools" at "https://oss.sonatype.org/content/groups/scala-tools/"

organization := "edu.upc.fib"

name := "dsbw-1213T"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test",
  "org.eclipse.jetty" % "jetty-servlet" % "8.1.0.RC5",
  "javax.servlet" % "servlet-api" % "2.5",
  "org.mongodb" %% "casbah" % "2.5.0",
	"com.novus" %% "salat" % "1.9.2-SNAPSHOT"
)

net.virtualvoid.sbt.graph.Plugin.graphSettings