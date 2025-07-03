package app.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val menuItemId: Long,
    val quantity: Int
)

@Serializable
data class OrderRequest(
    val customerId: Long,
    val items: List<OrderItemRequest>
)