package com.procurement.contracting.domain.model.bid

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class BusinessFunctionType(@JsonValue override val key: String) : EnumElementProvider.Element {

    AUTHORITY("authority"),
    CHAIRMAN("chairman"),
    CONTACT_POINT("contactPoint"),
    PRICE_EVALUATOR("priceEvaluator"),
    PRICE_OPENER("priceOpener"),
    PROCUREMENT_OFFICER("procurementOfficer"),
    TECHNICAL_EVALUATOR("technicalEvaluator"),
    TECHNICAL_OPENER("technicalOpener");

    override fun toString(): String = key

    companion object : EnumElementProvider<BusinessFunctionType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
