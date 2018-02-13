
package com.procurement.contracting.model.dto.updateCA;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "name",
    "email",
    "telephone",
    "faxNumber",
    "url"
})
public class UpdateContactPointRQDto {
    @JsonProperty("name")
    @NotNull
    private final String name;

    @JsonProperty("email")
    @NotNull
    private final String email;

    @JsonProperty("telephone")
    @NotNull
    private final String telephone;

    @JsonProperty("faxNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String faxNumber;

    @JsonProperty("url")
    @NotNull
    private final URI url;

    public UpdateContactPointRQDto(@JsonProperty("name") @NotNull final String name,
                                   @JsonProperty("email") @NotNull final String email,
                                   @JsonProperty("telephone") @NotNull final String telephone,
                                   @JsonProperty("faxNumber")
                                   @JsonInclude(JsonInclude.Include.NON_NULL) final String faxNumber,
                                   @JsonProperty("url") @NotNull final URI url) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.faxNumber = faxNumber;
        this.url = url;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name)
                                    .append(email)
                                    .append(telephone)
                                    .append(faxNumber)
                                    .append(url)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateContactPointRQDto)) {
            return false;
        }
        final UpdateContactPointRQDto rhs = (UpdateContactPointRQDto) other;
        return new EqualsBuilder().append(name, rhs.name)
                                  .append(email, rhs.email)
                                  .append(telephone, rhs.telephone)
                                  .append(faxNumber, rhs.faxNumber)
                                  .append(url, rhs.url)
                                  .isEquals();
    }
}
