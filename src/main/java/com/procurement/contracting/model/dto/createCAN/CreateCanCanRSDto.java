package com.procurement.contracting.model.dto.createCAN;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "token",
    "contracts"
})
public class CreateCanCanRSDto {
    @JsonProperty("token")
    @NotNull
    private final String token;

    @JsonProperty("contracts")
    @NotNull
    private final CreateCanContractRSDto contracts;

    public CreateCanCanRSDto(@JsonProperty("token")
                             @NotNull final String token,
                             @JsonProperty("contracts")
                             @NotNull final CreateCanContractRSDto contracts) {
        this.token = token;
        this.contracts = contracts;
    }
}
