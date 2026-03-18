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

sourceSets {
    test {
        java.srcDir("build/generated/sources/annotationProcessor/java/test")
        java.srcDir("build/generated/sources/annotationProcessor/java/main")
    }
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation(project(":annotations"))
    annotationProcessor(project(":processor"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor(project(":processor"))
}

tasks.test {
    useJUnitPlatform()
}