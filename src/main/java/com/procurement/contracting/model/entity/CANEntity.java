package com.procurement.contracting.model.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("contracting_contract_notice")
@Getter
@Setter
public class CANEntity {

    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private String cpId;

    @PrimaryKeyColumn(name = "token_entity", type = PrimaryKeyType.CLUSTERED)
    private UUID token;

    @PrimaryKeyColumn(name = "award_id", type = PrimaryKeyType.CLUSTERED)
    private String awardId;

    @Column(value = "owner")
    private String owner;

    @Column(value = "ac_id")
    private UUID acId;

    @Column(value = "status")
    private String status;

    @Column(value = "status_details")
    private String statusDetails;
}
