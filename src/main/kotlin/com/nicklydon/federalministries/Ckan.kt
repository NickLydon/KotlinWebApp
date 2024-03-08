package com.nicklydon.federalministries

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Serializable
data class GovDataCkanResponse<T>(val success: Boolean, val result: T)
@Serializable
data class Organization(val title: String, val package_count: Int)

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

@Service
class GovDataCkanClient {
    @Autowired
    lateinit var appProperties: AppProperties

    suspend fun getOrganizations(): GovDataCkanResponse<List<Organization>> = run {
        return httpClient.get("${appProperties.govDataApiUrl}/action/organization_list?all_fields=true")
            .body<GovDataCkanResponse<List<Organization>>>()
    }
}