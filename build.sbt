ThisBuild / scalaVersion := "2.13.14"

ThisBuild / version := "1.0-SNAPSHOT"



lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """ToDoList""",
    libraryDependencies ++= Seq(
      guice
    )
  )

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "5.11.0" % Test,
  "org.mockito" % "mockito-inline" % "5.2.0" % Test,
  "junit" % "junit" % "4.13.2" % Test
)

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.14"
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.2"
// https://mvnrepository.com/artifact/com.auth0/java-jwt
libraryDependencies += "com.auth0" % "java-jwt" % "4.4.0"
// https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "3.0.2"

