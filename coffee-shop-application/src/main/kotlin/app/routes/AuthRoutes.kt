package app.routes

import app.models.Users
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class LoginRequest(val username: String, val password: String)

fun Route.authRoutes(secret: String) {
    route("/login") {
        post {
            val credentials = call.receive<LoginRequest>()

            val user = transaction {
                Users.select {
                    (Users.username eq credentials.username) and
                            (Users.passwrd eq credentials.password)
                }.singleOrNull()
            }

            if (user != null) {
                val token = JWT.create()
                    .withAudience("coffee-users")
                    .withIssuer("coffee-shop-api")
                    .withClaim("username", user[Users.username])
                    .withClaim("role", user[Users.role])
                    .sign(Algorithm.HMAC256(secret))

                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
    }
}
