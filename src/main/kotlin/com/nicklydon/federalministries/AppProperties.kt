package com.nicklydon.federalministries

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${govdata.api.url}")
    lateinit var govDataApiUrl: String
}