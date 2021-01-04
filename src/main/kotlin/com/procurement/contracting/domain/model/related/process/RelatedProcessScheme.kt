package com.procurement.contracting.domain.model.related.process

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class RelatedProcessScheme(@JsonValue override val key: String) : EnumElementProvider.Element {
    OCID("ocid");

    override fun toString(): String = key

    companion object : EnumElementProvider<RelatedProcessScheme>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = RelatedProcessScheme.orThrow(name)
    }
}