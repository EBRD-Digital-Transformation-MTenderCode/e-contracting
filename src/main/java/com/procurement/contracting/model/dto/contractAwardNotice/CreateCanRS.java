package com.procurement.contracting.model.dto.contractAwardNotice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class CreateCanRS {

    @JsonProperty("cans")
    private final List<CreateCanRSDto> cans;

    public CreateCanRS(@JsonProperty("cans") final List<CreateCanRSDto> cans) {
        this.cans = cans;
    }
}
