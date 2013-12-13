import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "interject"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
//        "commons-configuration" % "commons-configuration" % "1.9",
//        "commons-lang" % "commons-lang" % "2.3",
//        "javax.servlet" % "servlet-api" % "2.5",
//        "javax.servlet" % "jstl" % "1.1.2",
//        "org.apache.httpcomponents" % "httpclient" % "4.3.1",
//        "org.apache.logging.log4j" % "log4j-1.2-api" % "2.0-beta9",
//        "com.typesafe" % "config" % "1.0.2",
        "org.apache.tika" % "tika-parsers" % "1.4"

        // or use the snapshot
//        "uk.bl.wa.interject" % "interject-servlet-filter" % "1.0.0-SNAPSHOT"
  )

  lazy val interjectFilter = RootProject(file("../interject-servlet-filter"))
  
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here  
	resolvers += ("Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository")
//	resolvers += ("Apache Snapshots" at "http://repository.apache.org/snapshots/"),
//	resolvers += ("Maven repository" at "http://repo1.maven.org/maven2/")
  )
  //.dependsOn(interjectFilter)
  
}
