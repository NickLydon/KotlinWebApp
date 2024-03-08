@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nicklydon.federalministries

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MinistriesServiceTests {
    @Test
    fun returnsOrganizationsMatchingMinistries() = runTest {
        val mockRepo = mockk<MinistryRepository>()
        every { mockRepo.getAll() } returns listOf(MinistryAndSubordinates("a", listOf()))
        val apiClientMock = mockk<GovDataCkanClient>()
        coEvery { apiClientMock.getOrganizations() } returns GovDataCkanResponse(true, listOf(Organization("a", 10)))
        val sut = MinistriesService(apiClientMock, mockRepo)
        assertEquals(listOf(MinistryOrganization("a", 10)), sut.getAll())
    }

    @Test
    fun excludesResultWithUnknownTitles() = runTest {
        val mockRepo = mockk<MinistryRepository>()
        every { mockRepo.getAll() } returns listOf(MinistryAndSubordinates("a", listOf()))
        val apiClientMock = mockk<GovDataCkanClient>()
        coEvery { apiClientMock.getOrganizations() } returns GovDataCkanResponse(true, listOf(Organization("_", 10)))
        val sut = MinistriesService(apiClientMock, mockRepo)
        assertEquals(listOf<MinistryOrganization>(), sut.getAll())
    }

    @Test
    fun returnsOrganizationsMatchingChildMinistries() = runTest {
        val mockRepo = mockk<MinistryRepository>()
        every { mockRepo.getAll() } returns listOf(MinistryAndSubordinates("a", listOf(MinistryAndSubordinates("aa", listOf()))))
        val apiClientMock = mockk<GovDataCkanClient>()
        coEvery { apiClientMock.getOrganizations() } returns GovDataCkanResponse(true, listOf(Organization( "aa", 5)))
        val sut = MinistriesService(apiClientMock, mockRepo)
        assertEquals(listOf(MinistryOrganization("aa", 5)), sut.getAll())
    }

    @Test
    fun returnsOrganizationsMatchingChildMinistries2() = runTest {
        val mockRepo = mockk<MinistryRepository>()
        every { mockRepo.getAll() } returns listOf(
            MinistryAndSubordinates(
                "a",
                listOf(
                    MinistryAndSubordinates(
                    "aa",
                    listOf(MinistryAndSubordinates("aaa", listOf())))
                ))
        )
        val apiClientMock = mockk<GovDataCkanClient>()
        coEvery { apiClientMock.getOrganizations() } returns GovDataCkanResponse(true, listOf(Organization( "aaa", 1)))
        val sut = MinistriesService(apiClientMock, mockRepo)
        assertEquals(listOf(MinistryOrganization("aaa", 1)), sut.getAll())
    }

    @Test
    fun sortableByDatasetCount() =
        sortingTest(
            SortColumn.DatasetCount,
            true,
            listOf(
                MinistryOrganization("aa", 1),
                MinistryOrganization("aaa", 2),
                MinistryOrganization("a", 3),
            ))

    @Test
    fun sortableByDatasetCountDesc() =
        sortingTest(
            SortColumn.DatasetCount,
            false,
            listOf(
                MinistryOrganization("a", 3),
                MinistryOrganization("aaa", 2),
                MinistryOrganization("aa", 1),
            ))

    @Test
    fun sortableByName() =
        sortingTest(
            SortColumn.Name,
            true,
            listOf(
                MinistryOrganization("a", 3),
                MinistryOrganization("aa", 1),
                MinistryOrganization("aaa", 2),
            ))

    @Test
    fun sortableByNameDesc() =
        sortingTest(
            SortColumn.Name,
            false,
            listOf(
                MinistryOrganization("aaa", 2),
                MinistryOrganization("aa", 1),
                MinistryOrganization("a", 3),
            ))

    private fun sortingTest(
        sortColumn: SortColumn,
        sortAscending: Boolean,
        expectedMinistryOrganizations: List<MinistryOrganization>
    ) = runTest {
        val mockRepo = mockk<MinistryRepository>()
        every { mockRepo.getAll() } returns listOf(
            MinistryAndSubordinates(
            "a",
            listOf(
                MinistryAndSubordinates(
                "aa",
                listOf(MinistryAndSubordinates("aaa", listOf())))
            ))
        )
        val apiClientMock = mockk<GovDataCkanClient>()
        coEvery { apiClientMock.getOrganizations() } returns GovDataCkanResponse(
            true,
            listOf(
                Organization( "aaa", 2),
                Organization( "a", 3),
                Organization( "aa", 1)
            ))
        val sut = MinistriesService(apiClientMock, mockRepo)
        assertEquals(
            expectedMinistryOrganizations,
            sut.getAll(sortColumn, sortAscending))
    }


}