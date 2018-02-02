package com.procurement.contracting.model.dto.updateContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class UpdateContractRS {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final UpdateContractRSDto contracts;

    public UpdateContractRS(@JsonProperty("contracts")
                            @Valid
                            @NotNull
                            final UpdateContractRSDto contracts) {
        this.contracts = contracts;
    }
}
