package com.procurement.contracting.application.service.rule

import com.procurement.contracting.application.repository.rule.RuleRepository
import com.procurement.contracting.application.service.Transform
import com.procurement.contracting.application.service.rule.model.MinReceivedConfResponsesRule
import com.procurement.contracting.application.service.rule.model.SourceOfConfirmationRequestRule
import com.procurement.contracting.application.service.rule.model.StateForSettingRule
import com.procurement.contracting.application.service.rule.model.ValidFCStatesRule
import com.procurement.contracting.application.service.tryDeserialization
import com.procurement.contracting.domain.model.OperationType
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.lib.functional.Result
import org.springframework.stereotype.Service

@Service
class RulesService(
    private val ruleRepository: RuleRepository,
    private val transform: Transform
) {

    fun getStateForSetting(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<StateForSettingRule, Fail> = ruleRepository
        .get(country, pmd, PARAMETER_STATE_FOR_SETTING, operationType)
        .onFailure { return it }
        .let { json ->
            json.tryDeserialization<StateForSettingRule>(transform)
                .mapFailure {
                    Fail.Incident.Database.DatabaseInteractionIncident(it.exception)
                }
        }

    fun getValidFCStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<ValidFCStatesRule, Fail> = ruleRepository
        .get(country, pmd, PARAMETER_VALID_CONTRACT_STATES, operationType)
        .onFailure { return it }
        .let { json ->
            json.tryDeserialization<ValidFCStatesRule>(transform)
                .mapFailure {
                    Fail.Incident.Database.DatabaseInteractionIncident(it.exception)
                }
        }

    fun getSourceOfConfirmationRequest(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<SourceOfConfirmationRequestRule, Fail> = ruleRepository
        .get(country, pmd, SOURCE_OF_CONFIRMATION_REQUEST, operationType)
        .onFailure { return it }
        .let { json ->
            json.tryDeserialization<SourceOfConfirmationRequestRule>(transform)
                .mapFailure {
                    Fail.Incident.Database.DatabaseInteractionIncident(it.exception)
                }
        }

    fun getMinReceivedConfResponses(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<MinReceivedConfResponsesRule, Fail> = ruleRepository
        .get(country, pmd, MIN_RECEIVED_CONF_RESPONSES, operationType)
        .onFailure { return it }
        .let { json ->
            json.tryDeserialization<MinReceivedConfResponsesRule>(transform)
                .mapFailure {
                    Fail.Incident.Database.DatabaseInteractionIncident(
                        exception = it.exception, shortDescription = "Error parsing $MIN_RECEIVED_CONF_RESPONSES."
                    )
                }
        }

    companion object {
        private const val PARAMETER_STATE_FOR_SETTING = "stateForSetting"
        private const val PARAMETER_VALID_CONTRACT_STATES = "validContractStates"
        private const val SOURCE_OF_CONFIRMATION_REQUEST = "sourceOfConfirmationRequest"
        private const val MIN_RECEIVED_CONF_RESPONSES = "minReceivedConfResponses"

    }
}
