package com.procurement.contracting.infrastructure.bind.owner

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.Owner

class OwnerModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Owner::class.java, OwnerSerializer())
        addDeserializer(Owner::class.java, OwnerDeserializer())
    }
}
