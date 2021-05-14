package com.procurement.contracting.infrastructure.bind.confirmation

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestId

class ConfirmationRequestIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ConfirmationRequestId::class.java, ConfirmationRequestIdSerializer())
        addDeserializer(ConfirmationRequestId::class.java, ConfirmationRequestIdDeserializer())
    }
}
