package com.procurement.contracting.model.dto.createCAN;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@JsonPropertyOrder("CANs")
public class CreateCanRS {

    @JsonProperty("CANs")
    private final List<CreateCanCanRSDto> contracts;

    public CreateCanRS(@JsonProperty("CANs")
                       @NotNull final List<CreateCanCanRSDto> contracts) {
        this.contracts = contracts;
    }
}
