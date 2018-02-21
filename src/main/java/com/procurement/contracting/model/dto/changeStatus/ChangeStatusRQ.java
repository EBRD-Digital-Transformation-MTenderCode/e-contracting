package com.procurement.contracting.model.dto.changeStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.updateAC.UpdateACContractRQDto;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("contracts")
public class ChangeStatusRQ {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final ChangeStatusContractRQDto contracts;

    public ChangeStatusRQ(@JsonProperty("contracts")
                            @Valid
                            @NotNull
                            final ChangeStatusContractRQDto contracts) {
        this.contracts = contracts;
    }
}
