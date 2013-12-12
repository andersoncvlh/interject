//example build.sbt
 
//the name of the project, will become the name of the war file when you run the package command.
name := "interject-webapp"
 
version := "1.0"
 
scalaVersion := "2.10.2"

resolvers += ("Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository")

//libraryDependencies += "commons-configuration" % "commons-configuration" % "1.9"
libraryDependencies +="uk.bl.wa.interject" % "interject-servlet-filter" % "1.0.0-SNAPSHOT"
