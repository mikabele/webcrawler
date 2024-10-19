ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "eu.timepit" %% "refined" % "0.11.2"
    )
  )

lazy val crawlerService = (project in file("crawler"))
  .settings(
    name := "crawler-service",
    libraryDependencies ++= Seq(
      "dev.profunktor"              %% "redis4cats-effects"  % "1.7.1",
      "com.themillhousegroup"       %% "scoup"               % "1.0.0",
      "com.softwaremill.sttp.tapir" %% "tapir-core"          % "1.11.7",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.11.7",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % "1.10.15",
      "io.circe"                    %% "circe-refined"       % "0.14.7",
    )
  )
  .dependsOn(core)
  .aggregate(core)

lazy val docService = (project in file("document"))
  .settings(
    name := "doc-service",
    libraryDependencies ++= Seq()
  )
  .dependsOn(core)
  .aggregate(core)

lazy val refresher = (project in file("refresher"))
  .settings(
    name := "refresher",
    libraryDependencies ++= Seq()
  )
  .dependsOn(core)
  .aggregate(core)

lazy val root = (project in file("."))
  .settings(
    name := "webcrawler"
  )
