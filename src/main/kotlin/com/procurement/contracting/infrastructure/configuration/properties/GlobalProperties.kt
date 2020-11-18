package com.procurement.contracting.infrastructure.configuration.properties

import com.procurement.contracting.infrastructure.api.v1.ApiVersion

object GlobalProperties {
    const val serviceId = "9"

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }
}
