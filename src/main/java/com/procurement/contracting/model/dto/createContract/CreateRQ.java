package com.procurement.contracting.model.dto.createContract;

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
public class CreateRQ {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final CreateContractRQDto contract;

    @JsonProperty("lots")
    @NotEmpty
    @Valid
    private final List<CreateContractingLotRQDto> lots;

    @JsonProperty("items")
    @NotEmpty
    @Valid
    private final List<ContractItemDto> items;

    @JsonProperty("awards")
    @Valid
    @NotNull
    private final CreateContractAwardRQDto award;

    public CreateRQ(@JsonProperty("contracts") @NotNull @Valid final CreateContractRQDto contract,
                    @JsonProperty("lots") @NotEmpty @Valid final List<CreateContractingLotRQDto> lots,
                    @JsonProperty("items") @NotEmpty @Valid final List<ContractItemDto> items,
                    @JsonProperty("awards") @Valid @NotNull final CreateContractAwardRQDto award) {
        this.contract = contract;
        this.lots = lots;
        this.items = items;
        this.award = award;
    }
}
