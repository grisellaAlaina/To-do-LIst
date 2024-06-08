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
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1"

//libraryDependencies += "org.mongodb.morphia" % "morphia" % "1.7.0"
//libraryDependencies += "org.mongodb.morphia" % "morphia-logging-slf4j" % "1.7.0"