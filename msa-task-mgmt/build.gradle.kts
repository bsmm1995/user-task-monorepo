import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

// Get versions from parent
val javaVersion: String by rootProject.extra
val springdocOpenApiVersion: String by rootProject.extra
val jakartaValidationVersion: String by rootProject.extra
val lombokVersion: String by rootProject.extra
val postgresqlVersion: String by rootProject.extra
val mapstructVersion: String by rootProject.extra

plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.openapi.generator")
}

// Ensure the build directory is ready
val buildDir = layout.buildDirectory.get().asFile

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

// Configure Spring Boot to use the correct main class
springBoot {
    mainClass.set("com.example.taskmgmt.TaskMgmtApplication")
}

sourceSets {
    main {
        java {
            srcDir("build/generated/openapi/src/main/java")
            srcDir("build/generated/user-client/src/main/java")
        }
    }
}

val openApiGenerateTask = tasks.register<GenerateTask>("openApiGenerateTask") {
    group = "openapi"
    description = "Generate Spring server code from OpenAPI specification for Task Management Service"

    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.taskmgmt.infrastructure.adapter.in.rest.api")
    modelPackage.set("com.example.taskmgmt.infrastructure.adapter.in.rest.dto")
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

val openApiGenerateUserClient = tasks.register<GenerateTask>("openApiGenerateUserClient") {
    group = "openapi"
    description = "Generate Java client code for User Management Service"

    generatorName.set("java")
    inputSpec.set("$projectDir/../msa-user-mgmt/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/user-client").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.taskmgmt.infrastructure.adapter.out.client.user.api")
    modelPackage.set("com.example.taskmgmt.infrastructure.adapter.out.client.user.dto")
    invokerPackage.set("com.example.taskmgmt.infrastructure.adapter.out.client.user.invoker")
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
    dependsOn(openApiGenerateTask, openApiGenerateUserClient)
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
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    group = "verification"
    description = "Runs the unit and integration tests"
    useJUnitPlatform()
}
