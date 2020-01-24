name := "scala-toot-test"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

fork := true

libraryDependencies += "com.github.izzyreal" % "toot2" % "1.1"
libraryDependencies += "com.github.izzyreal" % "tootaudioservers" % "1.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.27"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.27"
libraryDependencies += "org.apache.commons" % "commons-collections4" % "4.4"
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "2.1.1"
