package com.procurement.contracting.infrastructure.dto.v2.request

import com.procurement.contracting.infrastructure.AbstractDTOTestBase
import com.procurement.contracting.infrastructure.handler.v2.model.request.FindContractDocumentIdRequest
import org.junit.jupiter.api.Test

class FindContractDocumentIdRequestTest : AbstractDTOTestBase<FindContractDocumentIdRequest>(FindContractDocumentIdRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/dto/v2/request/request_find_contract_document_id_fully.json")
    }
}
