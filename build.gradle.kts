// Version Management - Centralized version catalog
extra["javaVersion"] = "25"
extra["springBootVersion"] = "4.0.2"
extra["springDependencyManagementVersion"] = "1.1.7"
extra["openApiGeneratorVersion"] = "7.20.0"
extra["springdocOpenApiVersion"] = "3.0.1"
extra["jakartaValidationVersion"] = "3.0.2"
extra["lombokVersion"] = "latest.release"
extra["liquibaseVersion"] = "5.0.1"
extra["postgresqlVersion"] = "42.7.3"
extra["junitVersion"] = "5.10.2"
extra["mapstructVersion"] = "1.6.3"

plugins {
    id("org.springframework.boot") version "4.0.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.openapi.generator") version "7.20.0" apply false
    id("java")
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Configure task documentation for better discoverability
subprojects {
    tasks.withType<Task> {
        // Add descriptions and groups to all tasks for better organization
        when {
            name.contains("openapi", ignoreCase = true) ||
                    name.contains("openApi", ignoreCase = true) -> {
                group = "openapi"
                if (description.isNullOrEmpty()) {
                    description = "OpenAPI code generation and related tasks"
                }
            }

            name.contains("build") && this.name.contains("buildDependents") -> {
                group = "build"
                if (description.isNullOrEmpty()) {
                    description = "Assembles and tests this project"
                }
            }

            name.contains("jar") -> {
                group = "build"
                if (description.isNullOrEmpty()) {
                    description = "Assembles a jar archive containing the main classes"
                }
            }
        }
    }
}
