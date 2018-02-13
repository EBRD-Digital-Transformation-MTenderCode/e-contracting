package com.procurement.contracting.service;

import com.fasterxml.uuid.Generators;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanCanRSDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanContractRSDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanRQ;
import com.procurement.contracting.model.dto.createCAN.CreateCanRS;
import com.procurement.contracting.model.dto.createCAN.Status;
import com.procurement.contracting.model.entity.ContractAwardNoticeEntity;
import com.procurement.contracting.repository.CANRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CANServiseImpl implements CANServise {

    private final CANRepository canRepository;

    public CANServiseImpl(final CANRepository canRepository) {
        this.canRepository = canRepository;
    }

    @Override
    public ResponseDto createCAN(String cpId, String owner, CreateCanRQ contractDto) {

        List<ContractAwardNoticeEntity> canEntities = new ArrayList<>();

        for (int i = 0; i < contractDto.getContractDtos()
                                       .size(); i++) {
            canEntities.add(createCANEntity(UUID.fromString(cpId), contractDto.getContractDtos()
                                                             .get(i)
                                                             .getId().toString(), owner));
        }

        List<CreateCanCanRSDto> canDtos = new ArrayList<>();

        for (int i = 0; i < canEntities.size(); i++) {
            canDtos.add(convertEntityToDto(canEntities.get(i)));
        }

        CreateCanRS createCanRS = new CreateCanRS(canDtos);

        ResponseDto responseDto = new ResponseDto(true, null, createCanRS);

        return responseDto;
    }

    private ContractAwardNoticeEntity createCANEntity(UUID cpId,
                                                      String awardId,
                                                      String owner) {

        ContractAwardNoticeEntity canEntity = new ContractAwardNoticeEntity();
        canEntity.setCpId(cpId);
        canEntity.setCanId(Generators.timeBasedGenerator()
                                     .generate());
        canEntity.setAward_id(awardId);
        canEntity.setOwner(owner);
        canEntity.setStatus(Status.PENDING.toString());
        canEntity.setStatusDetails("contractProject");

        canRepository.save(canEntity);

        return canEntity;
    }

    private CreateCanCanRSDto convertEntityToDto(ContractAwardNoticeEntity contractAwardNoticeEntity) {

        return new CreateCanCanRSDto(contractAwardNoticeEntity.getCanId()
                                                              .toString(),
                                     new CreateCanContractRSDto(contractAwardNoticeEntity.getCanId()
                                                                                         .toString(),
                                                                contractAwardNoticeEntity.getAward_id(),
                                                                Status.fromValue(contractAwardNoticeEntity.getStatus()),
                                                                contractAwardNoticeEntity.getStatusDetails()));
    }
}
