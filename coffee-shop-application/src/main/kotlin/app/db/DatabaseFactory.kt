package app.db

import app.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger("DatabaseFactory")

    fun init() {
        val url = System.getenv("JDBC_DATABASE_URL")
            ?: "jdbc:h2:mem:coffee_shop;DB_CLOSE_DELAY=-1"
        val driver = "org.h2.Driver"
        val user = "sa"
        val password = ""

        Database.connect(url, driver, user, password)

        transaction {
            logger.info("Creating database tables...")
            SchemaUtils.create(
                Customers,
                MenuItems,
                Orders,
                OrderItems
            )
            logger.info("Database tables created.")
            SchemaUtils.create(Users, Customers, Orders, MenuItems, OrderItems)
            // Optional: Seed test data
            if (MenuItems.selectAll().empty()) {
                MenuItems.insert {
                    it[name] = "Espresso"
                    it[description] = "Strong coffee shot"
                    it[price] = 7.50
                }
                MenuItems.insert {
                    it[name] = "Latte"
                    it[description] = "Milk and espresso"
                    it[price] = 3.50
                }
                MenuItems.insert {
                    it[name] = "Cappuccino"
                    it[description] = "Milk coffee with foam"
                    it[price] = 3.00
                }
                MenuItems.insert {
                    it[name] = "Americano"
                    it[description] = "Dark, strong coffee shot with water"
                    it[price] = 5.00
                }
            }

            if (Customers.selectAll().empty()) {
                Customers.insert {
                    it[userId] = 1001L
                    it[name] = "Mikal Bridges"
                    it[discountPercent] = 10.0
                }
                Customers.insert {
                    it[userId] = 1002L
                    it[name] = "John Sullivan"
                    it[discountPercent] = 15.0
                }
                Customers.insert {
                    it[userId] = 1003L
                    it[name] = "Paul Shearer"
                    it[discountPercent] = 10.0
                }
                Customers.insert {
                    it[userId] = 1004L
                    it[name] = "Anthony Edwards"
                    it[discountPercent] = 5.0
                }
            }

            if (Users.selectAll().empty()) {
                Users.insert {
                    it[username] = "alice"
                    it[passwrd] = "password123"
                    it[role] = "CUSTOMER"
                }
                Users.insert {
                    it[username] = "bob"
                    it[passwrd] = "secret456"
                    it[role] = "STAFF"
                }
            }

        }
    }
}
