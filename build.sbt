name := "W2VQuery"

organization := "PYA Analytics"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies ++=  Seq(
  "org.apache.spark" %% "spark-mllib" % "1.2.1",
  "com.github.scopt" %% "scopt" % "3.2.0"
)
