package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Planning @JsonCreator constructor(

        var implementation: Implementation?,

        val budget: Budget?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Budget @JsonCreator constructor(

        var description: String?,

        val budgetAllocation: Set<BudgetAllocation>?,

        val budgetSource: Set<PlanningBudgetSource>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetAllocation @JsonCreator constructor(

        var budgetBreakdownID: String?,

        val period: Period?,

        val relatedItem: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlanningBudgetSource @JsonCreator constructor(

        var budgetBreakdownID: String?,

        @field: JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal?,

        val currency: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Implementation @JsonCreator constructor(

        val transactions: Set<Transaction>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Transaction @JsonCreator constructor(

        val id: String?,

        val type: String?,

        val value: Value?,

        val executionPeriod: ExecutionPeriod?,

        val relatedContractMilestone: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExecutionPeriod @JsonCreator constructor(

        val durationInDays: Long?
)