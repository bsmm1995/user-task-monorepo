// Version Management - Get versions from root
val javaVersion: String by rootProject.extra
val springBootVersion: String by rootProject.extra

plugins {
    id("java-library")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:${rootProject.extra["springBootVersion"]}")
    compileOnly("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")
    annotationProcessor("org.projectlombok:lombok:${rootProject.extra["lombokVersion"]}")
}
