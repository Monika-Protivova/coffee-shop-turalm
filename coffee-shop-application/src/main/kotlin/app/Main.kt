package app
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import io.ktor.server.plugins.contentnegotiation.*
import app.db.DatabaseFactory
import app.routes.orderRoutes
import app.routes.authRoutes
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val jwtSecret = "secret" // <-- unify
    val jwtIssuer = "coffee-shop-api"
    val jwtAudience = "coffee-users"
    val jwtRealm = "coffee-shop"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("role").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        static("/static") {
            resources("static")
        }
        get("/order-form") {
            call.respondRedirect("/static/order-form.html")
        }
        authenticate("auth-jwt") {
            route("/api/v1") {
                orderRoutes()
            }
        }
        // Login route should NOT be behind JWT auth!
        route("/api/v1") {
            authRoutes(jwtSecret)
        }
    }
}
