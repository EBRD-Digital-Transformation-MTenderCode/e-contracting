package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.AddGeneratedDocumentToContractResponse
import org.junit.jupiter.api.Test

class AddGeneratedDocumentToContractResponseTest : AbstractDTOTestBase<AddGeneratedDocumentToContractResponse>(AddGeneratedDocumentToContractResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/response_add_generated_document_to_contract_fully.json")
    }
}
