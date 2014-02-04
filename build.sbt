name := "myapp"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.json" % "json" % "20131018",
   cache
)     

val appDependencies = Seq(
  javaJdbc
)

play.Project.playJavaSettings
