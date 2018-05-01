package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.contracting.model.dto.ocds.Contract;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateContractRS {

    @NotEmpty
    @Valid
    @JsonProperty("cans")
    private final List<Can> cans;

    @NotEmpty
    @Valid
    @JsonProperty("contracts")
    private final List<Contract> contracts;

    public CreateContractRS(@JsonProperty("cans") final List<Can> cans,
                            @JsonProperty("contracts") final List<Contract> contracts) {
        this.cans = cans;
        this.contracts = contracts;
    }
}
