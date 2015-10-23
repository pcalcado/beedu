name := "chaos-mico"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.4" % "test",
  "com.twitter" %% "finagle-httpx" % "6.29.0" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
