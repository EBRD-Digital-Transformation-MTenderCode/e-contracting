package com.procurement.contracting.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.contracting.infrastructure.api.ApiVersion
import com.procurement.contracting.infrastructure.api.v2.ApiResponseV2
import com.procurement.contracting.infrastructure.extension.tryGetAttribute
import com.procurement.contracting.infrastructure.fail.Fail
import com.procurement.contracting.infrastructure.fail.error.BadRequest
import com.procurement.contracting.infrastructure.handler.Handler
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.flatMap
import com.procurement.contracting.utils.tryToObject

abstract class AbstractHandlerV2<R : Any> : Handler<ApiResponseV2> {

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    inline fun <reified T : Any> JsonNode.params() = params(T::class.java)

    fun <T : Any> JsonNode.params(target: Class<T>): Result<T, Fail.Error> {
        val name = "params"
        return tryGetAttribute(name)
            .flatMap {
                when (val result = it.tryToObject(target)) {
                    is Result.Success -> result
                    is Result.Failure -> BadRequest("Error parsing '$name'", result.reason.exception)
                        .asFailure<T, Fail.Error>()
                }
            }
    }
}
