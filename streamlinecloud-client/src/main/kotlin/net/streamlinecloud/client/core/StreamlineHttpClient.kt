package net.streamlinecloud.client.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

class StreamlineHttpClient(
    val url: String,
    private val apiKey: String
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun get(path: String): HttpResponse =
        client.get("$url$path")

    suspend fun post(path: String, body: Any): HttpResponse =
        client.post("$url$path") { setBody(body) }

    suspend fun put(path: String, body: Any): HttpResponse =
        client.put("$url$path") { setBody(body) }

    suspend fun delete(path: String, body: Any): HttpResponse =
        client.delete("$url$path") { setBody(body) }
}