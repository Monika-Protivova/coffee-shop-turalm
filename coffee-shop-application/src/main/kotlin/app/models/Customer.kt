package app.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Customer(
    val id: Long,
    val userId: Long,
    val name: String,
    val discountPercent: Double
)

object Customers : Table() {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val name = varchar("name", 255)
    val discountPercent = double("discount_percent")

    override val primaryKey = PrimaryKey(id)
}
