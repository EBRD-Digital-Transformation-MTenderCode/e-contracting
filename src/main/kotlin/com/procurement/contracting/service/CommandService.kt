package com.procurement.contracting.service

import com.procurement.contracting.application.service.CancelCANContext
import com.procurement.contracting.application.service.CancelCANData
import com.procurement.contracting.application.service.CancelCANService
import com.procurement.contracting.application.service.ac.ACService
import com.procurement.contracting.application.service.ac.CreateACContext
import com.procurement.contracting.application.service.ac.CreateACData
import com.procurement.contracting.application.service.can.CANService
import com.procurement.contracting.application.service.can.CreateCANContext
import com.procurement.contracting.application.service.can.CreateCANData
import com.procurement.contracting.application.service.treasury.TreasureProcessingContext
import com.procurement.contracting.application.service.treasury.TreasureProcessingData
import com.procurement.contracting.application.service.treasury.TreasuryProcessing
import com.procurement.contracting.dao.HistoryDao
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.infrastructure.dto.ac.create.CreateACRequest
import com.procurement.contracting.infrastructure.dto.ac.create.CreateACResponse
import com.procurement.contracting.infrastructure.dto.can.cancel.CancelCANRequest
import com.procurement.contracting.infrastructure.dto.can.cancel.CancelCANResponse
import com.procurement.contracting.infrastructure.dto.can.create.CreateCANRequest
import com.procurement.contracting.infrastructure.dto.can.create.CreateCANResponse
import com.procurement.contracting.infrastructure.dto.treasury.TreasureProcessingRequest
import com.procurement.contracting.infrastructure.dto.treasury.TreasureProcessingResponse
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.CommandType
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.bpe.cpid
import com.procurement.contracting.model.dto.bpe.language
import com.procurement.contracting.model.dto.bpe.lotId
import com.procurement.contracting.model.dto.bpe.ocid
import com.procurement.contracting.model.dto.bpe.owner
import com.procurement.contracting.model.dto.bpe.pmd
import com.procurement.contracting.model.dto.bpe.startDate
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toLocalDateTime
import com.procurement.contracting.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CommandService(
    private val historyDao: HistoryDao,
    private val createCanService: CreateCanService,
    private val updateAcService: UpdateAcService,
    private val issuingAcService: IssuingAcService,
    private val statusService: StatusService,
    private val finalUpdateService: FinalUpdateService,
    private val verificationAcService: VerificationAcService,
    private val signingAcService: SigningAcService,
    private val activationAcService: ActivationAcService,
    private val updateDocumentsService: UpdateDocumentsService,
    private val cancelService: CancelCANService,
    private val treasuryProcessing: TreasuryProcessing,
    private val canService: CANService,
    private val acService: ACService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CHECK_CAN -> createCanService.checkCan(cm)
            CommandType.CHECK_CAN_BY_AWARD -> createCanService.checkCanByAwardId(cm)
            CommandType.CREATE_CAN -> {
                val context = CreateCANContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    lotId = cm.lotId
                )
                val request = toObject(CreateCANRequest::class.java, cm.data)
                val createCANData = CreateCANData(
                    award = request.award?.let { award ->
                        CreateCANData.Award(
                            id = award.id
                        )
                    }
                )
                val result = canService.create(context = context, data = createCANData)
                if (log.isDebugEnabled)
                    log.debug("CAN was create. Result: ${toJson(result)}")

                val dataResponse = CreateCANResponse(
                    token = result.token,
                    can = result.can.let { can ->
                        CreateCANResponse.CAN(
                            id = can.id,
                            awardId = can.awardId,
                            lotId = can.lotId,
                            date = can.date,
                            status = can.status,
                            statusDetails = can.statusDetails
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("CAN was create. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.GET_CANS -> createCanService.getCans(cm)
            CommandType.UPDATE_CAN_DOCS -> updateDocumentsService.updateCanDocs(cm)
            CommandType.CANCEL_CAN -> {
                val context = CancelCANContext(
                    cpid = getCPID(cm),
                    token = getToken(cm),
                    owner = getOwner(cm),
                    canId = getCANId(cm)
                )
                val request = toObject(CancelCANRequest::class.java, cm.data)
                val data = CancelCANData(
                    amendment = request.contract.amendment.let { amendment ->
                        CancelCANData.Amendment(
                            rationale = amendment.rationale,
                            description = amendment.description,
                            documents = amendment.documents?.map { document ->
                                CancelCANData.Amendment.Document(
                                    id = document.id,
                                    documentType = document.documentType,
                                    title = document.title,
                                    description = document.description
                                )
                            }
                        )
                    }
                )
                val result = cancelService.cancel(context = context, data = data)
                if (log.isDebugEnabled)
                    log.debug("CANs were cancelled. Result: ${toJson(result)}")
                val dataResponse = CancelCANResponse(
                    cancelledCAN = result.cancelledCAN.let { can ->
                        CancelCANResponse.CancelledCAN(
                            id = can.id,
                            status = can.status,
                            statusDetails = can.statusDetails,
                            amendment = can.amendment.let { amendment ->
                                CancelCANResponse.CancelledCAN.Amendment(
                                    rationale = amendment.rationale,
                                    description = amendment.description,
                                    documents = amendment.documents?.map { document ->
                                        CancelCANResponse.CancelledCAN.Amendment.Document(
                                            id = document.id,
                                            documentType = document.documentType,
                                            title = document.title,
                                            description = document.description
                                        )
                                    }
                                )
                            }
                        )
                    },
                    relatedCANs = result.relatedCANs.map { relatedCAN ->
                        CancelCANResponse.RelatedCAN(
                            id = relatedCAN.id,
                            status = relatedCAN.status,
                            statusDetails = relatedCAN.statusDetails
                        )
                    },
                    contract = result.contract?.let { contract ->
                        CancelCANResponse.Contract(
                            id = contract.id,
                            status = contract.status,
                            statusDetails = contract.statusDetails
                        )
                    },
                    acCancel = result.isCancelledAC,
                    lotId = result.lotId
                )
                if (log.isDebugEnabled)
                    log.debug("CANs were cancelled. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.CONFIRMATION_CAN -> createCanService.confirmationCan(cm)
            CommandType.CREATE_AC -> {
                val context = CreateACContext(
                    cpid = cm.cpid,
                    owner = cm.owner,
                    pmd = cm.pmd,
                    startDate = cm.startDate,
                    language = cm.language
                )
                val request = toObject(CreateACRequest::class.java, cm.data)
                val data = CreateACData(
                    cans = request.cans.map { can ->
                        CreateACData.CAN(
                            id = can.id
                        )
                    },
                    awards = request.awards.map { award ->
                        CreateACData.Award(
                            id = award.id,
                            value = award.value.let { value ->
                                CreateACData.Award.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            relatedLots = award.relatedLots.toList(),
                            relatedBid = award.relatedBid,
                            suppliers = award.suppliers.map { supplier ->
                                CreateACData.Award.Supplier(
                                    id = supplier.id,
                                    name = supplier.name,
                                    identifier = supplier.identifier.let { identifier ->
                                        CreateACData.Award.Supplier.Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                    additionalIdentifiers = supplier.additionalIdentifiers?.map { additionalIdentifier ->
                                        CreateACData.Award.Supplier.AdditionalIdentifier(
                                            scheme = additionalIdentifier.scheme,
                                            id = additionalIdentifier.id,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    },
                                    address = supplier.address.let { address ->
                                        CreateACData.Award.Supplier.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { addressDetails ->
                                                CreateACData.Award.Supplier.Address.AddressDetails(
                                                    country = addressDetails.country.let { country ->
                                                        CreateACData.Award.Supplier.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description,
                                                            uri = country.uri
                                                        )
                                                    },
                                                    region = addressDetails.region.let { region ->
                                                        CreateACData.Award.Supplier.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description,
                                                            uri = region.uri
                                                        )
                                                    },
                                                    locality = addressDetails.locality.let { locality ->
                                                        CreateACData.Award.Supplier.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description,
                                                            uri = locality.uri
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    },
                                    contactPoint = supplier.contactPoint.let { contactPoint ->
                                        CreateACData.Award.Supplier.ContactPoint(
                                            name = contactPoint.name,
                                            email = contactPoint.email,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            url = contactPoint.url
                                        )
                                    }
                                )
                            },
                            documents = award.documents?.map { document ->
                                CreateACData.Award.Document(
                                    documentType = document.documentType,
                                    id = document.id,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots?.toList()
                                )
                            }
                        )
                    },
                    contractedTender = request.contractedTender.let { contractedTender ->
                        CreateACData.ContractedTender(
                            mainProcurementCategory = contractedTender.mainProcurementCategory,
                            items = contractedTender.items.map { item ->
                                CreateACData.ContractedTender.Item(
                                    id = item.id,
                                    internalId = item.internalId,
                                    classification = item.classification.let { classification ->
                                        CreateACData.ContractedTender.Item.Classification(
                                            scheme = classification.scheme,
                                            id = classification.id,
                                            description = classification.description
                                        )
                                    },
                                    additionalClassifications = item.additionalClassifications?.map { additionalClassification ->
                                        CreateACData.ContractedTender.Item.AdditionalClassification(
                                            scheme = additionalClassification.scheme,
                                            id = additionalClassification.id,
                                            description = additionalClassification.description
                                        )
                                    },
                                    quantity = item.quantity,
                                    unit = item.unit.let { unit ->
                                        CreateACData.ContractedTender.Item.Unit(
                                            id = unit.id,
                                            name = unit.name
                                        )
                                    },
                                    description = item.description,
                                    relatedLot = item.relatedLot
                                )
                            }
                        )
                    }
                )
                val result = acService.create(context = context, data = data)
                if (log.isDebugEnabled)
                    log.debug("AC was created. Result: ${toJson(result)}")

                val dataResponse = CreateACResponse(
                    token = result.token,
                    cans = result.cans.map { can ->
                        CreateACResponse.CAN(
                            id = can.id,
                            status = can.status,
                            statusDetails = can.statusDetails
                        )
                    },
                    contract = result.contract.let { contract ->
                        CreateACResponse.Contract(
                            id = contract.id,
                            awardId = contract.awardId,
                            status = contract.status,
                            statusDetails = contract.statusDetails
                        )
                    },
                    contractedAward = result.contractedAward.let { contractedAward ->
                        CreateACResponse.ContractedAward(
                            id = contractedAward.id,
                            date = contractedAward.date,
                            value = contractedAward.value.let { value ->
                                CreateACResponse.ContractedAward.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            relatedLots = contractedAward.relatedLots.toList(),
                            suppliers = contractedAward.suppliers.map { supplier ->
                                CreateACResponse.ContractedAward.Supplier(
                                    id = supplier.id,
                                    name = supplier.name,
                                    identifier = supplier.identifier.let { identifier ->
                                        CreateACResponse.ContractedAward.Supplier.Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                    additionalIdentifiers = supplier.additionalIdentifiers?.map { additionalIdentifier ->
                                        CreateACResponse.ContractedAward.Supplier.AdditionalIdentifier(
                                            scheme = additionalIdentifier.scheme,
                                            id = additionalIdentifier.id,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    },
                                    address = supplier.address.let { address ->
                                        CreateACResponse.ContractedAward.Supplier.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { addressDetails ->
                                                CreateACResponse.ContractedAward.Supplier.Address.AddressDetails(
                                                    country = addressDetails.country.let { country ->
                                                        CreateACResponse.ContractedAward.Supplier.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description,
                                                            uri = country.uri
                                                        )
                                                    },
                                                    region = addressDetails.region.let { region ->
                                                        CreateACResponse.ContractedAward.Supplier.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description,
                                                            uri = region.uri
                                                        )
                                                    },
                                                    locality = addressDetails.locality.let { locality ->
                                                        CreateACResponse.ContractedAward.Supplier.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description,
                                                            uri = locality.uri
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    },
                                    contactPoint = supplier.contactPoint.let { contactPoint ->
                                        CreateACResponse.ContractedAward.Supplier.ContactPoint(
                                            name = contactPoint.name,
                                            email = contactPoint.email,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            url = contactPoint.url
                                        )
                                    }
                                )
                            },
                            documents = contractedAward.documents.map { document ->
                                CreateACResponse.ContractedAward.Document(
                                    documentType = document.documentType,
                                    id = document.id,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots?.toList()
                                )
                            },
                            items = contractedAward.items.map { item ->
                                CreateACResponse.ContractedAward.Item(
                                    id = item.id,
                                    internalId = item.internalId,
                                    classification = item.classification.let { classification ->
                                        CreateACResponse.ContractedAward.Item.Classification(
                                            scheme = classification.scheme,
                                            id = classification.id,
                                            description = classification.description
                                        )
                                    },
                                    additionalClassifications = item.additionalClassifications?.map { additionalClassification ->
                                        CreateACResponse.ContractedAward.Item.AdditionalClassification(
                                            scheme = additionalClassification.scheme,
                                            id = additionalClassification.id,
                                            description = additionalClassification.description
                                        )
                                    },
                                    quantity = item.quantity,
                                    unit = item.unit.let { unit ->
                                        CreateACResponse.ContractedAward.Item.Unit(
                                            id = unit.id,
                                            name = unit.name
                                        )
                                    },
                                    description = item.description,
                                    relatedLot = item.relatedLot
                                )
                            }
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("AC was created. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.UPDATE_AC -> updateAcService.updateAC(cm)
            CommandType.CHECK_STATUS_DETAILS -> TODO()
            CommandType.GET_BUDGET_SOURCES -> statusService.getActualBudgetSources(cm)
            CommandType.GET_RELATED_BID_ID -> statusService.getRelatedBidId(cm)
            CommandType.ISSUING_AC -> issuingAcService.issuingAc(cm)
            CommandType.FINAL_UPDATE -> finalUpdateService.finalUpdate(cm)
            CommandType.BUYER_SIGNING_AC -> signingAcService.buyerSigningAC(cm)
            CommandType.SUPPLIER_SIGNING_AC -> signingAcService.supplierSigningAC(cm)
            CommandType.VERIFICATION_AC -> verificationAcService.verificationAc(cm)
            CommandType.TREASURY_RESPONSE_PROCESSING -> {
                val context = TreasureProcessingContext(
                    cpid = getCPID(cm),
                    ocid = getOCID(cm),
                    startDate = getStartDate(cm)
                )
                val request = toObject(TreasureProcessingRequest::class.java, cm.data)
                val data = TreasureProcessingData(
                    verification = request.verification.let { verification ->
                        TreasureProcessingData.Verification(
                            status = verification.status,
                            rationale = verification.rationale
                        )
                    },
                    dateMet = request.dateMet,
                    regData = request.regData?.let { regData ->
                        TreasureProcessingData.RegData(
                            regNom = regData.regNom,
                            regDate = regData.regDate
                        )
                    }
                )

                val result = treasuryProcessing.processing(context = context, data = data)
                if (log.isDebugEnabled)
                    log.debug("CANs were cancelled. Result: ${toJson(result)}")

                val dataResponse = TreasureProcessingResponse(
                    contract = result.contract.let { contract ->
                        TreasureProcessingResponse.Contract(
                            id = contract.id,
                            date = contract.date,
                            awardId = contract.awardId,
                            status = contract.status,
                            statusDetails = contract.statusDetails,
                            title = contract.title,
                            description = contract.description,
                            period = contract.period.let { period ->
                                TreasureProcessingResponse.Contract.Period(
                                    startDate = period.startDate,
                                    endDate = period.endDate
                                )
                            },
                            documents = contract.documents.map { document ->
                                TreasureProcessingResponse.Contract.Document(
                                    id = document.id,
                                    documentType = document.documentType,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots?.toList(),
                                    relatedConfirmations = document.relatedConfirmations?.toList()
                                )
                            },
                            milestones = contract.milestones.map { milestone ->
                                TreasureProcessingResponse.Contract.Milestone(
                                    id = milestone.id,
                                    relatedItems = milestone.relatedItems?.toList(),
                                    status = milestone.status,
                                    additionalInformation = milestone.additionalInformation,
                                    dueDate = milestone.dueDate,
                                    title = milestone.title,
                                    type = milestone.type,
                                    description = milestone.description,
                                    dateModified = milestone.dateModified,
                                    dateMet = milestone.dateMet,
                                    relatedParties = milestone.relatedParties.map { relatedParty ->
                                        TreasureProcessingResponse.Contract.Milestone.RelatedParty(
                                            id = relatedParty.id,
                                            name = relatedParty.name
                                        )
                                    }
                                )
                            },
                            confirmationRequests = contract.confirmationRequests.map { confirmationRequest ->
                                TreasureProcessingResponse.Contract.ConfirmationRequest(
                                    id = confirmationRequest.id,
                                    type = confirmationRequest.type,
                                    title = confirmationRequest.title,
                                    description = confirmationRequest.description,
                                    relatesTo = confirmationRequest.relatesTo,
                                    relatedItem = confirmationRequest.relatedItem,
                                    source = confirmationRequest.source,
                                    requestGroups = confirmationRequest.requestGroups.map { requestGroup ->
                                        TreasureProcessingResponse.Contract.ConfirmationRequest.RequestGroup(
                                            id = requestGroup.id,
                                            requests = requestGroup.requests.map { request ->
                                                TreasureProcessingResponse.Contract.ConfirmationRequest.RequestGroup.Request(
                                                    id = request.id,
                                                    title = request.title,
                                                    description = request.description,
                                                    relatedPerson = request.relatedPerson?.let { relatedPerson ->
                                                        TreasureProcessingResponse.Contract.ConfirmationRequest.RequestGroup.Request.RelatedPerson(
                                                            id = relatedPerson.id,
                                                            name = relatedPerson.name
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    }
                                )
                            },
                            confirmationResponses = contract.confirmationResponses.map { confirmationResponse ->
                                TreasureProcessingResponse.Contract.ConfirmationResponse(
                                    id = confirmationResponse.id,
                                    value = confirmationResponse.value.let { value ->
                                        TreasureProcessingResponse.Contract.ConfirmationResponse.Value(
                                            id = value.id,
                                            name = value.name,
                                            date = value.date,
                                            relatedPerson = value.relatedPerson?.let { relatedPerson ->
                                                TreasureProcessingResponse.Contract.ConfirmationResponse.Value.RelatedPerson(
                                                    id = relatedPerson.id,
                                                    name = relatedPerson.name
                                                )
                                            },
                                            verifications = value.verifications.map { verification ->
                                                TreasureProcessingResponse.Contract.ConfirmationResponse.Value.Verification(
                                                    type = verification.type,
                                                    value = verification.value,
                                                    rationale = verification.rationale
                                                )
                                            }
                                        )
                                    },
                                    request = confirmationResponse.request
                                )
                            },
                            value = contract.value.let { value ->
                                TreasureProcessingResponse.Contract.Value(
                                    amount = value.amount,
                                    currency = value.currency,
                                    amountNet = value.amountNet,
                                    valueAddedTaxIncluded = value.valueAddedTaxIncluded
                                )
                            }
                        )
                    },
                    cans = result.cans?.map { can ->
                        TreasureProcessingResponse.Can(
                            id = can.id,
                            status = can.status,
                            statusDetails = can.statusDetails
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("CANs were cancelled. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.ACTIVATION_AC -> activationAcService.activateAc(cm)
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }

    private fun getCPID(cm: CommandMessage): String = cm.context.cpid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

    private fun getOCID(cm: CommandMessage): String = cm.context.ocid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

    private fun getStartDate(cm: CommandMessage): LocalDateTime = cm.context.startDate?.toLocalDateTime()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

    private fun getToken(cm: CommandMessage): UUID = cm.context.token
        ?.let { token ->
            try {
                UUID.fromString(token)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

    private fun getOwner(cm: CommandMessage): String = cm.context.owner
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

    private fun getCANId(cm: CommandMessage): CANId = cm.context.id
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_CAN_ID)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")
}