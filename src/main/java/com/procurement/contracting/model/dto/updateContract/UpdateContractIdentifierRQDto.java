
package com.procurement.contracting.model.dto.updateContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "scheme",
    "id",
    "legalName",
    "uri"
})
public class UpdateContractIdentifierRQDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("scheme")
    @NotNull
    private final String scheme;

    @JsonProperty("legalName")
    @NotNull
    private final String legalName;

    @JsonProperty("uri")
    @NotNull
    private final URI uri;

    public UpdateContractIdentifierRQDto(@JsonProperty("id") @NotNull final String id,
                                         @JsonProperty("scheme") @NotNull final String scheme,
                                         @JsonProperty("legalName") @NotNull final String legalName,
                                         @JsonProperty("uri") @NotNull final URI uri) {
        this.id = id;
        this.scheme = scheme;
        this.legalName = legalName;
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(scheme)
                                    .append(id)
                                    .append(legalName)
                                    .append(uri)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateContractIdentifierRQDto)) {
            return false;
        }
        final UpdateContractIdentifierRQDto rhs = (UpdateContractIdentifierRQDto) other;
        return new EqualsBuilder().append(scheme, rhs.scheme)
                                  .append(id, rhs.id)
                                  .append(legalName, rhs.legalName)
                                  .append(uri, rhs.uri)
                                  .isEquals();
    }
}
