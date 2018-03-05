package com.procurement.contracting.service;

import com.fasterxml.uuid.Generators;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.checkContract.CheckCanRS;
import com.procurement.contracting.model.dto.contractAwardNotice.ChangeStatusCanRS;
import com.procurement.contracting.model.dto.contractAwardNotice.CreateCanCanRSDto;
import com.procurement.contracting.model.dto.contractAwardNotice.CreateCanContractRSDto;
import com.procurement.contracting.model.dto.contractAwardNotice.CreateCanRQ;
import com.procurement.contracting.model.dto.contractAwardNotice.CreateCanRS;
import com.procurement.contracting.model.entity.ACEntity;
import com.procurement.contracting.model.entity.CANEntity;
import com.procurement.contracting.repository.ACRepository;
import com.procurement.contracting.repository.CANRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CANServiseImpl implements CANServise {

    public static final String CAN_NOT_FOUND = "CAN not found";
    private final CANRepository canRepository;
    private final ACRepository acRepository;

    public CANServiseImpl(final CANRepository canRepository, final ACRepository acRepository) {
        this.canRepository = canRepository;
        this.acRepository = acRepository;
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
                responseDto.setError("invalid owner");
            }
        } else {
            responseDto.setError(CAN_NOT_FOUND);
        }
        return responseDto;
    }

    @Override
    public ResponseDto changeStatus(final String cpId, final String awardId) {
        final CANEntity canEntity = canRepository.getByCpIdAndAwardId(UUID.fromString(cpId), awardId);
        final ResponseDto responseDto = new ResponseDto(null, null, null);
        if (canEntity != null) {
            if (!isACCreated(canEntity)) {
                canEntity.setStatus(ContractStatus.UNSUCCESSFUL.toString());
                canEntity.setStatusDetails(null);
                responseDto.setSuccess(true);
                responseDto.setData(convertEntityToChangeStatusDto(canEntity));
            } else if (isStatusACCorresponds(canEntity, ContractStatus.TERMINATED)
                || isStatusACCorresponds(canEntity, ContractStatus.CANCELLED)) {
                canEntity.setStatus(ContractStatus.CANCELLED.toString());
                canEntity.setStatusDetails(null);
                responseDto.setSuccess(true);
                responseDto.setData(convertEntityToChangeStatusDto(canEntity));
            } else {
                responseDto.setError("do nothing");
            }
        } else {
            responseDto.setError(CAN_NOT_FOUND);
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

    private CreateCanCanRSDto convertEntityToCreateCANDto(final CANEntity contractAwardNoticeEntity) {

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

    private ChangeStatusCanRS convertEntityToChangeStatusDto(final CANEntity contractAwardNoticeEntity) {
        return new ChangeStatusCanRS(new CreateCanContractRSDto(contractAwardNoticeEntity.getCanId()
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
            dtos.add(convertEntityToCreateCANDto(canEntities.get(i)));
        }
        return dtos;
    }

    private boolean isACCreated(final CANEntity canEntity) {
        if (canEntity.getAcId() == null) {
            return false;
        }
        return true;
    }

    private boolean isStatusACCorresponds(final CANEntity canEntity, final ContractStatus contractStatus) {
        if (isACCreated(canEntity)) {
            final ACEntity acEntity = acRepository.getByCpIdAndAcId(canEntity.getCpId(), canEntity.getAcId());
            if (acEntity.getStatus()
                        .equals(contractStatus.toString())) {
                return true;
            }
        }
        return false;
    }
}
