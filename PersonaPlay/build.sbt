name := "PersonaPlay"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final" // replace by your jpa implementation
)     

play.Project.playJavaSettings

Keys.fork in (Test) := false

javaOptions in Test += "-Dtest.timeout=1000000"
