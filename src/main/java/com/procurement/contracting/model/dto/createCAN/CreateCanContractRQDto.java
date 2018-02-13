package com.procurement.contracting.model.dto.createCAN;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("id")
public class CreateCanContractRQDto {

    @JsonProperty("id")
    @NotNull
    private final String id;

    public CreateCanContractRQDto(@JsonProperty("id") final String id) {
        this.id = id;
    }
}
