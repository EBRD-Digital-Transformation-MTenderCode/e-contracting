package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.procurement.contracting.model.dto.ocds.Award;
import com.procurement.contracting.model.dto.ocds.Item;
import com.procurement.contracting.model.dto.ocds.Lot;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "contracts",
        "lots",
        "items",
        "awards"
})
public class CreateContractRQ {

    @Valid
    @NotEmpty
    @JsonProperty("lots")
    private final List<Lot> lots;

    @Valid
    @JsonProperty("items")
    private final List<Item> items;

    @Valid
    @NotEmpty
    @JsonProperty("awards")
    private final List<Award> awards;

    public CreateContractRQ(@JsonProperty("lots") final List<Lot> lots,
                            @JsonProperty("items") final List<Item> items,
                            @JsonProperty("awards") final List<Award> awards) {
        this.lots = lots;
        this.items = items;
        this.awards = awards;
    }
}
