package com.procurement.contracting.model.dto.bpe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto<T> {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("details")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResponseDetailsDto> details;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDto(@JsonProperty("success") final Boolean success,
                       @JsonProperty("details") final List<ResponseDetailsDto> details,
                       @JsonProperty(value = "data") final T data) {
        this.success = success;
        this.details = details;
        this.data = data;
    }

}
