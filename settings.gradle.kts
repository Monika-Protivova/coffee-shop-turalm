rootProject.name = "backend-development-course"

pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.0"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    }
}

include("deprecated")
include("coffee-shop-integration-tests")
include("coffee-shop-application")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Kotlin
            version("kotlin", "1.9.22")

            // Ktor
            version("ktor", "2.3.7")
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef("ktor")
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef("ktor")
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef("ktor")
            library("ktor-serialization-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
            library("ktor-server-default-headers", "io.ktor", "ktor-server-default-headers").versionRef("ktor")
            library("ktor-server-cors", "io.ktor", "ktor-server-cors").versionRef("ktor")
            library("ktor-server-openapi", "io.ktor", "ktor-server-openapi").versionRef("ktor")
            library("ktor-server-swagger", "io.ktor", "ktor-server-swagger").versionRef("ktor")
            library("ktor-server-auth", "io.ktor", "ktor-server-auth").versionRef("ktor")
            library("ktor-server-auth-jwt", "io.ktor", "ktor-server-auth-jwt").versionRef("ktor")
            library("ktor-server-test-host", "io.ktor", "ktor-server-test-host").versionRef("ktor")
            library("ktor-server-config-yaml", "io.ktor", "ktor-server-config-yaml").versionRef("ktor")
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef("ktor")
            library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef("ktor")

            // Logging
            version("logback", "1.4.14")
            library("logback-classic", "ch.qos.logback", "logback-classic").versionRef("logback")

            library("kotlin-logging", "io.github.oshai", "kotlin-logging-jvm").version("7.0.3")

            // Koin
            version("koin", "3.4.3")
            library("koin-core", "io.insert-koin", "koin-core").versionRef("koin")
            library("koin-ktor", "io.insert-koin", "koin-ktor").versionRef("koin")
            library("koin-logger-slf4j", "io.insert-koin", "koin-logger-slf4j").versionRef("koin")

            // Database
            library("h2", "com.h2database", "h2").version("2.2.224")
            version("exposed", "0.46.0")
            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef("exposed")
            library("exposed-dao", "org.jetbrains.exposed", "exposed-dao").versionRef("exposed")
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef("exposed")
            library("exposed-java-time", "org.jetbrains.exposed", "exposed-java-time").versionRef("exposed")
            library("hikaricp", "com.zaxxer", "HikariCP").version("5.1.0")

            // Testing
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")

            // Kotest
            version("kotest", "5.8.0")
            library("kotest-runner-junit5", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
            library("kotest-assertions-core", "io.kotest", "kotest-assertions-core").versionRef("kotest")
            library("kotest-property", "io.kotest", "kotest-property").versionRef("kotest")

            // Testcontainers
            version("testcontainers", "1.19.3")
            library("testcontainers", "org.testcontainers", "testcontainers").versionRef("testcontainers")
            library("testcontainers-junit-jupiter", "org.testcontainers", "junit-jupiter").versionRef("testcontainers")

            // Plugins
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("ktor", "io.ktor.plugin").version("2.3.7")
        }
    }
}
