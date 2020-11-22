package com.procurement.contracting.domain.model.contract.id

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.nowDefaultUTC
import com.procurement.contracting.domain.util.extension.toMilliseconds
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import java.util.*

class ContractId private constructor(val underlying: String) {

    companion object {
        val pattern: String
            get() = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-AC-[0-9]{13}\$"

        private val regex = pattern.toRegex()

        @JvmStatic
        @JsonCreator
        fun orNull(value: String): ContractId? = if (value.matches(regex)) ContractId(underlying = value) else null

        fun generate(cpid: Cpid): ContractId =
            ContractId(cpid.underlying + "-AC-" + (nowDefaultUTC().toMilliseconds() + Random().nextInt()))
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ContractId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}

fun Ocid.asContractId(): ContractId = ContractId.orNull(underlying)
    ?: throw ErrorException(
        error = ErrorType.CONTRACT_ID,
        message = "The ocid '${underlying}' do not represent a valid contract id."
    )
