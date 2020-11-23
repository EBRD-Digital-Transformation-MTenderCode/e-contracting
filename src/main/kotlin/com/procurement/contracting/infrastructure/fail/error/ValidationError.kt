package com.procurement.contracting.infrastructure.fail.error

import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.infrastructure.fail.Fail

sealed class ValidationError(val id: String? = null) : Fail.Error() {

    class ActiveFrameworkContractNotFound(val cpid: Cpid, val ocid: Ocid) : ValidationError() {
        override val code: String = "VR.COM-6.3.1"
        override val description: String
            get() = "Active framework contract by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
    }
}