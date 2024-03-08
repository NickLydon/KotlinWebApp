package com.nicklydon.federalministries

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.springframework.stereotype.Service

data class MinistryOrganization(val name: String, val datasetCount: Int)

@Serializable
data class MinistryAndSubordinates(val name: String, val subordinates: List<MinistryAndSubordinates> = listOf())

interface MinistryRepository {
    fun getAll(): List<MinistryAndSubordinates>
}

@Service
private object JsonMinistryRepository : MinistryRepository {

    val file: List<MinistryAndSubordinates> by lazy {
        val fileStream = JsonMinistryRepository::class.java.getResource("/departments.json").openStream()
        Json.decodeFromStream<MinistryJsonFile>(fileStream).departments
    }

    override fun getAll(): List<MinistryAndSubordinates> = file

    @Serializable
    private data class MinistryJsonFile(val departments: List<MinistryAndSubordinates>)
}

enum class SortColumn {
    Name, DatasetCount
}

@Service
class MinistriesService(private val apiClient: GovDataCkanClient, private val ministryRepository: MinistryRepository) {
    suspend fun getAll(sortColumn: SortColumn = SortColumn.Name, sortAscending: Boolean = true): List<MinistryOrganization> {
        fun flattenMinistries(ministries: List<MinistryAndSubordinates>): Sequence<String> = sequence {
            for (ministry in ministries) {
                yield(ministry.name)
                for (child in flattenMinistries(ministry.subordinates)) {
                    yield(child)
                }
            }
        }
        val knownMinistries = flattenMinistries(ministryRepository.getAll()).toHashSet()
        val apiResult = apiClient.getOrganizations().result
        val responseObjects = apiResult
            .filter { org -> knownMinistries.contains(org.title) }
            .map { org -> MinistryOrganization(org.title, org.package_count) }
        val sorted = if (sortAscending) when (sortColumn) {
            SortColumn.DatasetCount -> responseObjects.sortedBy { x -> x.datasetCount }
            SortColumn.Name -> responseObjects.sortedBy { x -> x.name }
        } else when (sortColumn) {
            SortColumn.DatasetCount -> responseObjects.sortedByDescending { x -> x.datasetCount }
            SortColumn.Name -> responseObjects.sortedByDescending { x -> x.name }
        }

        return sorted
    }
}