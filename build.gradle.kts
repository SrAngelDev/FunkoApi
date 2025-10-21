plugins {
    java
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
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        // Clase principal
        attributes["Main-Class"] = "srangeldev.Main"
    }
    // Incluir dependencias
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    // Excluir duplicados
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}