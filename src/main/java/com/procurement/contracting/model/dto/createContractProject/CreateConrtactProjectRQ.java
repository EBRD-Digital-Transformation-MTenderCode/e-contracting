package com.procurement.contracting.model.dto.createContractProject;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CreateConrtactProjectRQ {
    @JsonProperty("contracts")
    @NotEmpty
    private List<ContractRQDto> contractDtos;

    public CreateConrtactProjectRQ(final List<ContractRQDto> contractDtos) {
        this.contractDtos = contractDtos;
    }
}
