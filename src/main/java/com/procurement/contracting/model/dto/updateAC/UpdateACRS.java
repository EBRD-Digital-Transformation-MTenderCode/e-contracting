package com.procurement.contracting.model.dto.updateAC;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class UpdateACRS {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final UpdateACContractRSDto contracts;

    public UpdateACRS(@JsonProperty("contracts")
                            @Valid
                            @NotNull
                            final UpdateACContractRSDto contracts) {
        this.contracts = contracts;
    }
}
