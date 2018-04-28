package com.procurement.contracting.service;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.uuid.Generators;
import com.procurement.contracting.exception.ErrorException;
import com.procurement.contracting.exception.ErrorType;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.checkContract.CheckCanRS;
import com.procurement.contracting.model.dto.contractAwardNotice.*;
import com.procurement.contracting.model.entity.ACEntity;
import com.procurement.contracting.model.entity.CANEntity;
import com.procurement.contracting.repository.ACRepository;
import com.procurement.contracting.repository.CANRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CANServiceImpl implements CANService {

    private final CANRepository canRepository;
    private final ACRepository acRepository;

    public CANServiceImpl(final CANRepository canRepository, final ACRepository acRepository) {
        this.canRepository = canRepository;
        this.acRepository = acRepository;
    }

    @Override
    public ResponseDto createCAN(final String cpId, final String owner, final CreateCanRQ contractDto) {
        final List<CANEntity> canEntities = createCANEntities(cpId, owner, contractDto);
        return new ResponseDto(true, null, new CreateCanRS(convertCANEntitiesListToDtoList(canEntities)));
    }

    @Override
    public ResponseDto checkCAN(final String cpId, final String token, final String idPlatform) {

        final CANEntity entity = canRepository.getByCpIdAndCanId(cpId, UUID.fromString(token));
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
                throw new ErrorException(ErrorType.INVALID_OWNER);
            }
        } else {
            throw new ErrorException(ErrorType.CAN_NOT_FOUND);
        }
        return responseDto;
    }

    @Override
    public ResponseDto changeStatus(final String cpId, final String awardId) {
        final CANEntity canEntity = canRepository.getByCpIdAndAwardId(cpId, awardId);
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
            }
        } else {
            throw new ErrorException(ErrorType.CAN_NOT_FOUND);
        }
        return responseDto;
    }

    private CANEntity createAndSaveCANEntity(final String cpId,
                                             final String awardId,
                                             final String owner) {
        final CANEntity canEntity = new CANEntity();
        canEntity.setCpId(cpId);
        canEntity.setCanId(UUIDs.timeBased());
        canEntity.setAwardId(awardId);
        canEntity.setOwner(owner);
        canEntity.setStatus(ContractStatus.PENDING.toString());
        canEntity.setStatusDetails(ContractStatusDetails.CONTRACT_PROJECT.value());
        canRepository.save(canEntity);
        return canEntity;
    }

    private CreateCanRSDto convertEntityToCreateCANDto(final CANEntity contractAwardNoticeEntity) {

        return new CreateCanRSDto(contractAwardNoticeEntity.getCanId().toString(),
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
        for (AwardDto awardDto: contractDto.getAwards()) {
            canEntities.add(createAndSaveCANEntity(cpId, awardDto.getId(), owner));
        }
        return canEntities;
    }

    private List<CreateCanRSDto> convertCANEntitiesListToDtoList(final List<CANEntity> canEntities) {
        final List<CreateCanRSDto> dtos = new ArrayList<>();
        for (CANEntity entity :canEntities) {
            dtos.add(convertEntityToCreateCANDto(entity));
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
