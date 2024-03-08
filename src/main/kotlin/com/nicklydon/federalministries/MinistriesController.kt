package com.nicklydon.federalministries

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class Response(val ministries: List<Ministry>)
data class Ministry(val name: String, val datasetCount: Int)

@RestController
class MinistriesController(private val ministriesService: MinistriesService) {

    @GetMapping("/ministries")
    suspend fun ministries(@RequestParam("sortColumn") sortColumn: SortColumn? = null,
                           @RequestParam("sortAscending") sortAscending: Boolean? = null) =
        Response(ministriesService.getAll(sortColumn ?: SortColumn.Name, sortAscending ?: true).map { m -> Ministry(m.name, m.datasetCount) })
}