package com.procurement.contracting.model.dto.updateCA;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class UpdateContractRQ {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final UpdateContractRQDto contracts;

    public UpdateContractRQ(@JsonProperty("contracts")
                            @Valid
                            @NotNull
                            final UpdateContractRQDto contracts) {
        this.contracts = contracts;
    }
}
