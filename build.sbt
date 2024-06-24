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
libraryDependencies += "org.mockito" % "mockito-core" % "5.11.0" % Test

// https://mvnrepository.com/artifact/org.mockito/mockito-inline
libraryDependencies += "org.mockito" % "mockito-inline" % "5.2.0" % Test

// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.2" % Test

// https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.10.2" % Test

libraryDependencies += "net.aichler" % "jupiter-interface" % "0.11.1" % Test

Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-a")

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.14"
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.2"

// https://mvnrepository.com/artifact/com.auth0/java-jwt
libraryDependencies += "com.auth0" % "java-jwt" % "4.4.0"

// https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox
libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "3.0.2"

// https://mvnrepository.com/artifact/com.github.jai-imageio/jai-imageio-jpeg2000
libraryDependencies += "com.github.jai-imageio" % "jai-imageio-jpeg2000" % "1.4.0"

// https://mvnrepository.com/artifact/com.levigo.jbig2/levigo-jbig2-imageio
libraryDependencies += "com.levigo.jbig2" % "levigo-jbig2-imageio" % "2.0"





