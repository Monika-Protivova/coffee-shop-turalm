package app.models

import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double
)

@Serializable
data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int
)

@Serializable
data class Order(
    val id: Long,
    val customerId: Long,
    val menuItems: List<OrderItem>,
    val totalPrice: Double,
    val status: String,   // You can later replace with an enum
    val isPaid: Boolean
)