package com.procurement.contracting.model.dto.updateContract;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.contracting.databind.LocalDateTimeDeserializer;
import com.procurement.contracting.databind.LocalDateTimeSerializer;
import com.procurement.contracting.model.dto.ContractValueDto;
import java.net.URI;
import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "id",
    "source",
    "date",
    "value",
    "payer",
    "payee",
    "uri"
})
public class UpdateContractTransactionRQDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("source")
    @NotNull
    private final URI source;

    @JsonProperty("date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull
    private final LocalDateTime date;

    @JsonProperty("value")
    @NotNull
    @Valid
    private final ContractValueDto value;

    @JsonProperty("payer")
    @NotNull
    @Valid
    private final UpdateContractOrganizationRQDto payer;

    @JsonProperty("payee")
    @NotNull
    @Valid
    private final UpdateContractOrganizationRQDto payee;

    @JsonProperty("uri")
    @NotNull
    private final URI uri;

    public UpdateContractTransactionRQDto(@JsonProperty("id")
                                          @NotNull final String id,
                                          @JsonProperty("source")
                                          @NotNull final URI source,
                                          @JsonProperty("date")
                                          @NotNull
                                          @JsonDeserialize(using = LocalDateTimeDeserializer.class) final
                                          LocalDateTime date,
                                          @JsonProperty("value") @NotNull @Valid final ContractValueDto value,
                                          @JsonProperty("payer") @NotNull @Valid final
                                          UpdateContractOrganizationRQDto payer,
                                          @JsonProperty("payee") @NotNull @Valid final
                                          UpdateContractOrganizationRQDto payee,
                                          @JsonProperty("uri") @NotNull final URI uri) {
        this.id = id;
        this.source = source;
        this.date = date;
        this.value = value;
        this.payer = payer;
        this.payee = payee;
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                                    .append(source)
                                    .append(date)
                                    .append(value)
                                    .append(payer)
                                    .append(payee)
                                    .append(uri)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UpdateContractTransactionRQDto)) {
            return false;
        }
        final UpdateContractTransactionRQDto rhs = (UpdateContractTransactionRQDto) other;
        return new EqualsBuilder().append(id, rhs.id)
                                  .append(source, rhs.source)
                                  .append(date, rhs.date)
                                  .append(value, rhs.value)
                                  .append(payer, rhs.payer)
                                  .append(payee, rhs.payee)
                                  .append(uri, rhs.uri)
                                  .isEquals();
    }
}
