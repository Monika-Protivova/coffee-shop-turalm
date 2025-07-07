package app.dto
import kotlinx.serialization.Serializable

@Serializable
data class OrderCreateRequest(
    val customerId: Long,
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    val menuItemId: Long,
    val quantity: Int
)

@Serializable
data class OrderUpdateRequest(
    val status: String
)