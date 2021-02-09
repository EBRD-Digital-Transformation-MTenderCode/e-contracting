package com.procurement.contracting.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.fc.FrameworkContract
import com.procurement.contracting.domain.util.extension.asString

data class AddSupplierReferencesInFCResponse(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: String,
    @get:JsonProperty("isFrameworkOrDynamic") @param:JsonProperty("isFrameworkOrDynamic") val isFrameworkOrDynamic: Boolean,
    @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>
) {
    data class Supplier(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("name") @param:JsonProperty("name") val name: String
    )

    companion object {
        fun fromDomain(frameworkContract: FrameworkContract): AddSupplierReferencesInFCResponse =
            AddSupplierReferencesInFCResponse(
                id = frameworkContract.id.underlying,
                status = frameworkContract.status.key,
                statusDetails = frameworkContract.statusDetails.key,
                date = frameworkContract.date.asString(),
                isFrameworkOrDynamic = frameworkContract.isFrameworkOrDynamic,
                suppliers = frameworkContract.suppliers.map { it.convert() }
            )

        private fun FrameworkContract.Supplier.convert(): Supplier =
            Supplier(id = id, name = name)
    }
}
