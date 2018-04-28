package com.procurement.contracting.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("contracting_awarded_contract")
@Getter
@Setter
public class ACEntity {
    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private String cpId;
    @PrimaryKeyColumn(name = "ac_id", type = PrimaryKeyType.PARTITIONED)
    private UUID acId;
    @Column(value = "can_id")
    private UUID canId;
    @Column(value = "owner")
    private String owner;
    @PrimaryKeyColumn(name = "release_date", type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime releaseDate;
    @Column(value = "status")
    private String status;
    @Column(value = "status_details")
    private String statusDetails;
    @Column(value = "json_data")
    private String jsonData;

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ACEntity)) {
            return false;
        }
        final ACEntity rhs = (ACEntity) other;
        return new EqualsBuilder().append(cpId, rhs.cpId)
                                  .append(acId, rhs.acId)
                                  .append(canId, rhs.canId)
                                  .append(owner, rhs.owner)
                                  .append(releaseDate, rhs.releaseDate)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .append(jsonData, rhs.jsonData)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(cpId)
                                    .append(acId)
                                    .append(canId)
                                    .append(owner)
                                    .append(releaseDate)
                                    .append(status)
                                    .append(statusDetails)
                                    .append(jsonData)
                                    .toHashCode();
    }
}
