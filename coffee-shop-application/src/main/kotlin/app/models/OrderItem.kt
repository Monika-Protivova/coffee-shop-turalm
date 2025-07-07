package app.models

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int
)
