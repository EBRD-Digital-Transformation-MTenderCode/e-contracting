package com.procurement.contracting.service;

import com.fasterxml.uuid.Generators;
import com.procurement.contracting.model.dto.ContractDocumentDto;
import com.procurement.contracting.model.dto.ContractStatus;
import com.procurement.contracting.model.dto.ContractStatusDetails;
import com.procurement.contracting.model.dto.awardedContract.ACContractDto;
import com.procurement.contracting.model.dto.awardedContract.ACDto;
import com.procurement.contracting.model.dto.awardedContract.CreateACRQ;
import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.updateAC.UpdateACRQ;
import com.procurement.contracting.model.entity.ACEntity;
import com.procurement.contracting.model.entity.CANEntity;
import com.procurement.contracting.repository.ACRepository;
import com.procurement.contracting.repository.CANRepository;
import com.procurement.contracting.utils.DateUtil;
import com.procurement.contracting.utils.JsonUtil;
import java.time.LocalDateTime;
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

    public ACServiseImpl(final ACRepository acRepository,
                         final CANRepository canRepository,
                         final DateUtil dateUtil, final JsonUtil jsonUtil) {
        this.acRepository = acRepository;
        this.canRepository = canRepository;
        this.dateUtil = dateUtil;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public ResponseDto createAC(final String cpId, final String token, final CreateACRQ createACRQ) {

        final CANEntity canEntity = canRepository.getByCpIdAndCanId(UUID.fromString(cpId), UUID.fromString(token));
        final ResponseDto responseDto = new ResponseDto(null, null, null);
        if (canEntity != null) {
            if (canEntity.getAcId() == null) {

                if (createACRQ.getContract()
                              .getId()
                              .equals(canEntity.getCanId()
                                               .toString())) {
                    canEntity.setStatus(ContractStatus.ACTIVE.toString());
                    canEntity.setStatusDetails(null);

                    final ACDto acDto = createCreateACRSFromRQ(createACRQ, token, ContractStatus.ACTIVE, null);
                    final ACEntity acEntity = convertACDtoToACEntity(cpId, acDto);
                    final String owner = canRepository.getOwnerByCpIdAndCanId(UUID.fromString(cpId),
                                                                              UUID.fromString(
                                                                                        acDto
                                                                                       .getContracts()
                                                                                       .getExtendsContractId()));
                    acEntity.setOwner(owner);
                    acEntity.setReleaseDate(dateUtil.getNowUTC());

                    canEntity.setAcId(acEntity.getAcId());

                    canRepository.save(canEntity);
                    acRepository.save(acEntity);

                    responseDto.setSuccess(true);
                    responseDto.setData(acDto);
                } else {
                    final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                        "code",
                        "invalid CAN id"
                    );
                    final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                    details.add(responseDetailsDto);

                    responseDto.setSuccess(false);
                    responseDto.setResponseDetail(details);
                }
            } else {
                final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                    "code",
                    "AC already created"
                );
                final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                details.add(responseDetailsDto);
                responseDto.setSuccess(false);
                responseDto.setResponseDetail(details);
            }
        } else {
            final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                "code",
                "not found"
            );
            final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
            details.add(responseDetailsDto);
            responseDto.setSuccess(false);
            responseDto.setResponseDetail(details);
        }
        return responseDto;
    }

    @Override
    public ResponseDto updateAC(final String cpId, final String token, final UpdateACRQ updateACRQ) {

        final ACEntity acEntity = acRepository.getByCpIdAndCanId(UUID.fromString(cpId), UUID.fromString(token));
        final ResponseDto responseDto = new ResponseDto(null, null, null);
        if (acEntity != null) {
            if (updateACRQ.getContracts()
                          .getAmendments()
                          .size() > 0) {
                final String jsonData = acEntity.getJsonData();
                final ACDto acDto = jsonUtil.toObject(ACDto.class, jsonData);
                //title description
                if (isTitleOrDescriptionChanged(acDto, updateACRQ)
                    && isStatusPending(acEntity)) {
                    acDto.getContracts()
                         .setTitle(updateACRQ.getContracts()
                                             .getTitle());
                    acDto.getContracts()
                         .setDescription(updateACRQ.getContracts()
                                                   .getDescription());
                }
                //documents
                if (updateACRQ.getContracts()
                              .getDocuments() != null) {
                    final List<ContractDocumentDto> savedDocs = acDto.getContracts()
                                                               .getDocuments();
                    final List<ContractDocumentDto> newDocs = getNewDocumets(savedDocs, updateACRQ.getContracts()
                                                                                            .getDocuments());
                    if (newDocs.size() > 0) {
                        final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                            "code",
                            "documents is not valid"
                        );
                        final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                        if (isStatusPending(acEntity)) {
                            if (isValidDocumentsFromPending(newDocs)) {
                                savedDocs.addAll(newDocs);
                                acDto.getContracts()
                                     .setDocuments(savedDocs);
                            } else {

                                details.add(responseDetailsDto);
                                responseDto.setSuccess(false);
                                responseDto.setResponseDetail(details);
                            }
                        } else if (isStatusActive(acEntity)) {
                            if (isValidDocumentFromActive(newDocs)) {
                                savedDocs.addAll(newDocs);
                                acDto.getContracts()
                                     .setDocuments(newDocs);
                            } else {
                                details.add(responseDetailsDto);
                                responseDto.setSuccess(false);
                                responseDto.setResponseDetail(details);
                            }
                        }
                    }
                }

                //date

                final LocalDateTime contractStartDate = updateACRQ.getContracts()
                                                            .getPeriod()
                                                            .getStartDate();
                if ((isStatusActive(acEntity) || isStatusPending(acEntity))
                    && contractStartDate.isAfter(dateUtil.getNowUTC())) {
                    acDto.getContracts()
                         .getPeriod()
                         .setStartDate(contractStartDate);
                }

                final LocalDateTime contractEndDate = updateACRQ.getContracts()
                                                          .getPeriod()
                                                          .getEndDate();
                if ((isStatusActive(acEntity) || isStatusPending(acEntity))
                    && contractEndDate.isAfter(contractStartDate)) {
                    acDto.getContracts()
                         .getPeriod()
                         .setEndDate(contractEndDate);
                }
                final LocalDateTime contractDateSigned = updateACRQ.getContracts()
                                                             .getDateSigned();

                if (contractDateSigned != null) {
                    if (isStatusPending(acEntity) && contractDateSigned.isBefore(dateUtil.getNowUTC())) {
                        acDto.getContracts()
                             .setDateSigned(contractDateSigned);
                    }
                }

                acEntity.setReleaseDate(dateUtil.getNowUTC());
                acEntity.setJsonData(jsonUtil.toJson(acDto));
                acRepository.save(acEntity);
                return new ResponseDto(true, null, acDto);
            } else {
                final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                    "code",
                    "no amendments!"
                );
                final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
                details.add(responseDetailsDto);
                responseDto.setSuccess(false);
                responseDto.setResponseDetail(details);
            }
            responseDto.setSuccess(true);
            responseDto.setData(responseDto);
        } else {
            final ResponseDto.ResponseDetailsDto responseDetailsDto = new ResponseDto.ResponseDetailsDto(
                "code",
                "ac not found"
            );
            final List<ResponseDto.ResponseDetailsDto> details = new ArrayList<>();
            details.add(responseDetailsDto);
            responseDto.setSuccess(false);
            responseDto.setResponseDetail(details);
        }

        return responseDto;
    }

    private ACDto createCreateACRSFromRQ(final CreateACRQ createACRQ,
                                         final String canId,
                                         final ContractStatus contractStatus,
                                         final ContractStatusDetails statusDetails) {
        final List<ACDto> createACRSList = new ArrayList<>();

        for (int i = 0; i < createACRQ.getLots()
                                      .size(); i++) {
            final String contractTitle = createACRQ.getLots()
                                             .get(i)
                                             .getTitle();
            final String contractDescription = createACRQ.getLots()
                                                   .get(i)
                                                   .getDescription();
            final UUID acId = Generators.timeBasedGenerator()
                                  .generate();

            final ACContractDto createACRSDto = new ACContractDto(acId.toString(),
                                                            createACRQ.getContract()
                                                                      .getAwardID(),
                                                            canId,
                                                            null,
                                                            contractTitle,
                                                            contractDescription,
                                                            ContractStatus.PENDING,
                                                            ContractStatusDetails.CONTRACT_PROJECT,
                                                            null,
                                                            null,
                                                            createACRQ.getAward()
                                                                      .getValue(),
                                                            createACRQ.getItems(),
                                                            null,
                                                            null,
                                                            null,
                                                            null);

            createACRSList.add(new ACDto(acId.toString(), contractStatus, statusDetails, createACRSDto));
        }
        return createACRSList.get(0);
    }

    private ACEntity convertACDtoToACEntity(final String cpId, final ACDto acDto) {

        final ACEntity acEntity = new ACEntity();
        acEntity.setCpId(UUID.fromString(cpId));
        acEntity.setAcId(UUID.fromString(acDto.getToken()));
        acEntity.setCanId(UUID.fromString(acDto.getContracts()
                                               .getExtendsContractId()));
        acEntity.setStatus(acDto.getContracts()
                                .getStatus()
                                .toString());

        acEntity.setStatusDetails(acDto.getContracts()
                                       .getStatusDetails()
                                       .toString());
        acEntity.setJsonData(jsonUtil.toJson(acDto));

        return acEntity;
    }

    private boolean isTitleOrDescriptionChanged(final ACDto acDto, final UpdateACRQ updateACRQ) {
        if (!acDto.getContracts()
                  .getDescription()
                  .equals(updateACRQ.getContracts()
                                    .getDescription())
            || !acDto.getContracts()
                     .getTitle()
                     .equals(updateACRQ.getContracts()
                                       .getTitle())) {
            return true;
        }
        return false;
    }

    private boolean isStatusPending(final ACEntity acEntity) {
        if (acEntity.getStatus()
                    .equals(ContractStatus.PENDING.toString())) {
            return true;
        }
        return false;
    }

    private boolean isStatusActive(final ACEntity acEntity) {
        if (acEntity.getStatus()
                    .equals(ContractStatus.ACTIVE.toString())) {
            return true;
        }
        return false;
    }

    private List<ContractDocumentDto> getNewDocumets(final List<ContractDocumentDto> savedDocs,
                                                     final List<ContractDocumentDto> inputDocs) {

        final List<ContractDocumentDto> newDocs = new ArrayList<>();

        for (int i = 0; i < inputDocs.size(); i++) {
            if (!savedDocs.contains(inputDocs.get(i))) {
                newDocs.add(inputDocs.get(i));
            }
        }
        return newDocs;
    }

    private boolean isValidDocumentsFromPending(final List<ContractDocumentDto> docs) {
        final List<ContractDocumentDto.DocumentType> validDocuments = new ArrayList<>();
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_NOTICE);
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_SIGNED);
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_SCHEDULE);
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_ANNEXE);
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_GUARANTEES);
        validDocuments.add(ContractDocumentDto.DocumentType.ILLUSTRATION);
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_SUMMARY);
        validDocuments.add(ContractDocumentDto.DocumentType.SUBMISSION_DOCUMENTS);

        for (int i = 0; i < docs.size(); i++) {
            if (!validDocuments.contains(docs.get(i)
                                             .getDocumentType())) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidDocumentFromActive(final List<ContractDocumentDto> docs) {
        final List<ContractDocumentDto.DocumentType> validDocuments = new ArrayList<>();
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_SCHEDULE);
        validDocuments.add(ContractDocumentDto.DocumentType.ILLUSTRATION);
        validDocuments.add(ContractDocumentDto.DocumentType.CONTRACT_SUMMARY);

        for (int i = 0; i < docs.size(); i++) {
            if (!validDocuments.contains(docs.get(i)
                                             .getDocumentType())) {
                return false;
            }
        }
        return true;
    }
}
