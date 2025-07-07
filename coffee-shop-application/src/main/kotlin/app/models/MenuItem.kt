package app.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class MenuItem(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double
)

object MenuItems : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255)
    val description = varchar("description", 1024)
    val price = double("price")

    override val primaryKey = PrimaryKey(id)
}
