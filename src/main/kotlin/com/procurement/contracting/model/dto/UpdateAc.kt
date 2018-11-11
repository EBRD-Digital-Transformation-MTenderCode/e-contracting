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

        val awards: AwardUpdate,

        val contracts: ContractUpdate,

        val planning: Planning,

        val buyer: OrganizationReferenceBuyer,

        val treasuryBudgetSources: List<TreasuryBudgetSource>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractUpdate @JsonCreator constructor(

        val title: String,

        val description: String,

        val period: Period,

        val documents: List<DocumentContract>,

        val milestones: List<Milestone>,

        val confirmationRequests: List<ConfirmationRequest>,

        val agreedMetrics: LinkedList<AgreedMetric>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Milestone @JsonCreator constructor(

        var id: String,

        var title: String,

        var description: String,

        val type: MilestoneType,

        var status: MilestoneStatus,

        var relatedItems: Set<String>?,

        var additionalInformation: String,

        var dueDate: LocalDateTime,

        var relatedParties: RelatedParty?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedParty @JsonCreator constructor(

        val id: String,

        val name: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfirmationRequest @JsonCreator constructor(

        var id: String,

        var type: String?,

        var title: String?,

        var description: String?,

        var relatesTo: String?,

        val relatedItem: String,

        val source: String,

        var requestGroups: Set<RequestGroup>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RequestGroup @JsonCreator constructor(

        val id: String,

        val requests: Set<Request>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Request @JsonCreator constructor(

        val id: String,

        val title: String,

        val description: String,

        val relatedPerson: RelatedPerson?
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class RelatedPerson @JsonCreator constructor(

        val id: String,

        val name: String
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

        var value: ValueUpdate,

        var suppliers: List<OrganizationReferenceSupplierUpdate>,

        var items: List<ItemUpdate>,

        var documents: List<DocumentAward>?
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

        val value: ValueUpdate
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ValueUpdate @JsonCreator constructor(

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        val currency: String,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amountNet: BigDecimal,

        val valueAddedTaxIncluded: Boolean
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class TreasuryBudgetSource @JsonCreator constructor(

        var budgetBreakdownID: String,

        val budgetIBAN: String,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal
)


@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReferenceSupplierUpdate @JsonCreator constructor(

        var id: String,

        val additionalIdentifiers: HashSet<Identifier>,

        val persones: HashSet<Person>,

        val details: DetailsSupplier
)