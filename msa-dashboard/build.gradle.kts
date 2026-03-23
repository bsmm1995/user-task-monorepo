import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

val javaVersion: String by rootProject.extra
val springdocOpenApiVersion: String by rootProject.extra
val jakartaValidationVersion: String by rootProject.extra
val jakartaAnnotationApi: String by rootProject.extra
val lombokVersion: String by rootProject.extra
val jacksonDatabindNullableVersion: String by rootProject.extra
val mapstructVersion: String by rootProject.extra

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

springBoot {
    mainClass.set("com.example.dashboard.DashboardApplication")
}

sourceSets {
    main {
        java {
            srcDir("build/generated/openapi/src/main/java")
            srcDir("build/generated/clients/user/src/main/java")
            srcDir("build/generated/clients/task/src/main/java")
        }
    }
}

// 1. Generate Server Code (Dashboard API)
val openApiGenerateDashboard = tasks.register<GenerateTask>("openApiGenerateDashboard") {
    group = "openapi"
    description = "Generate Spring server code for Dashboard Service"

    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.dashboard.infrastructure.adapter.in.rest.api")
    modelPackage.set("com.example.dashboard.infrastructure.adapter.in.rest.dto")
    importMappings.put("Nullable", "jakarta.annotation.Nullable")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "delegatePattern" to "true",
            "interfaceOnly" to "false",
            "useSpringBoot3" to "false",
            "useSpringBoot4" to "true",
            "useTags" to "true",
            "openApiNullable" to "false",
            "useJakartaEe" to "true",
            "generateSupportingFiles" to "false",
            "useBeanValidation" to "true",
            "performBeanValidation" to "true",
            "additionalModelTypeAnnotations" to "@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);@java.lang.SuppressWarnings(\"deprecation\")",
            "additionalEnumTypeAnnotations" to "@java.lang.SuppressWarnings(\"deprecation\")",
            "additionalApiTypeAnnotations" to "@java.lang.SuppressWarnings(\"deprecation\")",
            "generatedAnnotation" to "false",
            "documentationProvider" to "source"
        )
    )
}

// 2. Generate User Client (Consumes User Service)
val openApiGenerateUserClient = tasks.register<GenerateTask>("openApiGenerateUserClient") {
    group = "openapi"
    description = "Generate Java client for User Management Service"

    generatorName.set("java")
    inputSpec.set("$projectDir/../msa-user-mgmt/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/clients/user").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.dashboard.infrastructure.adapter.out.client.user.api")
    modelPackage.set("com.example.dashboard.infrastructure.adapter.out.client.user.dto")
    invokerPackage.set("com.example.dashboard.infrastructure.adapter.out.client.user.invoker")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "library" to "native",
            "useSpringBoot3" to "false",
            "useSpringBoot4" to "true",
            "generateSupportingFiles" to "true",
            "useJakartaEe" to "true",
            "additionalModelTypeAnnotations" to "@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)"
        )
    )
    // Fix deprecation warnings in generated code
    doLast {
        val outputDirValue = outputDir.get()
        val invokerPath = invokerPackage.get().replace(".", "/")
        fileTree("$outputDirValue/src/main/java/$invokerPath").matching { include("ApiClient.java", "JSON.java") }.forEach { file ->
            val content = file.readText()
            if (!content.contains("@java.lang.SuppressWarnings(\"deprecation\")")) {
                val newContent = content.replace(
                    "public class ",
                    "@java.lang.SuppressWarnings(\"deprecation\")\npublic class "
                )
                file.writeText(newContent)
            }
        }
    }
}

// 3. Generate Task Client (Consumes Task Service)
val openApiGenerateTaskClient = tasks.register<GenerateTask>("openApiGenerateTaskClient") {
    group = "openapi"
    description = "Generate Java client for Task Management Service"

    generatorName.set("java")
    inputSpec.set("$projectDir/../msa-task-mgmt/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/clients/task").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.dashboard.infrastructure.adapter.out.client.task.api")
    modelPackage.set("com.example.dashboard.infrastructure.adapter.out.client.task.dto")
    invokerPackage.set("com.example.dashboard.infrastructure.adapter.out.client.task.invoker")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "library" to "native",
            "useSpringBoot3" to "false",
            "useSpringBoot4" to "true",
            "generateSupportingFiles" to "true",
            "useJakartaEe" to "true",
            "additionalModelTypeAnnotations" to "@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)"
        )
    )
    doLast {
        val outputDirValue = outputDir.get()
        val invokerPath = invokerPackage.get().replace(".", "/")
        fileTree("$outputDirValue/src/main/java/$invokerPath").matching { include("ApiClient.java", "JSON.java") }.forEach { file ->
            val content = file.readText()
            if (!content.contains("@java.lang.SuppressWarnings(\"deprecation\")")) {
                val newContent = content.replace(
                    "public class ",
                    "@java.lang.SuppressWarnings(\"deprecation\")\npublic class "
                )
                file.writeText(newContent)
            }
        }
    }
}

tasks.withType<JavaCompile> {
    dependsOn(openApiGenerateDashboard, openApiGenerateUserClient, openApiGenerateTaskClient)
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    // Project Dependencies
    implementation(project(":msa-common"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Note: No Database dependency needed for this aggregator service

    // Documentation and Utilities
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocOpenApiVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakartaAnnotationApi")
    implementation("org.openapitools:jackson-databind-nullable:$jacksonDatabindNullableVersion")

    // Mapping
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    
    // Code Generation
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
