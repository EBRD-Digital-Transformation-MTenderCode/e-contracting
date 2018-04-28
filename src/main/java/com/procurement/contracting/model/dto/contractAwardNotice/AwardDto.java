package com.procurement.contracting.model.dto.contractAwardNotice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("id")
public class AwardDto {

    @JsonProperty("id")
    @NotNull
    private final String id;

    public AwardDto(@JsonProperty("id") final String id) {
        this.id = id;
    }
}
