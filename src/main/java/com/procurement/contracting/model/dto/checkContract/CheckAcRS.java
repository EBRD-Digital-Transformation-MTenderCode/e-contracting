package com.procurement.contracting.model.dto.checkContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@JsonPropertyOrder("awardedcontractfailed")
@Getter
public class CheckAcRS {
    @JsonProperty("awardedcontractfailed")
    @NotNull
    private final Boolean awardedcontractfailed;

    public CheckAcRS(@JsonProperty("awardedcontractfailed") @NotNull final Boolean awardedcontractfailed) {
        this.awardedcontractfailed = awardedcontractfailed;
    }
}
