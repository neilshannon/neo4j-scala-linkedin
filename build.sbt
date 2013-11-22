organization := "com.neilshannon"

name := "neo4j-linkedin"

version := "0.1.1-SNAPSHOT"

scalaVersion := "2.10.2"

parallelExecution in Test := false

resolvers ++= Seq(
  "github-repo" at "https://raw.github.com/FaKod/fakod-mvn-repo/master/releases",
  "github-repo-snapshots" at "https://raw.github.com/FaKod/fakod-mvn-repo/master/snapshots",
  "neo4j.org" at "http://m2.neo4j.org/"
)

libraryDependencies ++= Seq(
   "net.databinder" %% "unfiltered-filter" % "0.7.0",
   "org.clapper" %% "avsl" % "1.0.1",   
   "com.typesafe" % "config" % "1.0.0",
   "net.databinder" %% "dispatch-core" % "0.8.10",
   "net.databinder" %% "dispatch-http" % "0.8.10",
   "net.liftweb" %% "lift-json" % "2.6-M2",
   "org.squeryl" %% "squeryl" % "0.9.5-6",
   "org.neo4j" % "neo4j" % "1.9.4",
   "com.h2database" % "h2" % "1.2.127",
   "net.databinder" %% "unfiltered-specs2" % "0.7.0" % "test",
   "junit" % "junit" % "4.7",
   "javax.servlet" % "servlet-api" % "2.3" % "provided",
   "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "container, compile"
)

testOptions in Test += Tests.Argument("junitxml")

seq(webSettings :_*)
