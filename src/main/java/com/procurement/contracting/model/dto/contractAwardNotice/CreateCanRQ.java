package com.procurement.contracting.model.dto.contractAwardNotice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
@JsonPropertyOrder(
        "awards"
)
public class CreateCanRQ {
    @Valid
    @NotEmpty
    @JsonProperty("awards")
    private List<AwardDto> awards;

    public CreateCanRQ(@JsonProperty("awards") final List<AwardDto> awards) {
        this.awards = awards;
    }
}
