package com.jcl.parcialplataformas.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {

    @Volatile
    private var INSTANCE: HttpClient? = null

    fun create(): HttpClient {
        return INSTANCE ?: synchronized(this) {
            val instance = HttpClient(CIO.create()) {
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }

                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.DEFAULT
                }

                defaultRequest {
                    url("https://rest.coincap.io/v3/assets")
                    contentType(ContentType.Application.Json)
                    header(
                        "Authorization", "Bearer 6f8c2f757cc81e9950a05aeed8292abff853114ebc731977f3f5a580b1e9371a"
                    )
                }
            }
            INSTANCE = instance
            instance
        }
    }
}