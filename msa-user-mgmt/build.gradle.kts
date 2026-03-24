import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.springframework.boot")
    id("org.openapi.generator")
}

springBoot {
    mainClass.set("com.example.usermgmt.UserMgmtApplication")
}

sourceSets {
    main {
        java {
            srcDir("build/generated/openapi/src/main/java")
            srcDir("build/generated/task-client/src/main/java")
        }
    }
}

// 1. Generate Server Code (User API)
val openApiGenerateUser = tasks.register<GenerateTask>("openApiGenerateUser") {
    group = "openapi"
    description = "Generate Spring server code from OpenAPI specification for User Management Service"

    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.usermgmt.infrastructure.adapter.in.rest.api")
    modelPackage.set("com.example.usermgmt.infrastructure.adapter.in.rest.dto")
    importMappings.put("Nullable", "jakarta.annotation.Nullable")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "delegatePattern" to "true",
            "interfaceOnly" to "false",
            "useSpringBoot3" to "true",
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
            "documentationProvider" to "springdoc"
        )
    )
}

// 2. Generate Task Client (Consumes Task Service)
val openApiGenerateTaskClient = tasks.register<GenerateTask>("openApiGenerateTaskClient") {
    group = "openapi"
    description = "Generate Java client code for Task Management Service"

    generatorName.set("java")
    inputSpec.set("$projectDir/../msa-task-mgmt/src/main/resources/openapi.yaml".replace("\\", "/"))
    outputDir.set(layout.buildDirectory.dir("generated/task-client").get().asFile.absolutePath.replace("\\", "/"))
    apiPackage.set("com.example.usermgmt.infrastructure.adapter.out.client.task.api")
    modelPackage.set("com.example.usermgmt.infrastructure.adapter.out.client.task.dto")
    invokerPackage.set("com.example.usermgmt.infrastructure.adapter.out.client.task.invoker")
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

tasks.withType<JavaCompile> {
    dependsOn(openApiGenerateUser, openApiGenerateTaskClient)
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation(project(":msa-common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.liquibase:liquibase-core")

    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.postgresql:postgresql:42.7.2")

    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
