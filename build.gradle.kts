plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("io.freefair.lombok") version "9.0.0"
}

group = "srangeldev"
version = "0.0.1-SNAPSHOT"
description = "FunkoApi"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    //Lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //bbdd h2
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")

    // Websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "srangeldev.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

jacoco {
    toolVersion = "0.8.14"
}

// Configura la tarea de reporte de JaCoCo
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named<Test>("test"))
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
}
