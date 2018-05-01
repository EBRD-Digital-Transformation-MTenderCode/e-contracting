package com.procurement.contracting.repository;

import com.procurement.contracting.model.entity.CANEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CANRepository extends CassandraRepository<CANEntity, String> {

    @Query(value = "select * from contracting_notice where cp_id=?0 and stage=?1")
    List<CANEntity> getByCpIdAndStage(String cpId, String stage);

    @Query(value = "select * from contracting_notice where cp_id=?0 and token_entity=?1 limit 1")
    CANEntity getByCpIdAndToken(String cpId, UUID token);

}
