package com.procurement.contracting.model.dto.createAC;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.ContractItemDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "contracts",
    "lots",
    "items",
    "awards"
})
public class CreateACRQ {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final CreateACContractRQDto contract;

    @JsonProperty("lots")
    @NotEmpty
    @Valid
    private final List<CreateACContractingLotRQDto> lots;

    @JsonProperty("items")
    @NotEmpty
    @Valid
    private final List<ContractItemDto> items;

    @JsonProperty("awards")
    @Valid
    @NotNull
    private final CreateACAwardRQDto award;

    public CreateACRQ(@JsonProperty("contracts") @NotNull @Valid final CreateACContractRQDto contract,
                      @JsonProperty("lots") @NotEmpty @Valid final List<CreateACContractingLotRQDto> lots,
                      @JsonProperty("items") @NotEmpty @Valid final List<ContractItemDto> items,
                      @JsonProperty("awards") @Valid @NotNull final CreateACAwardRQDto award) {
        this.contract = contract;
        this.lots = lots;
        this.items = items;
        this.award = award;
    }
}
