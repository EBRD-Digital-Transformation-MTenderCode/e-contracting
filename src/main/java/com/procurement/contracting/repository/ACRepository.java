package com.procurement.contracting.repository;

import com.procurement.contracting.model.entity.ACEntity;
import com.procurement.contracting.model.entity.CANEntity;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ACRepository extends CassandraRepository<ACEntity, String> {
    @Query(value = "select * from contracting_awarded_contract where cp_id=?0 and ac_id=?1 limit 1")
    ACEntity getByCpIdAndCanId(UUID cpId,UUID acId);

}
