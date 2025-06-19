package app

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import app.plugins.configureRouting
import app.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}