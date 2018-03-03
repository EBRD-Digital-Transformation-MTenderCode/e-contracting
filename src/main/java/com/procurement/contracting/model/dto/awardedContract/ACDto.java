package com.procurement.contracting.model.dto.awardedContract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.procurement.contracting.jsonview.View;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
    "token",
    "statusCAN",
    "statusDetailsCAN",
    "contracts"
})

public class ACDto {
    @JsonProperty("token")
    @NotNull
    @JsonView(View.CreateACView.class)
    private String token;

    @JsonProperty("statusCAN")
    @NotNull
    @Valid
    @JsonView(View.CreateACView.class)
    private ContractStatus statusCAN;

    @JsonProperty("statusDetailsCAN")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonView(View.CreateACView.class)
    private ContractStatusDetails statusDetailsCAN;

    @JsonProperty("contracts")
    @NotEmpty
    @Valid
    private ACContractDto contracts;

    public ACDto(@JsonProperty("token") @NotNull final String token,
                 @JsonProperty("statusCAN") @Valid @NotNull final ContractStatus statusCAN,
                 @JsonProperty("statusDetailsCAN") @JsonInclude(JsonInclude.Include.ALWAYS) final
                 ContractStatusDetails statusDetailsCAN,
                 @JsonProperty("contracts") @NotEmpty @Valid final ACContractDto contracts) {
        this.token = token;
        this.statusCAN = statusCAN;
        this.statusDetailsCAN = statusDetailsCAN;
        this.contracts = contracts;
    }
}
