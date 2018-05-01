package com.procurement.contracting.model.dto.old.awardedContract;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

//    @NotNull
//    @JsonProperty("token")
//    @JsonView(View.CreateACView.class)
//    private String token;
//
//    @Valid
//    @NotNull
//    @JsonProperty("statusCAN")
//    @JsonView(View.CreateACView.class)
//    private ContractStatus statusCAN;
//
//    @JsonProperty("statusDetailsCAN")
//    @JsonView(View.CreateACView.class)
//    private ContractStatusDetails statusDetailsCAN;
//
//    @Valid
//    @NotEmpty
//    @JsonProperty("contracts")
//    private ACContractDto contracts;
//
//    public ACDto(@JsonProperty("token") @NotNull final String token,
//                 @JsonProperty("statusCAN") @Valid @NotNull final ContractStatus statusCAN,
//                 @JsonProperty("statusDetailsCAN") @JsonInclude(JsonInclude.Include.ALWAYS) final
//                 ContractStatusDetails statusDetailsCAN,
//                 @JsonProperty("contracts") @NotEmpty @Valid final ACContractDto contracts) {
//        this.token = token;
//        this.statusCAN = statusCAN;
//        this.statusDetailsCAN = statusDetailsCAN;
//        this.contracts = contracts;
//    }
}
