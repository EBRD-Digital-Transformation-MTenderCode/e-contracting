package com.procurement.contracting.infrastructure.dto.v2.response

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.response.FindContractDocumentIdResponse
import org.junit.jupiter.api.Test

class FindContractDocumentIdResponseTest : AbstractDTOTestBase<FindContractDocumentIdResponse>(FindContractDocumentIdResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/response/response_find_contract_document_id_fully.json")
    }
}
