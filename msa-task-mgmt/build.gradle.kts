import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.springframework.boot")
    id("org.openapi.generator")
}

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
    dependsOn(openApiGenerateTask, openApiGenerateUserClient)
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation(project(":msa-common"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.liquibase.core)

    implementation(libs.springdoc.ui)
    implementation(libs.jakarta.validation.api)
    implementation(libs.jakarta.annotation.api)
    implementation(libs.jackson.databind.nullable)

    implementation(libs.mapstruct)
    implementation(libs.postgresql)

    annotationProcessor(libs.mapstruct.processor)

    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}
