name := "scala-toot-test"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies += "com.github.izzyreal" % "toot2" % "1.1"
libraryDependencies += "com.github.izzyreal" % "tootaudioservers" % "1.1"
