organization := "com.typesafe.akka.samples"
name := "akka-sample-persistence-scala"

scalaVersion := "2.12.4"

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

lazy val global = (project in file (".")).settings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka"          %% "akka-persistence" % "2.5.13",
    "org.iq80.leveldb"            % "leveldb"          % "0.7",
    "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8"
  )
)
