package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Planning @JsonCreator constructor(

        var implementation: Implementation,

        val budget: Budget
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Implementation @JsonCreator constructor(

        var transactions: Set<Transaction>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Transaction @JsonCreator constructor(

        var id: String,

        val type: TransactionType,

        val value: Value,

        val executionPeriod: ExecutionPeriod,

        val relatedContractMilestone: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExecutionPeriod @JsonCreator constructor(

        val durationInDays: Long
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Budget @JsonCreator constructor(

        var description: String,

        val budgetAllocation: List<BudgetAllocation>,

        val budgetSource: List<PlanningBudgetSource>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BudgetAllocation @JsonCreator constructor(

        var budgetBreakdownID: String,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        val period: Period,

        val relatedItem: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlanningBudgetSource @JsonCreator constructor(

        var budgetBreakdownID: String,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        val currency: String
)
