package com.procurement.contracting.application.model.can

import com.procurement.contracting.application.service.model.FindCANIdsParams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FindCANIdsParamsTest {

    @Test
    fun testSorting() {
        val unsortedStates = listOf(
            createState(status = "active", statusDetails = null),
            createState(status = null, statusDetails = "active"),
            createState(status = "unsuccessful", statusDetails = "empty"),
            createState(status = "cancelled", statusDetails = "empty"),
            createState(status = null, statusDetails = "contractProject")
        )
        val expected = listOf(
            createState(status = "active", statusDetails = null),
            createState(status = "cancelled", statusDetails = "empty"),
            createState(status = "unsuccessful", statusDetails = "empty"),
            createState(status = null, statusDetails = "active"),
            createState(status = null, statusDetails = "contractProject")
        )

        val sortedStates = unsortedStates.sorted()
        assertEquals(expected, sortedStates)
    }

    @Test
    fun testSortByLotStatus() {
        val unsortedStates = listOf(
            createState(status = "cancelled", statusDetails = null),
            createState(status = "active", statusDetails = null),
            createState(status = "pending", statusDetails = null),
            createState(status = "active", statusDetails = null)
        )
        val expected = listOf(
            createState(status = "active", statusDetails = null),
            createState(status = "active", statusDetails = null),
            createState(status = "cancelled", statusDetails = null),
            createState(status = "pending", statusDetails = null)
        )

        val sortedStates = unsortedStates.sorted()
        assertEquals(expected, sortedStates)
    }

    @Test
    fun testSortByLotStatusDetails() {
        val unsortedStates = listOf(
            createState(status = null, statusDetails = "empty"),
            createState(status = null, statusDetails = "empty"),
            createState(status = null, statusDetails = "contractProject")
        )
        val expected = listOf(
            createState(status = null, statusDetails = "contractProject"),
            createState(status = null, statusDetails = "empty"),
            createState(status = null, statusDetails = "empty")
        )

        val sortedStates = unsortedStates.sorted()
        assertEquals(expected, sortedStates)
    }

    @Test
    fun testSortDuplicate() {
        val unsortedStates = listOf(
            createState(status = "active", statusDetails = "contractProject"),
            createState(status = null, statusDetails = "empty"),
            createState(status = "active", statusDetails = "empty"),
            createState(status = "active", statusDetails = "contractProject")
        )
        val expected = listOf(
            createState(status = "active", statusDetails = "contractProject"),
            createState(status = "active", statusDetails = "contractProject"),
            createState(status = "active", statusDetails = "empty"),
            createState(status = null, statusDetails = "empty")
        )

        val sortedStates = unsortedStates.sorted()
        assertEquals(expected, sortedStates)
    }

    @Test
    fun testSortNulls() {
        val unsortedStates = listOf(
            createState(status = "active", statusDetails = null),
            createState(status = null, statusDetails = "empty"),
            createState(status = "active", statusDetails = "empty"),
            createState(status = null, statusDetails = "contractProject"),
            createState(status = "cancelled", statusDetails = null)
        )
        val expected = listOf(
            createState(status = "active", statusDetails = "empty"),
            createState(status = "active", statusDetails = null),
            createState(status = "cancelled", statusDetails = null),
            createState(status = null, statusDetails = "contractProject"),
            createState(status = null, statusDetails = "empty")
        )

        val sortedStates = unsortedStates.sorted()
        assertEquals(expected, sortedStates)
    }

    private fun createState(status: String?, statusDetails: String?): FindCANIdsParams.State {
        return FindCANIdsParams.State.tryCreate(status, statusDetails).get
    }
}
