package com.procurement.contracting.infrastructure.bind.token

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.contracting.domain.model.Token

class TokenModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Token::class.java, TokenSerializer())
        addDeserializer(Token::class.java, TokenDeserializer())
    }
}
