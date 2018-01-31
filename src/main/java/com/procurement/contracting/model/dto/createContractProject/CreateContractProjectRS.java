package com.procurement.contracting.model.dto.createContractProject;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateContractProjectRS {
    @JsonProperty("token")
    private final String token;

    @JsonProperty("contracts")
    private final List<ContractRSDto> contracts;

    public CreateContractProjectRS(@JsonProperty("token")
                                   @NotNull final String token,
                                   @JsonProperty("contracts")
                                   @NotNull final List<ContractRSDto> contracts) {
        this.token = token;
        this.contracts = contracts;
    }
}
