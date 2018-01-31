package com.procurement.contracting.model.dto.createContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRQ {
    @JsonProperty("contracts")
    @NotNull
    @Valid
    private final Contract contract;

    @JsonProperty("lots")
    @NotEmpty
    @Valid
    private final List<Lot> lots;

    @JsonProperty("items")
    @NotEmpty
    @Valid
    private final List<Item> items;

    @JsonProperty("awards")
    @Valid
    @NotNull
    private final Award award;

    public CreateRQ(@JsonProperty("contracts") @NotNull @Valid final Contract contract,
                    @JsonProperty("lots") @NotEmpty @Valid final List<Lot> lots,
                    @JsonProperty("items") @NotEmpty @Valid final List<Item> items,
                    @JsonProperty("awards") @Valid @NotNull final Award award) {
        this.contract = contract;
        this.lots = lots;
        this.items = items;
        this.award = award;
    }
}
