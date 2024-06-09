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

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.12.14"
libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.3.2"