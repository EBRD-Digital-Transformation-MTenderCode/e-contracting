package com.procurement.contracting.model.dto.old.awardedContract;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "contracts",
        "lots",
        "items",
        "awards"
})
public class CreateContractRQ {
//
//    @NotEmpty
//    @Valid
//    @JsonProperty("lots")
//    private final List<Lot> lots;
//
//    @NotEmpty
//    @Valid
//    @JsonProperty("items")
//    private final List<Item> items;
//
//    @Valid
//    @NotNull
//    @JsonProperty("awards")
//    private final List<Award> award;
//
//    public CreateContractRQ(@JsonProperty("lots") final List<Lot> lots,
//                            @JsonProperty("items") final List<Item> items,
//                            @JsonProperty("awards") final List<Award> award) {
//        this.lots = lots;
//        this.items = items;
//        this.award = award;
//    }
}
