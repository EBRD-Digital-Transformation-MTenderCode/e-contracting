package com.procurement.contracting.model.dto.templates

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfirmationRequestTemplate @JsonCreator constructor(

        var id: String?,

        var type: String?,

        var title: String?,

        var description: String?,

        var relatesTo: String?,

        var requestTitle: String?,

        val source: String?,

        var requestDescription: String?
)