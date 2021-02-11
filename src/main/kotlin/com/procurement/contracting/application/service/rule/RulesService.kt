package com.procurement.contracting.application.service.rule

import com.procurement.contracting.application.repository.rule.RuleRepository
import com.procurement.contracting.application.service.Transform
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

    companion object {
        private const val PARAMETER_STATE_FOR_SETTING = "stateForSetting"
        private const val PARAMETER_VALID_CONTRACT_STATES = "validContractStates"
    }
}
