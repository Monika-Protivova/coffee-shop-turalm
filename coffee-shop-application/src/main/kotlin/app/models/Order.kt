package app.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.*      // Only if you use Java time
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*

@Serializable
data class Order(
    val id: Long,
    val customerId: Long,
    val menuItems: List<OrderItem>,
    val totalPrice: Double,
    val status: String,
    val isPaid: Boolean
)

object Orders : LongIdTable() {
    val customerId = long("customer_id")
    val totalPrice = double("total_price")
    val status = varchar("status", 50)
    val isPaid = bool("is_paid")
}

