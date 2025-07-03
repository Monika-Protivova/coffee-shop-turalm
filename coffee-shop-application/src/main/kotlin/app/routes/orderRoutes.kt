package app.routes

import app.models.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.orderRoutes() {
    route("/api/v1/orders") {

        get {
            // Dummy order as before
            val espresso = MenuItem(1, "Espresso", "Strong coffee shot", 2.50)
            val item = OrderItem(menuItem = espresso, quantity = 2)
            val order = Order(
                id = 1,
                customerId = 1,
                menuItems = listOf(item),
                totalPrice = 5.00,
                status = "PENDING",
                isPaid = false
            )
            call.respond(HttpStatusCode.OK, listOf(order))
        }

        post {
            val orderRequest = call.receive<OrderRequest>()

            // Mock menu item lookup
            val espresso = MenuItem(1, "Espresso", "Strong coffee shot", 2.50)

            val orderItems = orderRequest.items.map {
                // Later you'll fetch menuItem from DB by id (it.menuItemId)
                OrderItem(menuItem = espresso, quantity = it.quantity)
            }

            val total = orderItems.sumOf { it.menuItem.price * it.quantity }

            val newOrder = Order(
                id = 99L, // dummy ID for now
                customerId = orderRequest.customerId,
                menuItems = orderItems,
                totalPrice = total,
                status = "PENDING",
                isPaid = false
            )

            call.respond(HttpStatusCode.Created, newOrder)
        }
    }
}