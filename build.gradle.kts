import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar

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

    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.findVersion("springBoot").get()}")
        }
    }

    dependencies {
        "compileOnly"(libs.findLibrary("lombok").get())
        "annotationProcessor"(libs.findLibrary("lombok").get())
        "testCompileOnly"(libs.findLibrary("lombok").get())
        "testAnnotationProcessor"(libs.findLibrary("lombok").get())
    }

    tasks.withType<BootJar> {
        layered {
            enabled.set(true)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
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
