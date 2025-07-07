package app.routes
import app.models.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.receive
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import app.dto.OrderCreateRequest
import app.dto.OrderItemRequest
import app.dto.OrderUpdateRequest
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal


fun Route.orderRoutes() {
    route("/orders") {

        get {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()

            if (role != "CUSTOMER" && role != "STAFF") {
                call.respond(HttpStatusCode.Forbidden, "Unauthorized")
                return@get
            }
            val orders = transaction {
                Orders.selectAll().map { orderRow ->
                    val orderId = orderRow[Orders.id]

                    val items = OrderItems
                        .select { OrderItems.orderId eq orderId.value }
                        .map { itemRow ->
                            val menuItemRow = MenuItems
                                .select { MenuItems.id eq itemRow[OrderItems.menuItemId] }
                                .single()

                            OrderItem(
                                menuItem = MenuItem(
                                    id = menuItemRow[MenuItems.id],
                                    name = menuItemRow[MenuItems.name],
                                    description = menuItemRow[MenuItems.description],
                                    price = menuItemRow[MenuItems.price]
                                ),
                                quantity = itemRow[OrderItems.quantity]
                            )
                        }

                    Order(
                        id = orderId.value,
                        customerId = orderRow[Orders.customerId],
                        menuItems = items,
                        totalPrice = orderRow[Orders.totalPrice],
                        status = orderRow[Orders.status],
                        isPaid = orderRow[Orders.isPaid]
                    )
                }
            }
            call.respond(HttpStatusCode.OK, orders)
        }

        get("{id}") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()

            if (role != "CUSTOMER" && role != "STAFF") {
                call.respond(HttpStatusCode.Forbidden, "Unauthorized")
                return@get
            }
            val idParam = call.parameters["id"]
            val orderId = idParam?.toLongOrNull()

            if (orderId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format: $idParam")
                return@get
            }

            val order = transaction {
                Orders.select { Orders.id eq orderId }.singleOrNull()?.let { orderRow ->
                    val items = OrderItems
                        .select { OrderItems.orderId eq orderId }
                        .map { itemRow ->
                            val menuItemRow = MenuItems
                                .select { MenuItems.id eq itemRow[OrderItems.menuItemId] }
                                .single()

                            OrderItem(
                                menuItem = MenuItem(
                                    id = menuItemRow[MenuItems.id],
                                    name = menuItemRow[MenuItems.name],
                                    description = menuItemRow[MenuItems.description],
                                    price = menuItemRow[MenuItems.price]
                                ),
                                quantity = itemRow[OrderItems.quantity]
                            )
                        }

                    Order(
                        id = orderId,
                        customerId = orderRow[Orders.customerId],
                        menuItems = items,
                        totalPrice = orderRow[Orders.totalPrice],
                        status = orderRow[Orders.status],
                        isPaid = orderRow[Orders.isPaid]
                    )
                }
            }

            if (order == null) {
                call.respond(HttpStatusCode.NotFound, "Order with ID $orderId not found")
            } else {
                call.respond(HttpStatusCode.OK, order)
            }
        }

        post {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()

            if (role != "CUSTOMER" && role != "STAFF") {
                call.respond(HttpStatusCode.Forbidden, "Unauthorized")
                return@post
            }
            val request = try {
                call.receive<OrderCreateRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.localizedMessage}")
                return@post
            }

            val createdOrder = try {
                transaction {
                    val customerRow = Customers.select { Customers.id eq request.customerId }.singleOrNull()
                        ?: return@transaction null

                    val discount = customerRow[Customers.discountPercent]

                    val itemDetails = request.items.map { item ->
                        val menuItemRow = MenuItems.select { MenuItems.id eq item.menuItemId }.singleOrNull()
                            ?: return@transaction null
                        val price = menuItemRow[MenuItems.price]
                        val subtotal = price * item.quantity
                        Triple(menuItemRow, item.quantity, subtotal)
                    }

                    val originalTotal = itemDetails.sumOf { it.third }
                    val finalPrice = originalTotal * (1 - discount / 100)

                    val insertedOrderId = Orders.insertAndGetId {
                        it[customerId] = request.customerId
                        it[totalPrice] = finalPrice
                        it[status] = "PENDING"
                        it[isPaid] = false
                    }.value

                    itemDetails.forEach { (menuItemRow, quantity, _) ->
                        OrderItems.insert {
                            it[orderId] = insertedOrderId
                            it[menuItemId] = menuItemRow[MenuItems.id]
                            it[OrderItems.quantity] = quantity
                        }
                    }

                    Order(
                        id = insertedOrderId,
                        customerId = request.customerId,
                        totalPrice = finalPrice,
                        status = "PENDING",
                        isPaid = false,
                        menuItems = itemDetails.map { (menuItemRow, quantity, _) ->
                            OrderItem(
                                menuItem = MenuItem(
                                    id = menuItemRow[MenuItems.id],
                                    name = menuItemRow[MenuItems.name],
                                    description = menuItemRow[MenuItems.description],
                                    price = menuItemRow[MenuItems.price]
                                ),
                                quantity = quantity
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error creating order: ${e.localizedMessage}")
                return@post
            }

            if (createdOrder == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid customer or menu item ID")
            } else {
                call.respond(HttpStatusCode.Created, createdOrder)
            }
        }

        put("/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()

            if (role != "CUSTOMER" && role != "STAFF") {
                call.respond(HttpStatusCode.Forbidden, "Unauthorized")
                return@put
            }
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid order ID format")
                return@put
            }

            val updateRequest = call.receive<Map<String, String>>() // or a proper DTO
            val newStatus = updateRequest["status"]
            if (newStatus == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing status field")
                return@put
            }

            val updatedOrder = transaction {
                val updatedRows = Orders.update({ Orders.id eq id }) {
                    it[status] = newStatus
                }

                if (updatedRows == 0) {
                    null
                } else {
                    // Fetch updated order
                    Orders.select { Orders.id eq id }.singleOrNull()
                }
            }

            if (updatedOrder == null) {
                call.respond(HttpStatusCode.NotFound, "Order not found")
            } else {
                call.respond(HttpStatusCode.OK, "Order updated successfully")
            }
        }
    }
}

