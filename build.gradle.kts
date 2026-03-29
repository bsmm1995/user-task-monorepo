import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.dependencyManagement) apply false
    alias(libs.plugins.openapiGenerator) apply false
    java
}

allprojects {
    group = "com.example"
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.findVersion("java").get().toString()))
        }
    }

    // Configuración centralizada de Spring Boot BOM
    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.findVersion("springBoot").get()}")
        }
    }

    // Lombok común para todos (evitamos boilerplate en cada build.gradle)
    dependencies {
        "compileOnly"(libs.findLibrary("lombok").get())
        "annotationProcessor"(libs.findLibrary("lombok").get())
        "testCompileOnly"(libs.findLibrary("lombok").get())
        "testAnnotationProcessor"(libs.findLibrary("lombok").get())
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        // Soporte para Lombok y Java 21+ con modularidad de compilador
        options.forkOptions.jvmArgs = mutableListOf(
            "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
        )
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
