package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.domain.model.organization.OrganizationId
import com.procurement.contracting.infrastructure.bind.MoneyDeserializer
import com.procurement.contracting.model.dto.ocds.AwardContract
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProceedResponseRq @JsonCreator constructor(

    val confirmationResponse: ConfirmationResponseRq
)

data class ConfirmationResponseRq @JsonCreator constructor(

    val value: ConfirmationResponseValueRq
)

data class ConfirmationResponseValueRq @JsonCreator constructor(

    val id: OrganizationId,

    val date: LocalDateTime,

    val relatedPerson: RelatedPersonRq,

    val verification: List<VerificationRq>
)

data class RelatedPersonRq @JsonCreator constructor(

    val id: String
)

data class VerificationRq @JsonCreator constructor(

    val value: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BuyerSigningRs @JsonCreator constructor(

    val contract: AwardContract
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SupplierSigningRs @JsonCreator constructor(

    val treasuryValidation: Boolean,

    val treasuryBudgetSources: List<TreasuryBudgetSourceSupplierSigning>?,

    val contract: AwardContract
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TreasuryBudgetSourceSupplierSigning @JsonCreator constructor(

    var budgetBreakdownID: String,

    val budgetIBAN: String,

    @JsonDeserialize(using = MoneyDeserializer::class)
    val amount: BigDecimal
)
