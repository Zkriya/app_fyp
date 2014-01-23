name := "myapp"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

val appDependencies = Seq(
  javaJdbc
)

play.Project.playJavaSettings
