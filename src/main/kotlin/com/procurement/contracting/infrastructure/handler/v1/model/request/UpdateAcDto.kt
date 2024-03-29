package com.procurement.contracting.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.document.type.DocumentTypeAward
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.item.ItemId
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.organization.MainEconomicActivity
import com.procurement.contracting.domain.model.organization.OrganizationId
import com.procurement.contracting.infrastructure.bind.BooleansDeserializer
import com.procurement.contracting.infrastructure.bind.MoneyDeserializer
import com.procurement.contracting.infrastructure.bind.quantity.QuantityDeserializer
import com.procurement.contracting.model.dto.ocds.Address
import com.procurement.contracting.model.dto.ocds.AgreedMetric
import com.procurement.contracting.model.dto.ocds.AwardContract
import com.procurement.contracting.model.dto.ocds.BankAccount
import com.procurement.contracting.model.dto.ocds.ConfirmationRequest
import com.procurement.contracting.model.dto.ocds.ContractedAward
import com.procurement.contracting.model.dto.ocds.Identifier
import com.procurement.contracting.model.dto.ocds.LegalForm
import com.procurement.contracting.model.dto.ocds.Milestone
import com.procurement.contracting.model.dto.ocds.OrganizationReference
import com.procurement.contracting.model.dto.ocds.OrganizationReferenceBuyer
import com.procurement.contracting.model.dto.ocds.Period
import com.procurement.contracting.model.dto.ocds.Permits
import com.procurement.contracting.model.dto.ocds.Person
import com.procurement.contracting.model.dto.ocds.Planning
import java.math.BigDecimal
import java.util.*

data class UpdateAcRq @JsonCreator constructor(

    val award: AwardUpdate,

    val contract: ContractUpdate,

    val planning: Planning,

    val buyer: OrganizationReferenceBuyer,

    val funders: List<OrganizationReference>?,

    val payers: List<OrganizationReference>?,

    val treasuryBudgetSources: List<TreasuryBudgetSource>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardUpdate @JsonCreator constructor(

    val id: AwardId,

    var description: String?,

    var value: ValueUpdate,

    var items: List<ItemUpdate>,

    var documents: List<DocumentAward>?,

    var suppliers: List<OrganizationReferenceSupplierUpdate>
){
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class DocumentAward @JsonCreator constructor(

        val id: String,

        var documentType: DocumentTypeAward,

        var title: String,

        var description: String?,

        var relatedLots: List<LotId>?
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ContractUpdate @JsonCreator constructor(

    val title: String,

    val description: String,

    val period: Period,

    val documents: List<DocumentContract>?,

    val milestones: List<Milestone>,

    val confirmationRequests: MutableList<ConfirmationRequest>?,

    val agreedMetrics: LinkedList<AgreedMetric>
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class DocumentContract @JsonCreator constructor(

        val id: String,

        var documentType: DocumentTypeContract,

        var title: String,

        var description: String?,

        var relatedLots: List<LotId>?,

        var relatedConfirmations: List<String>? = null
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ItemUpdate @JsonCreator constructor(

    val id: ItemId,

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

    @JsonDeserialize(using = BooleansDeserializer::class)
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

    var id: OrganizationId,

    val additionalIdentifiers: List<Identifier>,

    val persones: List<Person>,

    val details: DetailsSupplierUpdate
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DetailsSupplierUpdate @JsonCreator constructor(

    val typeOfSupplier: String,

    val mainEconomicActivities: List<MainEconomicActivity>,

    val scale: String,

    val permits: List<Permits>?,

    val bankAccounts: List<BankAccount>,

    val legalForm: LegalForm
)

data class UpdateAcRs @JsonCreator constructor(

    var planning: Planning,

    val contract: AwardContract,

    val award: ContractedAward,

    val buyer: OrganizationReferenceBuyer
)