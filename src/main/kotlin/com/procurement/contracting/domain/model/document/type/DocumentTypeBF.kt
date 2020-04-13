package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class DocumentTypeBF(@JsonValue override val key: String) : EnumElementProvider.Key {
    REGULATORY_DOCUMENT("regulatoryDocument");

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentTypeBF>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentTypeBF.orThrow(name)
    }
}

