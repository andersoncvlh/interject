import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "web"
  val appVersion      = "1.0-SNAPSHOT"

  val mainDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    jdbc,
    anorm,
    cache,
    "org.apache.tika" % "tika-core" % "1.4",
    "org.apache.tika" % "tika-parsers" % "1.4",
    "org.apache.commons" % "commons-io" % "1.3.2",
    "eu.scape-project.nanite" % "nanite-core" % "1.0.72.2",
    "cglib" % "cglib-nodep" % "2.1_3", // DROID tries to bring in 2.2.2, which doesn't work.
    "uk.bl.wa.interject" % "interject-access-core" % "1.0.0-SNAPSHOT",
    "uk.bl.wa.access" % "qaop" % "1.4.1-SNAPSHOT",
    "uk.bl.wa.access" % "VRML97toX3D" % "1.0"
  )
  
//  val filterDependencies = Seq(
//    // Add your project dependencies here,
//	"commons-configuration" % "commons-configuration" % "1.9",
//	"commons-lang" % "commons-lang" % "2.3",
//	"javax.servlet" % "servlet-api" % "2.5",
//	"javax.servlet" % "jstl" % "1.1.2",
//	"org.apache.httpcomponents" % "httpclient" % "4.3.1",
//	"org.apache.logging.log4j" % "log4j-1.2-api" % "2.0-beta9"
//  )

//  val service = Project("service", file("interject-servlet-filter"),
//	  settings = Seq(
//      resolvers ++= Seq(
//    	"Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
//      ),
//      libraryDependencies ++= filterDependencies
//    )
//  )
  
  val web = play.Project(appName, appVersion, mainDependencies).settings(
	// Add your own project settings here
//    resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
  )
//  .dependsOn(service)
  
//  val root = Project("root", file(".")).aggregate(service, web)
}
