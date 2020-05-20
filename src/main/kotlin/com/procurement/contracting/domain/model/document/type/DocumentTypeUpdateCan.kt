package com.procurement.contracting.domain.model.document.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class DocumentTypeUpdateCan(@JsonValue override val key: String) : EnumElementProvider.Key {
    EVALUATION_REPORT("evaluationReports");

    override fun toString(): String = key

    companion object : EnumElementProvider<DocumentTypeUpdateCan>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DocumentTypeUpdateCan.orThrow(name)
    }
}