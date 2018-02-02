package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "token",
    "contracts"
})
public class CreateRS {
    @JsonProperty("token")
    @NotNull
    private final String token;

    @JsonProperty("contractRSDto")
    @NotNull
    @Valid
    private final CreateContractRSDto contractRSDto;

    public CreateRS(@JsonProperty("token") @NotNull String token,
                    @JsonProperty("contractRSDto") @NotNull @Valid CreateContractRSDto contractRSDto) {
        this.token = token;
        this.contractRSDto = contractRSDto;
    }
}
