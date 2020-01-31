package com.procurement.contracting.config

import com.procurement.contracting.infrastructure.web.dto.ApiVersion

object GlobalProperties {
    const val serviceId = "9"

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }
}
