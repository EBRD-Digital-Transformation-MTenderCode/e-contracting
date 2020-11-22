package com.procurement.contracting.application.service

import com.procurement.contracting.application.exception.repository.ReadEntityException
import com.procurement.contracting.application.exception.repository.SaveEntityException
import com.procurement.contracting.application.repository.ac.ACRepository
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.model.ContractProcess
import com.procurement.contracting.domain.entity.ACEntity
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.DOCS_RELATED_LOTS
import com.procurement.contracting.infrastructure.handler.v1.CommandMessage
import com.procurement.contracting.infrastructure.handler.v1.canId
import com.procurement.contracting.infrastructure.handler.v1.cpid
import com.procurement.contracting.infrastructure.handler.v1.model.request.DocumentUpdate
import com.procurement.contracting.infrastructure.handler.v1.model.request.UpdateDocumentContract
import com.procurement.contracting.infrastructure.handler.v1.model.request.UpdateDocumentsRq
import com.procurement.contracting.infrastructure.handler.v1.model.request.UpdateDocumentsRs
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service

@Service
class UpdateDocumentsService(
    private val acRepository: ACRepository,
    private val canRepository: CANRepository
) {

    fun updateCanDocs(cm: CommandMessage): UpdateDocumentsRs {
        val cpid = cm.cpid
        val canId: CANId = cm.canId
        val dto = toObject(UpdateDocumentsRq::class.java, cm.data)

        val canEntity = canRepository.findBy(cpid, canId)
            .orThrow {
                ReadEntityException(message = "Error read CAN from the database.", cause = it.exception)
            }
            ?: throw ErrorException(ErrorType.CAN_NOT_FOUND)
        val can = toObject(Can::class.java, canEntity.jsonData)
        if (can.status != CANStatus.PENDING) throw ErrorException(ErrorType.INVALID_CAN_STATUS)

        if (canEntity.contractId != null) {
            val acEntity: ACEntity = acRepository.findBy(cpid, canEntity.contractId)
                .orThrow { it.exception }
                ?: throw ErrorException(ErrorType.CONTRACT_NOT_FOUND)
            val contractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)
            if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(ErrorType.CONTRACT_STATUS)
            if (!(contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PREPARATION
                    || contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PROJECT)) throw ErrorException(ErrorType.CONTRACT_STATUS_DETAILS)
            validateDocsRelatedLotContract(dto, contractProcess)
        }
        validateDocsRelatedLotCan(dto, can)
        can.documents = updateCanDocuments(dto, can)

        val updatedCANEntity = canEntity.copy(
            status = can.status,
            statusDetails = can.statusDetails,
            jsonData = toJson(can)
        )
        val wasApplied = canRepository.update(cpid = cpid, entity = updatedCANEntity)
            .orThrow { it.exception }
        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of CAN by cpid '$cpid' and id '${updatedCANEntity.id}' to the database. Record is already.")

        return UpdateDocumentsRs(
            contract = UpdateDocumentContract(
                id = canId,
                documents = can.documents!!
            )
        )
    }

    private fun updateCanDocuments(dto: UpdateDocumentsRq, can: Can): List<DocumentContract>? {
        val documentsDb = can.documents
        val documentsDto = dto.documents
        //validation
        val documentDtoIds = documentsDto.asSequence().map { it.id }.toSet()
        if (documentDtoIds.size != documentsDto.size) throw ErrorException(ErrorType.DOCUMENTS)
        //update
        return if (documentsDb != null) {
            val documentsDbIds = documentsDb.asSequence().map { it.id }.toSet()
            documentsDb.forEach { docDb -> docDb.update(documentsDto.firstOrNull { it.id == docDb.id }) }
            val newDocumentsId = documentDtoIds - documentsDbIds
            val newDocuments = documentsDto.asSequence().filter { it.id in newDocumentsId }.toList()
            (documentsDb + convertUpdateDocsToBaseDocs(newDocuments))
        } else {
            convertUpdateDocsToBaseDocs(documentsDto)
        }
    }

    private fun convertUpdateDocsToBaseDocs(documents: List<DocumentUpdate>): List<DocumentContract> {
        val documentsContract = mutableListOf<DocumentContract>()
        documents.forEach {
            documentsContract.add(
                DocumentContract(
                    id = it.id,
                    documentType = DocumentTypeContract.creator(it.documentType.toString()),
                    title = it.title,
                    description = it.description,
                    relatedLots = it.relatedLots,
                    relatedConfirmations = it.relatedConfirmations
                )
            )
        }
        return documentsContract
    }

    private fun DocumentContract.update(documentDto: DocumentUpdate?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description ?: this.description
        }
    }

    private fun validateDocsRelatedLotContract(dto: UpdateDocumentsRq, contractProcess: ContractProcess) {
        val relatedLots: MutableList<LotId> = mutableListOf()
        dto.documents.forEach {
            val relatedLot = it.relatedLots?.toMutableList() ?: mutableListOf()
            if (relatedLot.isNotEmpty()) {
                relatedLots.addAll(relatedLot)
            }
        }
        if (relatedLots.isNotEmpty() && !relatedLots.contains(contractProcess.award.relatedLots.first()))
            throw ErrorException(DOCS_RELATED_LOTS)
    }

    private fun validateDocsRelatedLotCan(dto: UpdateDocumentsRq, can: Can) {
        val relatedLots: MutableList<LotId> = mutableListOf()
        dto.documents.forEach {
            val relatedLot = it.relatedLots?.toMutableList() ?: mutableListOf()
            if (relatedLot.isNotEmpty()) {
                relatedLots.addAll(relatedLot)
            }
        }
        if (relatedLots.isNotEmpty() && !relatedLots.contains(can.lotId))
            throw ErrorException(DOCS_RELATED_LOTS)
    }
}
