package com.procurement.contracting.model.dto.checkCAN;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@JsonPropertyOrder("canCreateContract")
@Getter
public class CheckCANRS {
    @JsonProperty("canCreateContract")
    @NotNull
    private final Boolean canCreateContract;

    public CheckCANRS(@JsonProperty("canCreateContract") @NotNull Boolean canCreateContract) {
        this.canCreateContract = canCreateContract;
    }
}
