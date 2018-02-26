package com.procurement.contracting.model.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("contracting_contract_award_notice")
@Getter
@Setter
public class CANEntity {
    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private UUID cpId;
    @PrimaryKeyColumn(name = "can_id", type = PrimaryKeyType.CLUSTERED)
    private UUID canId;
    @Column(value = "awardId")
    private String awardId;
    @Column(value = "owner")
    private String owner;
    @Column(value = "ac_id")
    private UUID acId;
    @Column(value = "status")
    private String status;
    @Column(value = "status_details")
    private String statusDetails;

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CANEntity)) {
            return false;
        }
        final CANEntity rhs = (CANEntity) other;
        return new EqualsBuilder().append(cpId, rhs.cpId)
                                  .append(canId, rhs.canId)
                                  .append(awardId, rhs.awardId)
                                  .append(owner, rhs.owner)
                                  .append(acId, rhs.acId)
                                  .append(status, rhs.status)
                                  .append(statusDetails, rhs.statusDetails)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(cpId)
                                    .append(canId)
                                    .append(awardId)
                                    .append(owner)
                                    .append(acId)
                                    .append(status)
                                    .append(statusDetails)
                                    .toHashCode();
    }
}
