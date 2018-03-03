package com.procurement.contracting.model.dto.checkContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@JsonPropertyOrder("canCreateContract")
@Getter
public class CheckCanRS {
    @JsonProperty("canCreateContract")
    @NotNull
    private final Boolean canCreateContract;

    public CheckCanRS(@JsonProperty("canCreateContract") @NotNull final Boolean canCreateContract) {
        this.canCreateContract = canCreateContract;
    }
}
