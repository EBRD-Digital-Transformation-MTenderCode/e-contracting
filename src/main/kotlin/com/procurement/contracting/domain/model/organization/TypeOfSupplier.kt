package com.procurement.contracting.domain.model.organization

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class TypeOfSupplier(@JsonValue override val key: String) : EnumElementProvider.Element {
    COMPANY("company"),
    INDIVIDUAL("individual");

    override fun toString(): String = key

    companion object : EnumElementProvider<TypeOfSupplier>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = TypeOfSupplier.orThrow(name)
    }
}