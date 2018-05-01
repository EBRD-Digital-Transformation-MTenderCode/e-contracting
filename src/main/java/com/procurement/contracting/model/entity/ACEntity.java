package com.procurement.contracting.model.entity;

import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("contracting_contract")
@Getter
@Setter
public class ACEntity {
    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    private String cpId;

    @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
    private String stage;

    @PrimaryKeyColumn(name = "token_entity", type = PrimaryKeyType.CLUSTERED)
    private UUID token;

    @Column(value = "owner")
    private String owner;

    @Column("created_date")
    private Date createdDate;

    @Column(value = "can_id")
    private UUID canId;

    @Column(value = "status")
    private String status;

    @Column(value = "status_details")
    private String statusDetails;

    @Column(value = "json_data")
    private String jsonData;
}
