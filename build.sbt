ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "eu.timepit"         %% "refined"         % "0.11.2",
      "io.github.kirill5k" %% "mongo4cats-core" % "0.7.8"
    )
  )

lazy val parser = (project in file("parser"))
  .settings(
    name := "parser",
    libraryDependencies ++= Seq(
      "io.scalaland"          %% "chimney" % "1.5.0",
      "com.themillhousegroup" %% "scoup"   % "1.0.0"
    )
  )
  .dependsOn(core)
  .aggregate(core)

lazy val crawlerService = (project in file("crawler"))
  .settings(
    name := "crawler_service",
    libraryDependencies ++= Seq(
      "dev.profunktor"              %% "redis4cats-effects"      % "1.7.1",
      "com.softwaremill.sttp.tapir" %% "tapir-core"              % "1.11.7",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % "1.11.7",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % "1.10.15",
      "io.circe"                    %% "circe-refined"           % "0.14.7",
      "io.scalaland"                %% "chimney"                 % "1.5.0",
      "io.github.kirill5k"          %% "mongo4cats-core"         % "0.7.8",
      "io.github.kirill5k"          %% "mongo4cats-circe"        % "0.7.6",
      "com.github.fd4s"             %% "fs2-kafka"               % "3.5.1",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.11.7",
      "org.http4s"                  %% "http4s-ember-server"     % "0.23.27",
      "com.github.pureconfig"       %% "pureconfig-yaml"         % "0.17.7",
      "com.github.pureconfig"       %% "pureconfig-generic"      % "0.17.6",
      "com.thesamet.scalapb"        %% "scalapb-runtime"         % scalapb.compiler.Version.scalapbVersion % "protobuf"
    ),
    Compile / PB.protoSources := Seq(file("schema_registry")),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(core, parser)
  .aggregate(core, parser)

lazy val docService = (project in file("document"))
  .settings(
    name := "doc_service",
    libraryDependencies ++= Seq(
      "io.github.kirill5k"    %% "mongo4cats-core"    % "0.7.8",
      "io.github.kirill5k"    %% "mongo4cats-circe"   % "0.7.6",
      "com.github.fd4s"       %% "fs2-kafka"          % "3.5.1",
      "io.circe"              %% "circe-core"         % "0.14.9",
      "io.circe"              %% "circe-generic"      % "0.14.9",
      "io.scalaland"          %% "chimney"            % "1.5.0",
      "com.github.pureconfig" %% "pureconfig-yaml"    % "0.17.7",
      "com.github.pureconfig" %% "pureconfig-generic" % "0.17.6",
      "com.thesamet.scalapb"  %% "scalapb-runtime"    % scalapb.compiler.Version.scalapbVersion % "protobuf"
    ),
    Compile / PB.protoSources := Seq(file("schema_registry")),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(core)
  .aggregate(core)

lazy val refresher = (project in file("refresher"))
  .settings(
    name := "refresher_tasks",
    libraryDependencies ++= Seq(
      "io.scalaland"          %% "chimney"            % "1.5.0",
      "com.github.fd4s"       %% "fs2-kafka"          % "3.5.1",
      "io.circe"              %% "circe-core"         % "0.14.9",
      "io.circe"              %% "circe-generic"      % "0.14.9",
      "io.github.kirill5k"    %% "mongo4cats-circe"   % "0.7.6",
      "com.github.pureconfig" %% "pureconfig-yaml"    % "0.17.7",
      "com.github.pureconfig" %% "pureconfig-generic" % "0.17.7",
      "com.github.pureconfig" %% "pureconfig-cats"    % "0.17.7",
      "com.thesamet.scalapb"  %% "scalapb-runtime"    % scalapb.compiler.Version.scalapbVersion % "protobuf"
    ),
    Compile / PB.protoSources := Seq(file("schema_registry")),
    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )
  .enablePlugins(JavaAppPackaging)
  .dependsOn(core, parser)
  .aggregate(core, parser)

lazy val root = (project in file("."))
  .settings(
    name := "webcrawler"
  )
