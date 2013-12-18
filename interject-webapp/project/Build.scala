import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "web"
  val appVersion      = "1.0-SNAPSHOT"

  val mainDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.apache.tika" % "tika-core" % "1.4",
	"uk.bl.wa.interject" % "interject-servlet-filter" % "1.0.0-SNAPSHOT"
  )
  
  val filterDependencies = Seq(
    // Add your project dependencies here,
	"commons-configuration" % "commons-configuration" % "1.9",
	"commons-lang" % "commons-lang" % "2.3",
	"javax.servlet" % "servlet-api" % "2.5",
	"javax.servlet" % "jstl" % "1.1.2",
	"org.apache.httpcomponents" % "httpclient" % "4.3.1",
	"org.apache.logging.log4j" % "log4j-1.2-api" % "2.0-beta9"
  )

  val service = Project("service", file("interject-servlet-filter"),
	  settings = Seq(
      resolvers ++= Seq(
    	"Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
      ),
      libraryDependencies ++= filterDependencies
    )
  )
  
  val web = play.Project(appName, appVersion, mainDependencies).settings(
	// Add your own project settings here
  )
//  .dependsOn(service)
  
//  val root = Project("root", file(".")).aggregate(service, web)
}
