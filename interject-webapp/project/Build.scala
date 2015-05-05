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
    "uk.bl.wa.interject" % "interject-access-core" % "1.0.0-SNAPSHOT",
    "uk.bl.wa.access" % "qaop" % "1.4.1-SNAPSHOT",
    "uk.bl.wa.access" % "VRML97toX3D" % "1.0",
    "org.apache.commons" % "commons-io" % "1.3.2",
    "eu.scape-project.nanite" % "nanite-core" % "1.1.6-82",
    "org.apache.tika" % "tika-core" % "1.8",
    "org.apache.tika" % "tika-parsers" % "1.8",
    "cglib" % "cglib-nodep" % "2.1_3" // DROID tries to bring in 2.2.2, which doesn't work.
  )
    
  val web = play.Project(appName, appVersion, mainDependencies).settings(
	// Add your own project settings here
  )
  
}
