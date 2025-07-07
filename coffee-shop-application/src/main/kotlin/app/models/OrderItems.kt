package app.models

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object OrderItems : Table() {
    val orderId = long("order_id").references(Orders.id, onDelete = ReferenceOption.CASCADE)
    val menuItemId = long("menu_item_id").references(MenuItems.id, onDelete = ReferenceOption.CASCADE)
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(orderId, menuItemId)
}
