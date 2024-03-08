package com.nicklydon.federalministries

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinWebAppApiTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldReturnOrganizationsMatchingMinistries() {
        runTestUsingWireMock { mockServer ->
            mockServer.stubFor(
                get("/action/organization_list?all_fields=true")
                    .willReturn(ok()
                        .withHeader("Content-Type", "application/json;charset=utf-8")
                        .withBody("""{
                                    "success": true,
                                    "result": [
                                      { "title": "Auswärtiges Amt", "package_count": 1 }
                                    ]
                                    }""".trimMargin())
                    ))

            val apiResult = restTemplate.getForEntity<Response>("/ministries")

            Assertions.assertEquals(HttpStatus.OK, apiResult.statusCode)
            Assertions.assertNotEquals(0, apiResult.body?.ministries?.size)
        }
    }

    @Test
    fun shouldReturnEmptyListWhenNoOrganisationsPresent() {
        runTestUsingWireMock { mockServer ->
            mockServer.stubFor(
                get("/action/organization_list?all_fields=true")
                    .willReturn(ok()
                        .withHeader("Content-Type", "application/json;charset=utf-8")
                        .withBody("""{
                                    "success": true,
                                    "result": []
                                    }""".trimMargin())
                    ))

            val apiResult = restTemplate.getForEntity<Response>("/ministries")

            Assertions.assertEquals(HttpStatus.OK, apiResult.statusCode)
            Assertions.assertEquals(0, apiResult.body?.ministries?.size)
        }
    }

    fun runTestUsingWireMock(test: (WireMockServer) -> Unit) {
        val mockServer = WireMockServer(65001)
        mockServer.start()
        try {
            test(mockServer)
        } finally {
            mockServer.stop()
        }
    }

}
