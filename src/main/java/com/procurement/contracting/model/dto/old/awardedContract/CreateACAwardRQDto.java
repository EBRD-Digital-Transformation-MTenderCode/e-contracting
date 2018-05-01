package com.procurement.contracting.model.dto.old.awardedContract;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
        "id",
        "date",
        "status",
        "description",
        "relatedLots",
        "value",
        "suppliers"
})
public class CreateACAwardRQDto {
//    @JsonProperty("id")
//    @NotNull
//    private final String id;
//
//    @JsonProperty("date")
//    @NotNull
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    private final LocalDateTime date;
//
//    @JsonProperty("status")
//    @NotNull
//    private final Status status;
//
//    @JsonProperty("description")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private final String description;
//
//    @JsonProperty("relatedLots")
//    @NotEmpty
//    private final List<String> relatedLots;
//
//    @JsonProperty("value")
//    @Valid
//    @NotNull
//    private final ContractValueDto value;
//
//    @JsonProperty("suppliers")
//    @NotEmpty
//    @JsonDeserialize(as = LinkedHashSet.class)
//    @Valid
//    private final Set<CreateACOrganizationReferenceRQDto> suppliers;
//
//    public CreateACAwardRQDto(@JsonProperty("id") @NotNull final String id,
//                              @JsonProperty("date") @NotNull @JsonDeserialize(using = LocalDateTimeDeserializer
//                                  .class) final LocalDateTime date,
//                              @JsonProperty("status") @NotNull final Status status,
//                              @JsonProperty("description") @JsonInclude(JsonInclude.Include.NON_NULL) final String
//                                  description,
//                              @JsonProperty("relatedLots") @NotEmpty final List<String> relatedLots,
//                              @JsonProperty("value") @Valid @NotNull final ContractValueDto value,
//                              @JsonProperty("suppliers") @NotEmpty @Valid final
//                              Set<CreateACOrganizationReferenceRQDto> suppliers) {
//        this.id = id;
//        this.date = date;
//        this.status = status;
//        this.description = description;
//        this.relatedLots = relatedLots;
//        this.value = value;
//        this.suppliers = suppliers;
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(id)
//                                    .append(description)
//                                    .append(status)
//                                    .append(date)
//                                    .append(value)
//                                    .append(suppliers)
//                                    .append(relatedLots)
//                                    .toHashCode();
//    }
//
//    @Override
//    public boolean equals(final Object other) {
//        if (other == this) {
//            return true;
//        }
//        if (!(other instanceof CreateACAwardRQDto)) {
//            return false;
//        }
//        final CreateACAwardRQDto rhs = (CreateACAwardRQDto) other;
//        return new EqualsBuilder().append(id, rhs.id)
//                                  .append(description, rhs.description)
//                                  .append(status, rhs.status)
//                                  .append(date, rhs.date)
//                                  .append(value, rhs.value)
//                                  .append(suppliers, rhs.suppliers)
//                                  .append(relatedLots, rhs.relatedLots)
//                                  .isEquals();
//    }
//
//    public enum Status {
//        PENDING("pending"),
//        ACTIVE("active"),
//        CANCELLED("cancelled"),
//        UNSUCCESSFUL("unsuccessful");
//
//        private static final Map<String, Status> CONSTANTS = new HashMap<>();
//
//        static {
//            for (final Status c : values()) {
//                CONSTANTS.put(c.value, c);
//            }
//        }
//
//        private final String value;
//
//        Status(final String value) {
//            this.value = value;
//        }
//
//        @JsonCreator
//        public static Status fromValue(final String value) {
//            final Status constant = CONSTANTS.get(value);
//            if (constant == null) {
//                throw new IllegalArgumentException(value);
//            }
//            return constant;
//        }
//
//        @Override
//        public String toString() {
//            return this.value;
//        }
//
//        @JsonValue
//        public String value() {
//            return this.value;
//        }
//
//    }
}
