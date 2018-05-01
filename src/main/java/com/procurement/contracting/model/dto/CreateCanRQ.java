package com.procurement.contracting.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.procurement.contracting.model.dto.ocds.Award;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CreateCanRQ {
    @Valid
    @NotEmpty
    @JsonProperty("awards")
    private List<Award> awards;

    public CreateCanRQ(@JsonProperty("awards") final List<Award> awards) {
        this.awards = awards;
    }
}
