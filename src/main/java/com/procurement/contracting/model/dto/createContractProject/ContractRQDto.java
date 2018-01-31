package com.procurement.contracting.model.dto.createContractProject;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ContractRQDto {

    @JsonProperty("awardId")
    @NotNull
    private final String awardId;

    public ContractRQDto(final String awardId) {
        this.awardId = awardId;
    }
}
