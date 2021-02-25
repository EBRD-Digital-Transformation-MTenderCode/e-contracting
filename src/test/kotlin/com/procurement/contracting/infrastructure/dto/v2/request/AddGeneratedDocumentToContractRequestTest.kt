package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.AddGeneratedDocumentToContractRequest
import org.junit.jupiter.api.Test

class AddGeneratedDocumentToContractRequestTest : AbstractDTOTestBase<AddGeneratedDocumentToContractRequest>(AddGeneratedDocumentToContractRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_add_generated_document_to_contract_fully.json")
    }
}
