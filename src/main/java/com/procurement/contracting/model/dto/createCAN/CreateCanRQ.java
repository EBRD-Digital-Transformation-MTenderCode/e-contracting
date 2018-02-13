package com.procurement.contracting.model.dto.createCAN;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
@JsonPropertyOrder(
    "awards"
)
public class CreateCanRQ {
    @JsonProperty("awards")
    @Valid
    @NotEmpty
    private List<CreateCanContractRQDto> contractDtos;

    public CreateCanRQ(@JsonProperty("awards") final List<CreateCanContractRQDto> contractDtos) {
        this.contractDtos = contractDtos;
    }
}
