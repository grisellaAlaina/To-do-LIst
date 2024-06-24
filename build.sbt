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

 // https://mvnrepository.com/artifact/log4j/log4j
 libraryDependencies += "log4j" % "log4j" % "1.2.17"

// https://mvnrepository.com/artifact/org.mockito/mockito-core
libraryDependencies += "org.mockito" % "mockito-core" % "3.12.4" % Test

// https://mvnrepository.com/artifact/org.mockito/mockito-inline
libraryDependencies += "org.mockito" % "mockito-inline" % "3.12.4" % Test

// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.2" % Test

// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.10.2" % Test

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.14"
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.2"

// https://mvnrepository.com/artifact/com.auth0/java-jwt
libraryDependencies += "com.auth0" % "java-jwt" % "4.4.0"

// https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "3.0.2"




