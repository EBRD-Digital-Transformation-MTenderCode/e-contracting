package com.procurement.contracting.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.contracting.model.dto.Can;
import com.procurement.contracting.model.dto.CreateCanRQ;
import com.procurement.contracting.model.dto.CreateCanRS;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.ocds.Award;
import com.procurement.contracting.model.dto.ocds.Contract;
import com.procurement.contracting.model.dto.ocds.ContractStatus;
import com.procurement.contracting.model.dto.ocds.ContractStatusDetails;
import com.procurement.contracting.model.entity.CANEntity;
import com.procurement.contracting.repository.ACRepository;
import com.procurement.contracting.repository.CANRepository;
import java.util.ArrayList;
import java.util.List;
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
    public ResponseDto createCAN(final String cpId, final String owner, final CreateCanRQ dto) {
        final List<CANEntity> canEntities = createCANEntities(cpId, owner, dto);
        final List<Can> cans = convertEntitiesToDtoList(canEntities);
        return new ResponseDto(true, null, new CreateCanRS(cans));
    }

    private List<CANEntity> createCANEntities(final String cpId, final String owner, final CreateCanRQ dto) {
        final List<CANEntity> canEntities = new ArrayList<>();
        for (Award awardDto : dto.getAwards()) {
            canEntities.add(createAndSaveCANEntity(cpId, awardDto.getId(), owner));
        }
        return canEntities;
    }

    private List<Can> convertEntitiesToDtoList(final List<CANEntity> canEntities) {
        final List<Can> cans = new ArrayList<>();
        for (CANEntity entity : canEntities) {
            final Can can = convertEntityToCanDto(entity);
            cans.add(can);
        }
        return cans;
    }

    private Can convertEntityToCanDto(final CANEntity entity) {
        final Contract contract = new Contract(
                entity.getToken().toString(),
                entity.getToken().toString(),
                entity.getAwardId(),
                ContractStatus.fromValue(entity.getStatus()),
                ContractStatusDetails.fromValue(entity.getStatusDetails()),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        return new Can(entity.getToken().toString(), contract);
    }

    private CANEntity createAndSaveCANEntity(final String cpId,
                                             final String awardId,
                                             final String owner) {
        final CANEntity canEntity = new CANEntity();
        canEntity.setCpId(cpId);
        canEntity.setToken(UUIDs.random());
        canEntity.setAwardId(awardId);
        canEntity.setOwner(owner);
        canEntity.setStatus(ContractStatus.PENDING.value());
        canEntity.setStatusDetails(ContractStatusDetails.CONTRACT_PROJECT.value());
        canRepository.save(canEntity);
        return canEntity;
    }

//    @Override
//    public ResponseDto checkCAN(final String cpId, final String token, final String idPlatform) {
//
//        final CANEntity entity = canRepository.getByCpIdAndToken(cpId, UUID.fromString(token));
//        final ResponseDto responseDto = new ResponseDto(null, null, null);
//        if (entity != null) {
//            if (idPlatform.equals(entity.getOwner())) {
//                if (ContractStatus.fromValue(entity.getStatus()) == ContractStatus.PENDING) {
//                    responseDto.setSuccess(true);
//                    responseDto.setData(new CheckCanRS(true));
//                } else {
//                    responseDto.setSuccess(true);
//                    responseDto.setData(new CheckCanRS(false));
//                }
//            } else {
//                throw new ErrorException(ErrorType.INVALID_OWNER);
//            }
//        } else {
//            throw new ErrorException(ErrorType.CAN_NOT_FOUND);
//        }
//        return responseDto;
//    }
//
//    @Override
//    public ResponseDto changeStatus(final String cpId, final String awardId) {
//        final CANEntity canEntity = canRepository.getByCpIdAndAwardId(cpId, awardId);
//        final ResponseDto responseDto = new ResponseDto(null, null, null);
//        if (canEntity != null) {
//            if (!isACCreated(canEntity)) {
//                canEntity.setStatus(ContractStatus.UNSUCCESSFUL.toString());
//                canEntity.setStatusDetails(null);
//                responseDto.setSuccess(true);
//                responseDto.setData(convertEntityToChangeStatusDto(canEntity));
//            } else if (isStatusACCorresponds(canEntity, ContractStatus.TERMINATED)
//                || isStatusACCorresponds(canEntity, ContractStatus.CANCELLED)) {
//                canEntity.setStatus(ContractStatus.CANCELLED.toString());
//                canEntity.setStatusDetails(null);
//                responseDto.setSuccess(true);
//                responseDto.setData(convertEntityToChangeStatusDto(canEntity));
//            }
//        } else {
//            throw new ErrorException(ErrorType.CAN_NOT_FOUND);
//        }
//        return responseDto;
//    }

//
//    private ChangeStatusCanRS convertEntityToChangeStatusDto(final CANEntity contractAwardNoticeEntity) {
//        return new ChangeStatusCanRS(new CreateCanContractRSDto(contractAwardNoticeEntity.getToken()
//                .toString(),
//                contractAwardNoticeEntity.getAwardId(),
//                ContractStatus.fromValue(contractAwardNoticeEntity
//                        .getStatus()),
//                ContractStatusDetails.fromValue(
//                        contractAwardNoticeEntity.getStatusDetails())));
//    }
//
//
//    private boolean isACCreated(final CANEntity canEntity) {
//        if (canEntity.getAcId() == null) {
//            return false;
//        }
//        return true;
//    }
//
//    private boolean isStatusACCorresponds(final CANEntity canEntity, final ContractStatus contractStatus) {
//        if (isACCreated(canEntity)) {
//            final ACEntity acEntity = acRepository.getByCpIdAndAcId(canEntity.getCpId(), canEntity.getAcId());
//            if (acEntity.getStatus()
//                    .equals(contractStatus.toString())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
