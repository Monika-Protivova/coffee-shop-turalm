package app.models

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = long("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val passwrd = varchar("password", 64) // hashed ideally
    val role = varchar("role", 20) // e.g., "CUSTOMER", "STAFF"
    override val primaryKey = PrimaryKey(id)
}
