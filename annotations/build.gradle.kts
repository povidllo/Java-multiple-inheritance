plugins {
    id("java")
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "kuzminov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}