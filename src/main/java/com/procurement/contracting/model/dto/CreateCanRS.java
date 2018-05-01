package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateCanRS {

    @JsonProperty("cans")
    private final List<Can> cans;

    public CreateCanRS(@JsonProperty("cans") final List<Can> cans) {
        this.cans = cans;
    }
}
