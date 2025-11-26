#!/usr/bin/env bash
set -e

MAVEN_VERSION=3.9.8
MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
MAVEN_TGZ="${MAVEN_DIR}-bin.tar.gz"

echo ">>> [Render] Downloading Maven ${MAVEN_VERSION}"
curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${MAVEN_TGZ}" -o maven.tgz

echo ">>> [Render] Extracting Maven"
tar -xzf maven.tgz

export PATH="$PWD/${MAVEN_DIR}/bin:$PATH"

echo ">>> [Render] Maven version:"
mvn -version

echo ">>> [Render] Building Spring Boot project"
mvn -DskipTests=true package
