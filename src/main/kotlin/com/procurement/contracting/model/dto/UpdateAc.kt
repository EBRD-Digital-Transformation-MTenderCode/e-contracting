package com.procurement.contracting.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.model.dto.databinding.MoneyDeserializer
import com.procurement.contracting.model.dto.databinding.QuantityDeserializer
import com.procurement.contracting.model.dto.ocds.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class UpdateAcRq @JsonCreator constructor(

        val treasuryBudgetSources: List<TreasuryBudgetSource>,

        val planning: Planning,

        val contracts: ContractUpdate,

        val awards: AwardUpdate,

        val buyer: OrganizationReferenceBuyer
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractUpdate @JsonCreator constructor(

        val title: String,

        val description: String,

        val period: Period,

        val documents: List<Document>,

        val milestones: List<Milestone>,

        val confirmationRequests: List<ConfirmationRequest>,

        val agreedMetrics: LinkedList<AgreedMetric>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Milestone @JsonCreator constructor(

        val id: String,

        val title: String,

        val description: String,

        val type: String,

        val relatedItems: Set<String>?,

        val additionalInformation: String,

        val dueDate: LocalDateTime
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfirmationRequest @JsonCreator constructor(

        val id: String,

        val relatedItem: String,

        val source: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AgreedMetric @JsonCreator constructor(

        var id: String,

        val title: String,

        val description: String,

        var observations: LinkedList<Observation>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Observation @JsonCreator constructor(

        val id: String,

        val notes: String,

        val measure: Any,

        val unit: ObservationUnit?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ObservationUnit @JsonCreator constructor(

        val id: String,

        val name: String,

        val scheme: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardUpdate @JsonCreator constructor(

        val id: String,

        var value: ValueTax,

        var suppliers: List<OrganizationReferenceSupplier>,

        var items: List<ItemUpdate>,

        var documents: List<Document>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ItemUpdate @JsonCreator constructor(

        val id: String,

        @JsonDeserialize(using = QuantityDeserializer::class)
        val quantity: BigDecimal,

        val unit: UnitUpdate,

        val deliveryAddress: Address
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UnitUpdate @JsonCreator constructor(

        val value: ValueTax
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class TreasuryBudgetSource @JsonCreator constructor(

        var budgetBreakdownID: String,

        val budgetIBAN: String,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReferenceSupplier @JsonCreator constructor(

        var id: String,

        val additionalIdentifiers: HashSet<Identifier>,

        val persones: HashSet<Person>,

        val details: DetailsSupplier
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReferenceBuyer @JsonCreator constructor(

        var id: String,

        val name: String,

        val identifier: Identifier,

        val address: Address,

        val contactPoint: ContactPoint,

        val additionalIdentifiers: HashSet<Identifier>,

        val persones: List<Person>,

        val details: DetailsBuyer
)