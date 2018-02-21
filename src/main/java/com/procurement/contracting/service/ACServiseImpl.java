package com.procurement.contracting.service;

import com.fasterxml.uuid.Generators;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.createAC.CreateACRQ;
import com.procurement.contracting.model.dto.createAC.CreateACRS;
import com.procurement.contracting.model.dto.createAC.CreateACContractRSDto;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import com.procurement.contracting.model.dto.updateAC.UpdateACRS;
import com.procurement.contracting.model.entity.ACEntity;
import com.procurement.contracting.model.entity.CANEntity;
import com.procurement.contracting.repository.ACRepository;
import com.procurement.contracting.repository.CANRepository;
import com.procurement.contracting.utils.DateUtil;
import com.procurement.contracting.utils.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ACServiseImpl implements ACServise {
    private final ACRepository acRepository;
    private final CANRepository canRepository;
    private final DateUtil dateUtil;
    private final JsonUtil jsonUtil;

    public ACServiseImpl(ACRepository acRepository,
                         CANRepository canRepository,
                         DateUtil dateUtil, JsonUtil jsonUtil) {
        this.acRepository = acRepository;
        this.canRepository = canRepository;
        this.dateUtil = dateUtil;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public ResponseDto createAC(String cpId, String token, CreateACRQ createACRQ) {

        CANEntity canEntity = canRepository.getByCpIdAndCanId(UUID.fromString(cpId), UUID.fromString(token));

        if (canEntity != null) {
            if (canEntity.getAcId() == null) {

                if (createACRQ.getContract()
                              .getId()
                              .equals(canEntity.getCanId()
                                               .toString())) {
                    canEntity.setStatus(ContractStatus.ACTIVE.toString());
                    canEntity.setStatusDetails(null);

                    CreateACRS createACRS = createCreateACRSFromRQ(createACRQ, token,ContractStatus.ACTIVE,null);
                    ACEntity acEntity = convertCreateACRSDtoToACEntity(cpId, createACRS);
                    String owner = canRepository.getOwnerByCpIdAndCanId(UUID.fromString(cpId),
                                                                        UUID.fromString(createACRS.getContracts().getExtendsContractID()));
                    acEntity.setOwner(owner);
                    acEntity.setReleaseDate(dateUtil.getNowUTC());

                    canEntity.setAcId(acEntity.getAcId());

                    canRepository.save(canEntity);
                    acRepository.save(acEntity);

                    return new ResponseDto(true, null, createACRS);
                } else {
                    final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                        "code",
                        "invalid CAN id"
                    );
                    final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                    details.add(responseDetailsDto);
                    return new ResponseDto(false, details, null);
                }
            } else {
                final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                    "code",
                    "AC already created"
                );
                final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                details.add(responseDetailsDto);
                return new ResponseDto(false, details, null);
            }
        } else {
            final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                "code",
                "not found"
            );
            final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
            details.add(responseDetailsDto);
            return new ResponseDto(false, details, null);
        }
    }

    @Override
    public ResponseDto updateAC(String cpId, String token, UpdateACRQ updateACRQ) {

        ACEntity acEntity = acRepository.getByCpIdAndCanId(UUID.fromString(cpId),UUID.fromString(token));
        if (acEntity!=null){
            String jsonCreateData = acEntity.getJsonCreateData();
            String jsonUpdateData = acEntity.getJsonUpdateData();
            if (jsonUpdateData == null){
                CreateACRS createACRS = jsonUtil.toObject(CreateACRS.class,jsonCreateData);
                if(isTitleOrDescriptionChanged(createACRS, updateACRQ)){
                    //меняем
                }

            }else{
                UpdateACRS updateACRS = jsonUtil.toObject(UpdateACRS.class,jsonUpdateData);
                if (isTitleOrDescriptionChanged(updateACRS,updateACRQ)){
                    //меняем
                }

            }
        }else{
            //not found
        }


        return null;
    }

    private CreateACRS createCreateACRSFromRQ(CreateACRQ createACRQ, String CANid, ContractStatus contractStatus, ContractStatusDetails statusDetails) {
        final List<CreateACRS> createACRSList = new ArrayList<>();

        for (int i = 0; i < createACRQ.getLots()
                                      .size(); i++) {
            String contractTitle = createACRQ.getLots()
                                             .get(i)
                                             .getTitle();
            String contractDescription = createACRQ.getLots()
                                                   .get(i)
                                                   .getDescription();
            UUID acId = Generators.timeBasedGenerator()
                                  .generate();

            CreateACContractRSDto createACRSDto = new CreateACContractRSDto(acId.toString(),
                                                                            createACRQ.getContract()
                                                                      .getAwardID(),
                                                                            CANid,
                                                                            contractTitle,
                                                                            contractDescription,
                                                                            ContractStatus.PENDING,
                                                                            ContractStatusDetails.CONTRACT_PROJECT,
                                                                            createACRQ.getAward()
                                                                      .getValue(),
                                                                            createACRQ.getItems());



            createACRSList.add(new CreateACRS(acId.toString(), contractStatus, statusDetails, createACRSDto));
        }
        return createACRSList.get(0);
    }

    private ACEntity convertCreateACRSDtoToACEntity(String cpId, CreateACRS createACRS) {

        ACEntity acEntity = new ACEntity();
        acEntity.setCpId(UUID.fromString(cpId));
        acEntity.setAcId(UUID.fromString(createACRS.getToken()));
        acEntity.setCanId(UUID.fromString(createACRS.getContracts()
                                                    .getExtendsContractID()));
        acEntity.setStatus(createACRS.getContracts().getStatus()
                                     .toString());

        acEntity.setStatusDetails(createACRS.getContracts().getStatusDetails()
                                                .toString());

        acEntity.setJsonCreateData(jsonUtil.toJson(createACRS));
        return acEntity;
    }

    private boolean isTitleOrDescriptionChanged(CreateACRS createACRS, UpdateACRQ updateACRQ){
        if(!createACRS.getContracts().getDescription().equals(updateACRQ.getContracts().getDescription())
            || !createACRS.getContracts().getTitle().equals(updateACRQ.getContracts().getTitle())){
            return true;
        }
        return false;
    }
    private boolean isTitleOrDescriptionChanged(UpdateACRS createACRS, UpdateACRQ updateACRQ){
        if(!createACRS.getContracts().getDescription().equals(updateACRQ.getContracts().getDescription())
            || !createACRS.getContracts().getTitle().equals(updateACRQ.getContracts().getTitle())){
            return true;
        }
        return false;
    }

}
