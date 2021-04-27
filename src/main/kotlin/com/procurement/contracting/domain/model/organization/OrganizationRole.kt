package com.procurement.contracting.domain.model.organization

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.contracting.domain.model.EnumElementProvider

enum class OrganizationRole(@JsonValue override val key: String) : EnumElementProvider.Element {
    BUYER("buyer"),
    SUPPLIER("supplier"),
    PROCURING_ENTITY("procuringEntity"),
    INVITED_CANDIDATE("invitedCandidate")
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<OrganizationRole>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = OrganizationRole.orThrow(name)
    }
}