package com.procurement.contracting.model.dto.createAC;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "token",
    "statusCAN",
    "statusDetailsCAN",
    "contracts"
})

public class CreateACRS {
    @JsonProperty("token")
    @NotNull
    private final String token;

    @JsonProperty("statusCAN")
    @NotNull
    @Valid
    private final ContractStatus statusCAN;

    @JsonProperty("statusDetailsCAN")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private final ContractStatusDetails statusDetailsCAN;

    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final CreateACContractRSDto contracts;

    public CreateACRS(@JsonProperty("token") @NotNull String token,
                      @JsonProperty("statusCAN")@Valid @NotNull ContractStatus statusCAN,
                      @JsonProperty("statusDetailsCAN")@JsonInclude(JsonInclude.Include.ALWAYS) ContractStatusDetails statusDetailsCAN,
                      @JsonProperty("contracts") @NotNull @Valid CreateACContractRSDto contracts) {
        this.token = token;
        this.statusCAN = statusCAN;
        this.statusDetailsCAN = statusDetailsCAN;
        this.contracts = contracts;
    }
}
