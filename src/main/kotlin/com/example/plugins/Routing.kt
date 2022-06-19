package com.example.plugins

import com.example.routes.getAllHeroes
import com.example.routes.root
import com.example.routes.searchHeroes
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {

    routing {
        root()
        getAllHeroes()
        searchHeroes()
    }
}
