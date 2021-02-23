package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.EnumElementProvider
import com.procurement.contracting.domain.model.ProcurementMethodDetails
import com.procurement.contracting.domain.model.fc.id.FrameworkContractId
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseIdFC
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.getDuplicate
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.infrastructure.handler.v2.converter.rule.notEmptyRule
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess
import com.procurement.contracting.lib.functional.validate
import com.procurement.contracting.domain.model.OperationType as ParentOperationType

class SetStateForContractsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethod,
    val country: String,
    val operationType: OperationType,
    val tender: Tender?,
    val contracts: List<Contract>
) {

    companion object {

        fun tryCreate(
            cpid: String,
            ocid: String,
            pmd: String,
            country: String,
            operationType: String,
            tender: Tender?,
            contracts: List<Contract>?
        ) : Result<SetStateForContractsParams, DataErrors.Validation> {
            val parsedCpid = parseCpid(value = cpid).onFailure { return it }
            val parsedOcid = parseOcid(value = ocid).onFailure { return it }

            val parsedPmd = parseEnum(
                value = pmd,
                allowedEnums = ProcurementMethod.allowedElements,
                attributeName = "pmd",
                target = ProcurementMethod
            ).onFailure { return it }

            val parsedOperationType = parseEnum(
                value = operationType,
                allowedEnums = OperationType.allowedElements,
                attributeName = "operationType",
                target = OperationType
            ).onFailure { return it }

            contracts.validate(notEmptyRule("contracts"))
                .onFailure { return it }

            return SetStateForContractsParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                pmd = parsedPmd,
                country = country,
                operationType = parsedOperationType,
                tender = tender,
                contracts = contracts.orEmpty()
            ).asSuccess()

        }

    }

    data class Tender(
        val lots: List<Lot>
    ) {
        companion object {
            fun tryCreate(lots: List<Lot>): Result<Tender, DataErrors.Validation.UniquenessDataMismatch> {

                val duplicate = lots.getDuplicate { it.id }
                if (duplicate != null)
                    return DataErrors.Validation.UniquenessDataMismatch(name = "lots.id", value = duplicate.id).asFailure()

                return Tender(lots = lots).asSuccess()
            }
        }

        data class Lot(
            val id: String
        )
    }

    class Contract private constructor(
        val id: FrameworkContractId
    ){
        companion object{
            fun tryCreate(id: String): Result<Contract, DataErrors> {
                val id = parseIdFC(id, "contracts.id").onFailure { return it }
                return Contract(id).asSuccess()
            }
        }
    }

    enum class OperationType(val base: ParentOperationType) : EnumElementProvider.Element {

        COMPLETE_SOURCING(ParentOperationType.COMPLETE_SOURCING),
        ISSUING_FRAMEWORK_CONTRACT(ParentOperationType.ISSUING_FRAMEWORK_CONTRACT);

        override val key: String
            get() = base.key

        override val deprecated: Boolean
            get() = base.deprecated

        companion object : EnumElementProvider<OperationType>(info = info())
    }

    enum class ProcurementMethod(val base: ProcurementMethodDetails) : EnumElementProvider.Element {

        CF(ProcurementMethodDetails.CF), TEST_CF(ProcurementMethodDetails.TEST_CF),
        OF(ProcurementMethodDetails.OF), TEST_OF(ProcurementMethodDetails.TEST_OF),
        ;

        override val key: String
            get() = base.key

        override val deprecated: Boolean
            get() = base.deprecated

        companion object : EnumElementProvider<ProcurementMethod>(info = info())
    }
}
