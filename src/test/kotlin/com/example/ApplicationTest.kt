package com.example

import com.example.moduls.ApiResponse
import com.example.plugins.configureRouting
import com.example.repo.HeroRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun accessRootEndpoint_AssertCorrectInformation() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            assertEquals(
                expected = "\"Welcome to Boruto API!\"",
                actual = bodyAsText()
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=6").apply {
            assertEquals(
                expected = HttpStatusCode.NotFound,
                actual = status
            )
            val expected = ApiResponse(
                success = false,
                message = "Heroes not Found."
            )
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
            println("Expected: $expected")
            println("Actual: $actual")
            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes?page=invalid").apply {
            assertEquals(
                expected = HttpStatusCode.BadRequest,
                actual = status
            )
            val expected = ApiResponse(
                success = false,
                message = "Only Numbers Allowed."
            )
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
            println("Expected: $expected")
            println("Actual: $actual")
            assertEquals(
                expected = expected,
                actual = actual
            )
        }
    }

    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() = testApplication {
        application {
            configureRouting()
        }
        val pages = 1..5
        val heroes = listOf(
            heroRepository.page1,
            heroRepository.page2,
            heroRepository.page3,
            heroRepository.page4,
            heroRepository.page5
        )
        pages.forEach { page ->
            client.get("/boruto/heroes?page=$page").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = status
                )
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = if (page == 1) null else page.minus(1),
                    nextPage = if (page == 5) null else page.plus(1),
                    heroes = heroes[page - 1]
                )
                val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query hero name, assert single hero result`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=sas").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                .heroes.size
            assertEquals(expected = 1, actual = actual)
        }
    }

    @Test
    fun `access all heroes endpoint, query hero name, assert multiply heroes result`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=sa").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                .heroes.size
            assertEquals(expected = 3, actual = actual)
        }
    }

    @Test
    fun `access all heroes endpoint, query an empty text, assert empty list as a result`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                .heroes
            assertEquals(expected = emptyList(), actual = actual)
        }
    }

    @Test
    fun `access all heroes endpoint, query non existing hero, assert empty list as a result`() = testApplication {
        application {
            configureRouting()
        }
        client.get("/boruto/heroes/search?name=unknown").apply {
            assertEquals(
                expected = HttpStatusCode.OK,
                actual = status
            )
            val actual = Json.decodeFromString<ApiResponse>(bodyAsText())
                .heroes
            assertEquals(expected = emptyList(), actual = actual)
        }
    }

}