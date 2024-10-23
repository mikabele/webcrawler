FROM sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_2.13.13 as buildenv

# prevent this error: java.lang.IllegalStateException: cannot run sbt from root directory without -Dsbt.rootdir=true; see sbt/sbt#1458

ARG MODULE_NAME

COPY . /webcrawler

WORKDIR /webcrawler

RUN sbt $MODULE_NAME/compile $MODULE_NAME/stage

#TODO (mikabele): use jre instead of jdk
FROM openjdk:17 as runenv

ARG SOURCE_ROOT

ARG SERVICE_NAME

ENV SERVICE_NAME_ENV=$SERVICE_NAME

COPY --from=buildenv /webcrawler/$SOURCE_ROOT/target /target

ENTRYPOINT /bin/sh /target/universal/stage/bin/$SERVICE_NAME_ENV

