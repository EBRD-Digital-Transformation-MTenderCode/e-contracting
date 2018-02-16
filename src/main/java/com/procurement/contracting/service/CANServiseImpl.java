package com.procurement.contracting.service;

import com.fasterxml.uuid.Generators;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.checkCAN.CheckCANRS;
import com.procurement.contracting.model.dto.createCAN.CreateCanCanRSDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanContractRSDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanRQ;
import com.procurement.contracting.model.dto.createCAN.CreateCanRS;
import com.procurement.contracting.model.entity.CANEntity;
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

        List<CANEntity> canEntities = createCANEntities(cpId, owner, contractDto);

        List<CreateCanCanRSDto> canDtos = convertCANEntitiesListToDtoList(canEntities);

        CreateCanRS createCanRS = new CreateCanRS(canDtos);

        ResponseDto responseDto = new ResponseDto(true, null, createCanRS);

        return responseDto;
    }

    @Override
    public ResponseDto checkCAN(String cpId, String token, String idPlatform) {

        CANEntity entity = canRepository.getByCpIdAndCanId(UUID.fromString(cpId), UUID.fromString(token));
        if (entity != null) {
            if(idPlatform.equals(entity.getOwner())){
                if(ContractStatus.fromValue(entity.getStatus())==ContractStatus.PENDING){
                    return new ResponseDto(true, null, new CheckCANRS(true));
                }else {
                    return new ResponseDto(true, null, new CheckCANRS(false));
                }
            }else {
                final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                    "code",
                    "invalid owner"
                );
                final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                details.add(responseDetailsDto);
                return new ResponseDto(false,details,null);
            }

        } else {
            final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                "code",
                "CAN not found"
            );
            final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
            details.add(responseDetailsDto);

            return new ResponseDto(false,details,null);
        }


    }

    private CANEntity createAndSaveCANEntity(UUID cpId,
                                             String awardId,
                                             String owner) {

        CANEntity canEntity = new CANEntity();
        canEntity.setCpId(cpId);
        canEntity.setCanId(Generators.timeBasedGenerator()
                                     .generate());
        canEntity.setAward_id(awardId);
        canEntity.setOwner(owner);
        canEntity.setStatus(ContractStatus.PENDING.toString());
        canEntity.setStatusDetails("contractProject");

        canRepository.save(canEntity);

        return canEntity;
    }

    private CreateCanCanRSDto convertEntityToDto(CANEntity contractAwardNoticeEntity) {

        return new CreateCanCanRSDto(contractAwardNoticeEntity.getCanId()
                                                              .toString(),
                                     new CreateCanContractRSDto(contractAwardNoticeEntity.getCanId()
                                                                                         .toString(),
                                                                contractAwardNoticeEntity.getAward_id(),
                                                                ContractStatus.fromValue(contractAwardNoticeEntity.getStatus()),
                                                                ContractStatusDetails.fromValue(contractAwardNoticeEntity.getStatusDetails())));
    }

    private List<CANEntity> createCANEntities(String cpId, String owner, CreateCanRQ contractDto) {
        List<CANEntity> canEntities = new ArrayList<>();

        for (int i = 0; i < contractDto.getContractDtos()
                                       .size(); i++) {
            canEntities.add(createAndSaveCANEntity(UUID.fromString(cpId), contractDto.getContractDtos()
                                                                                     .get(i)
                                                                                     .getId(), owner));
        }
        return canEntities;
    }

    private List<CreateCanCanRSDto> convertCANEntitiesListToDtoList(List<CANEntity> canEntities) {
        List<CreateCanCanRSDto> dtos = new ArrayList<>();

        for (int i = 0; i < canEntities.size(); i++) {
            dtos.add(convertEntityToDto(canEntities.get(i)));
        }
        return dtos;
    }
}
