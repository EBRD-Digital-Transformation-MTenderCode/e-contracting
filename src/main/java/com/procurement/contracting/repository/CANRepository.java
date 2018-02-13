package com.procurement.contracting.repository;

import com.procurement.contracting.model.entity.ContractAwardNoticeEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CANRepository extends CassandraRepository<ContractAwardNoticeEntity, String> {
}
