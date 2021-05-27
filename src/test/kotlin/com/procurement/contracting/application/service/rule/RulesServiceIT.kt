package com.procurement.contracting.application.service.rule

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.contracting.application.repository.rule.RuleRepository
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.get
import com.procurement.contracting.infrastructure.bind.configuration
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.service.JacksonJsonTransform
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RulesServiceIT {

    companion object {
        const val COUNTRY = "country"
        const val MIN_RECEIVED_CONF_RESPONSES = "minReceivedConfResponses"
        val PMD = ProcurementMethodDetails.TEST_SV
        val OPERATION_TYPE = OperationType.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION
    }

    private lateinit var rulesRepository: RuleRepository
    private lateinit var transform: Transform
    private lateinit var rulesService: RulesService

    @BeforeEach
    fun init() {
        rulesRepository = mock()
        transform = JacksonJsonTransform(ObjectMapper().apply { configuration() })
        rulesService = RulesService(rulesRepository, transform)
    }

    @Test
    fun getMinReceivedConfResponses_string_success() {
        whenever(rulesRepository.get("$COUNTRY-$PMD-$OPERATION_TYPE", MIN_RECEIVED_CONF_RESPONSES))
            .thenReturn("{\"quantity\": \"all\"}".asSuccess())

        val actual = rulesService.getMinReceivedConfResponses(COUNTRY, PMD, OPERATION_TYPE).get()
        assertTrue(actual.quantity is MinReceivedConfResponsesRule.Quantity.All)
    }

    @Test
    fun getMinReceivedConfResponses_stringNotInEnum_fail() {
        whenever(rulesRepository.get("$COUNTRY-$PMD-$OPERATION_TYPE", MIN_RECEIVED_CONF_RESPONSES))
            .thenReturn("{\"quantity\": \"string\"}".asSuccess())

        val actual = rulesService.getMinReceivedConfResponses(COUNTRY, PMD, OPERATION_TYPE) as Result.Failure
        assertTrue(actual.reason is Fail.Incident.Database.DatabaseInteractionIncident)
        assertEquals(actual.reason.description, "Database incident. Error parsing minReceivedConfResponses.")
    }

    @Test
    fun getMinReceivedConfResponses_int_success() {
        whenever(rulesRepository.get("$COUNTRY-$PMD-$OPERATION_TYPE", MIN_RECEIVED_CONF_RESPONSES))
            .thenReturn("{\"quantity\": 1}".asSuccess())

        val expected = MinReceivedConfResponsesRule.Quantity.Number(1)
        val actual = rulesService.getMinReceivedConfResponses(COUNTRY, PMD, OPERATION_TYPE).get()
        assertEquals(expected, actual.quantity)
    }

    @Test
    fun getMinReceivedConfResponses_double_fail() {
        whenever(rulesRepository.get("$COUNTRY-$PMD-$OPERATION_TYPE", MIN_RECEIVED_CONF_RESPONSES))
            .thenReturn("{\"quantity\": 1.23}".asSuccess())

        val actual = rulesService.getMinReceivedConfResponses(COUNTRY, PMD, OPERATION_TYPE) as Result.Failure

        assertTrue(actual.reason is Fail.Incident.Database.DatabaseInteractionIncident)
        assertEquals(actual.reason.description, "Database incident. Error parsing minReceivedConfResponses.")
    }

    @Test
    fun getMinReceivedConfResponses_boolean_fail() {
        whenever(rulesRepository.get("$COUNTRY-$PMD-$OPERATION_TYPE", MIN_RECEIVED_CONF_RESPONSES))
            .thenReturn("{\"quantity\": true}".asSuccess())

        val actual = rulesService.getMinReceivedConfResponses(COUNTRY, PMD, OPERATION_TYPE) as Result.Failure

        assertTrue(actual.reason is Fail.Incident.Database.DatabaseInteractionIncident)
        assertEquals(actual.reason.description, "Database incident. Error parsing minReceivedConfResponses.")
    }
}