package com.procurement.contracting.model.dto.contractAwardNotice;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateCanRSDto {

    @NotNull
    @JsonProperty("token")
    private final String token;

    @NotNull
    @JsonProperty("contract")
    private final CreateCanContractRSDto contract;

    public CreateCanRSDto(@JsonProperty("token") final String token,
                          @JsonProperty("contract") final CreateCanContractRSDto contract) {
        this.token = token;
        this.contract = contract;
    }
}
