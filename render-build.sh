#!/usr/bin/env bash
set -e

# Версию Maven можно поменять при желании
MAVEN_VERSION=3.9.8

echo ">>> Download Maven ${MAVEN_VERSION}"
curl -sL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION%.*}/apache-maven-${MAVEN_VERSION}-bin.tar.gz" | tar xz

export PATH="$PWD/apache-maven-${MAVEN_VERSION}/bin:$PATH"

echo ">>> Maven version:"
mvn -version

echo ">>> Build Spring Boot app"
mvn -DskipTests=true package
