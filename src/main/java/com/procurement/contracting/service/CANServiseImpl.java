package com.procurement.contracting.service;

import com.fasterxml.uuid.Generators;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.checkCAN.CheckCanRS;
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
    public ResponseDto createCAN(final String cpId, final String owner, final CreateCanRQ contractDto) {

        final List<CANEntity> canEntities = createCANEntities(cpId, owner, contractDto);

        final List<CreateCanCanRSDto> canDtos = convertCANEntitiesListToDtoList(canEntities);

        final CreateCanRS createCanRS = new CreateCanRS(canDtos);

        final ResponseDto responseDto = new ResponseDto(true, null, createCanRS);

        return responseDto;
    }

    @Override
    public ResponseDto checkCAN(final String cpId, final String token, final String idPlatform) {

        final CANEntity entity = canRepository.getByCpIdAndCanId(UUID.fromString(cpId), UUID.fromString(token));
        final ResponseDto responseDto = new ResponseDto(null, null, null);
        if (entity != null) {
            if (idPlatform.equals(entity.getOwner())) {
                if (ContractStatus.fromValue(entity.getStatus()) == ContractStatus.PENDING) {
                    responseDto.setSuccess(true);
                    responseDto.setData(new CheckCanRS(true));
                } else {
                    responseDto.setSuccess(true);
                    responseDto.setData(new CheckCanRS(false));
                }
            } else {

                final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                    "code",
                    "invalid owner"
                );
                final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                details.add(responseDetailsDto);
                responseDto.setSuccess(false);
                responseDto.setResponseDetail(details);
            }
        } else {
            final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                "code",
                "CAN not found"
            );
            final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
            details.add(responseDetailsDto);
            responseDto.setSuccess(false);
            responseDto.setResponseDetail(details);
        }
        return responseDto;
    }

    private CANEntity createAndSaveCANEntity(final UUID cpId,
                                             final String awardId,
                                             final String owner) {

        final CANEntity canEntity = new CANEntity();
        canEntity.setCpId(cpId);
        canEntity.setCanId(Generators.timeBasedGenerator()
                                     .generate());
        canEntity.setAwardId(awardId);
        canEntity.setOwner(owner);
        canEntity.setStatus(ContractStatus.PENDING.toString());
        canEntity.setStatusDetails("contractProject");

        canRepository.save(canEntity);

        return canEntity;
    }

    private CreateCanCanRSDto convertEntityToDto(final CANEntity contractAwardNoticeEntity) {

        return new CreateCanCanRSDto(contractAwardNoticeEntity.getCanId()
                                                              .toString(),
                                     new CreateCanContractRSDto(contractAwardNoticeEntity.getCanId()
                                                                                         .toString(),
                                                                contractAwardNoticeEntity.getAwardId(),
                                                                ContractStatus.fromValue(contractAwardNoticeEntity
                                                                                             .getStatus()),
                                                                ContractStatusDetails.fromValue(
                                                                    contractAwardNoticeEntity.getStatusDetails())));
    }

    private List<CANEntity> createCANEntities(final String cpId, final String owner, final CreateCanRQ contractDto) {
        final List<CANEntity> canEntities = new ArrayList<>();

        for (int i = 0; i < contractDto.getContractDtos()
                                       .size(); i++) {
            canEntities.add(createAndSaveCANEntity(UUID.fromString(cpId), contractDto.getContractDtos()
                                                                                     .get(i)
                                                                                     .getId(), owner));
        }
        return canEntities;
    }

    private List<CreateCanCanRSDto> convertCANEntitiesListToDtoList(final List<CANEntity> canEntities) {
        final List<CreateCanCanRSDto> dtos = new ArrayList<>();

        for (int i = 0; i < canEntities.size(); i++) {
            dtos.add(convertEntityToDto(canEntities.get(i)));
        }
        return dtos;
    }
}
