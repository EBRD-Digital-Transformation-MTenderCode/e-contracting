package com.procurement.contracting.infrastructure.handler

import com.procurement.contracting.infrastructure.api.Action
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.handler.v2.model.CommandDescriptor

interface Handler<R : Any> {
    val version: ApiVersion
    val action: Action

    fun handle(descriptor: CommandDescriptor): R
}
