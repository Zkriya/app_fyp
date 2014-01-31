name := "myapp"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  "mysql" % "mysql-connector-java" % "5.1.18",
  cache
)     

val appDependencies = Seq(
  javaJdbc
)

play.Project.playJavaSettings
