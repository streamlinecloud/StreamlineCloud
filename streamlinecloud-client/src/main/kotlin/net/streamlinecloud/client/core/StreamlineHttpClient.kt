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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

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

    fun fetchGetResponse(path: String): String =
        runBlocking {
            client.get("$url$path").bodyAsText()
        }

    suspend fun get(path: String): HttpResponse =
        client.get("$url$path")

    suspend fun post(path: String, body: Any): HttpResponse =
        client.post("$url$path") { setBody(body) }

    suspend fun put(path: String, body: Any): HttpResponse =
        client.put("$url$path") { setBody(body) }

    suspend fun delete(path: String, body: Any): HttpResponse =
        client.delete("$url$path") { setBody(body) }

    fun runBlockingGet(path: String): String =
        runBlocking {
            get("$url$path").bodyAsText()
        }

    fun runBlockingPut(path: String, body: Any): String =
        runBlocking {
            put("$url$path", body).bodyAsText()
        }
}