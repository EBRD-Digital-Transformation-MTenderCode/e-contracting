package com.procurement.contracting.model.dto.changeStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class ChangeStatusRS {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final ChangeStatusContractRSDto contracts;

    public ChangeStatusRS(@JsonProperty("contracts")
                          @Valid
                          @NotNull final ChangeStatusContractRSDto contracts) {
        this.contracts = contracts;
    }
}
