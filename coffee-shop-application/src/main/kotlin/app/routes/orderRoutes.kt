package app.routes

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Route.orderRoutes() {
    route("/api/v1/orders") {
        get {
            call.respond(HttpStatusCode.OK, "List of orders goes here")
        }
    }
}