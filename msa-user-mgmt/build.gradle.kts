import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

// Get versions from parent
val javaVersion: String by rootProject.extra
val springdocOpenApiVersion: String by rootProject.extra
val jakartaValidationVersion: String by rootProject.extra
val lombokVersion: String by rootProject.extra
val postgresqlVersion: String by rootProject.extra
val mapstructVersion: String by rootProject.extra
val apachePoiVersion: String by rootProject.extra

plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.openapi.generator")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

// Configure Spring Boot to use the correct main class
springBoot {
    mainClass.set("com.example.usermgmt.UserMgmtApplication")
}

sourceSets {
    main {
        java {
            srcDir("build/generated/openapi/src/main/java")
        }
    }
}

val openApiGenerateUser = tasks.register<GenerateTask>("openApiGenerateUser") {
    group = "openapi"
    description = "Generate Spring server code from OpenAPI specification for User Management Service"

    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.usermgmt.infrastructure.adapter.in.rest.api")
    modelPackage.set("com.example.usermgmt.infrastructure.adapter.in.rest.dto")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "useSpringBoot3" to "true",
            "useTags" to "true",
            "openApiNullable" to "false",
            "generateSupportingFiles" to "false",
            "skipDefaultInterface" to "true"
        )
    )
    // Prevent generation of OpenApiGeneratorApplication class
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "supportingFiles" to ""
        )
    )
}

val openApiGenerateTaskClient = tasks.register<GenerateTask>("openApiGenerateTaskClient") {
    group = "openapi"
    description = "Generate Spring client code from Task Management OpenAPI specification"

    generatorName.set("java")
    inputSpec.set("$rootDir/msa-task-mgmt/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.usermgmt.infrastructure.adapter.out.client.task.api")
    modelPackage.set("com.example.usermgmt.infrastructure.adapter.out.client.task.dto")
    invokerPackage.set("com.example.usermgmt.infrastructure.adapter.out.client.task.invoker")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "library" to "resttemplate",
            "useSpringBoot3" to "true",
            "openApiNullable" to "false",
            "generateSupportingFiles" to "true",
            "useJakartaEe" to "true"
        )
    )
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
            "supportingFiles" to ""
        )
    )
}

tasks.withType<JavaCompile> {
    dependsOn(openApiGenerateUser, openApiGenerateTaskClient)
}

dependencies {
    implementation(project(":msa-common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocOpenApiVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.apache.poi:poi-ooxml:$apachePoiVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    group = "verification"
    description = "Runs the unit and integration tests"
    useJUnitPlatform()
}
