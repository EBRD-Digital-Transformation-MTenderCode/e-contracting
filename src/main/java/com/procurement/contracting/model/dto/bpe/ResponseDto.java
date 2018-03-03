package com.procurement.contracting.model.dto.bpe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto<T> {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("responseDetail")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ResponseDetailsDto> responseDetail;

    @JsonProperty("jsonData")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDto(@JsonProperty("success") final Boolean success,
                       @JsonProperty("responseDetail") final List<ResponseDetailsDto> responseDetail,
                       @JsonProperty(value = "jsonData") final T data) {
        this.success = success;
        this.responseDetail = responseDetail;
        this.data = data;
    }

    public void setError(final String message) {
        final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
            "code",
            message
        );
        final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
        details.add(responseDetailsDto);
        this.success = false;
        this.responseDetail = details;
    }

    @Getter
    public static class ResponseDetailsDto {
        @JsonProperty("code")
        private final String code;
        @JsonProperty("message")
        private final String message;

        public ResponseDetailsDto(@JsonProperty("code") final String code,
                                  @JsonProperty("message") final String message) {
            this.code = code;
            this.message = message;
        }
    }
}
