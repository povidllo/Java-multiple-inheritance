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

dependencies {
    compileOnly("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("net.bytebuddy:byte-buddy:1.14.8")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.8")
}