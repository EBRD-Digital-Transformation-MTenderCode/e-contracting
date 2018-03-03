package com.procurement.contracting.model.dto.contractAwardNotice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class ChangeStatusCanRS {

    @JsonProperty("contracts")
    @NotNull
    private final CreateCanContractRSDto contracts;

    public ChangeStatusCanRS(
        @JsonProperty("contracts")
        @NotNull final CreateCanContractRSDto contracts) {

        this.contracts = contracts;
    }
}
