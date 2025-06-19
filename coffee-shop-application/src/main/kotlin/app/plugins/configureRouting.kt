package app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import app.routes.orderRoutes

fun Application.configureRouting() {
    routing {
        orderRoutes()
    }
}