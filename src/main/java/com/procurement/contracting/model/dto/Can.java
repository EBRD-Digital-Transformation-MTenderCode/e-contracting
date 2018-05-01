package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.contracting.model.dto.ocds.Contract;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class Can {

    @NotNull
    @JsonProperty("token")
    private final String token;

    @NotNull
    @JsonProperty("contract")
    private final Contract contract;

    public Can(@JsonProperty("token") final String token,
               @JsonProperty("contract") final Contract contract) {
        this.token = token;
        this.contract = contract;
    }
}
