#!/usr/bin/env bash
set -e

echo ">>> [Render] Installing Temurin JDK 21"

# Скачиваем JDK 21 (Temurin) для Linux x64
curl -fsSL "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.7%2B6/OpenJDK21U-jdk_x64_linux_hotspot_21.0.7_6.tar.gz" -o temurin.tar.gz

echo ">>> [Render] Extracting JDK"
# Сначала узнаем имя папки внутри архива
JDK_DIR=$(tar -tzf temurin.tar.gz | head -1 | cut -d/ -f1)
tar -xzf temurin.tar.gz

export JAVA_HOME="$PWD/$JDK_DIR"
export PATH="$JAVA_HOME/bin:$PATH"

echo ">>> [Render] JAVA_HOME = $JAVA_HOME"
java -version

echo ">>> [Render] Installing Maven"

MAVEN_VERSION=3.9.8
MAVEN_DIR="apache-maven-${MAVEN_VERSION}"
MAVEN_TGZ="${MAVEN_DIR}-bin.tar.gz"

curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${MAVEN_TGZ}" -o maven.tgz

echo ">>> [Render] Extracting Maven"
tar -xzf maven.tgz

export PATH="$PWD/${MAVEN_DIR}/bin:$PATH"

echo ">>> [Render] Maven version:"
mvn -version

echo ">>> [Render] Building Spring Boot project"
mvn -DskipTests=true package
