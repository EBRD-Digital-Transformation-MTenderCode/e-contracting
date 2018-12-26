package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.DOCS_RELATED_LOTS
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.UpdateDocumentContract
import com.procurement.contracting.model.dto.UpdateDocumentsRq
import com.procurement.contracting.model.dto.UpdateDocumentsRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.bpe.ResponseDto
import com.procurement.contracting.model.dto.ocds.*
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class UpdateDocumentsService(private val canDao: CanDao,
                             private val acDao: AcDao) {

    fun updateCanDocs(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val canId = cm.context.id ?: throw ErrorException(CONTEXT)
        val dto = toObject(UpdateDocumentsRq::class.java, cm.data)
        validateDocumentType(dto.documents)

        val canEntity = canDao.getByCpIdAndCanId(cpId, UUID.fromString(canId))
        val can = toObject(Can::class.java, canEntity.jsonData)
        if (!
            (can.status == ContractStatus.PENDING
                &&
                (can.statusDetails == ContractStatusDetails.CONTRACT_PROJECT
                    || can.statusDetails == ContractStatusDetails.CONTRACT_PREPARATION))) throw ErrorException(ErrorType.CAN_STATUS)

        if (canEntity.acId != null) {
            val acEntity = acDao.getByCpIdAndAcId(cpId, canEntity.acId!!)
            val contractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)
            if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(ErrorType.CONTRACT_STATUS)
            if (!(contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PREPARATION
                    || contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PROJECT)) throw ErrorException(ErrorType.CONTRACT_STATUS_DETAILS)
            validateDocsRelatedLotContract(dto, contractProcess)
        }
        validateDocsRelatedLotCan(dto, can)
        can.documents = updateCanDocuments(dto, can)
        canEntity.jsonData = toJson(can)
        canDao.save(canEntity)
        return ResponseDto(
            data = UpdateDocumentsRs(
                contract = UpdateDocumentContract(
                    id = canId,
                    documents = can.documents!!
                )
            ))
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
            (documentsDb + newDocuments)
        } else {
            documentsDto
        }
    }

    private fun DocumentContract.update(documentDto: DocumentContract?) {
        if (documentDto != null) {
            this.title = documentDto.title ?: this.title
            this.description = documentDto.description ?: this.description
        }
    }

    private fun validateDocsRelatedLotContract(dto: UpdateDocumentsRq, contractProcess: ContractProcess) {
        val relatedLots: MutableList<String> = mutableListOf()
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
        val relatedLots: MutableList<String> = mutableListOf()
        dto.documents.forEach {
            val relatedLot = it.relatedLots?.toMutableList() ?: mutableListOf()
            if (relatedLot.isNotEmpty()) {
                relatedLots.addAll(relatedLot)
            }
        }
        if (relatedLots.isNotEmpty() && !relatedLots.contains(can.lotId))
            throw ErrorException(DOCS_RELATED_LOTS)
    }

    private fun validateDocumentType(documents: List<DocumentContract>) {
        documents.forEach {
            if (it.documentType != DocumentTypeContract.EVALUATION_REPORT) throw ErrorException(ErrorType.DOCUMENTS_IS_NOT_EVALUATION_REPORTS)

        }
    }

}

