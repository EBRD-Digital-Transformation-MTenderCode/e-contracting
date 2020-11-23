package com.procurement.contracting.infrastructure.fail.error

import com.procurement.contracting.infrastructure.fail.Fail

sealed class RulesError(override val description: String) : Fail.Error() {

    class NotFound(description: String) : RulesError(description) {
        override val code: String = "VR-17"
    }
}
