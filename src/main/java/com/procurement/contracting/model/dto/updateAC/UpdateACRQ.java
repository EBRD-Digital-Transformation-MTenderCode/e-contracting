package com.procurement.contracting.model.dto.updateAC;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class UpdateACRQ {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final UpdateACContractRQDto contracts;

    public UpdateACRQ(@JsonProperty("contracts")
                      @Valid
                      @NotNull final UpdateACContractRQDto contracts) {
        this.contracts = contracts;
    }
}
