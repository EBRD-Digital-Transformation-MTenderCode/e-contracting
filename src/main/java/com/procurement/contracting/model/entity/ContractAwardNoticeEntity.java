package com.procurement.contracting.model.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("contract_award_notice")
@Getter
@Setter
public class ContractAwardNoticeEntity {
    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private UUID cpId;
    @PrimaryKeyColumn(name = "can_id", type = PrimaryKeyType.CLUSTERED)
    private UUID canId;
    @PrimaryKeyColumn(name = "award_id")
    private String award_id;
    @PrimaryKeyColumn(value = "owner")
    private String owner;
    @PrimaryKeyColumn(value = "awarded_contract_id")
    private UUID acId;
    @PrimaryKeyColumn(value = "status")
    private String status;
    @PrimaryKeyColumn(value = "status_details")
    private String statusDetails;


    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContractAwardNoticeEntity)) {
            return false;
        }
        final ContractAwardNoticeEntity rhs = (ContractAwardNoticeEntity) other;
        return new EqualsBuilder().append(cpId, rhs.cpId)
                                  .append(canId, rhs.canId)
                                  .append(award_id, rhs.award_id)
                                  .append(owner, rhs.owner)
                                  .append(acId,rhs.acId)
                                  .append(status,rhs.status)
                                  .append(statusDetails,rhs.statusDetails)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(cpId)
                                    .append(canId)
                                    .append(award_id)
                                    .append(owner)
                                    .append(acId)
                                    .append(status)
                                    .append(statusDetails)
                                    .toHashCode();
    }


}
