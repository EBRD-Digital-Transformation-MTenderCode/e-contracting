package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.*
import com.procurement.contracting.model.dto.*
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
        val token = cm.context.token ?: throw ErrorException(ErrorType.CONTEXT)
        val dto = toObject(UpdateDocumentsRq::class.java, cm.data)

        validateDocumentTypeInRequest(dto.documents)
        val canEntity = canDao.getByCpIdAndCanId(cpId, UUID.fromString(canId))
        val canAcOcId = canEntity.acId ?: throw ErrorException(CAN_AC_ID_NOT_FOUND)
        val acEntity = acDao.getByCpIdAndAcId(cpId, canAcOcId)
        val contractProcess = toObject(ContractProcess::class.java, acEntity.jsonData)

        if (canEntity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (contractProcess.contract.status != ContractStatus.PENDING) throw ErrorException(ErrorType.CONTRACT_STATUS)
        if (!(contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PREPARATION
                        || contractProcess.contract.statusDetails == ContractStatusDetails.CONTRACT_PROJECT)) throw ErrorException(ErrorType.CONTRACT_STATUS_DETAILS)

        val can = toObject(Can::class.java, canEntity.jsonData)

        val canDocuments = can.documents?.toMutableList() ?: mutableListOf()
        if (canDocuments.isEmpty()) {
            validateRelatedLotInRq(dto, contractProcess)
            val newDocuments: ArrayList<DocumentContract> = arrayListOf()
            dto.documents.forEach {
                newDocuments.add(DocumentContract(
                        id = it.id,
                        documentType = it.documentType,
                        title = it.title,
                        description = it.description,
                        relatedLots = it.relatedLots
                ))
                canDocuments.addAll(newDocuments)
            }
        } else {
            val newDocs = newDocumentsInRq(dtoDocuments = dto.documents, canDocuments = canDocuments)
            canDocuments.forEach { docDb -> docDb.update(dto.documents.firstOrNull { it.id == docDb.id }) }
            if (isNewDocumentsInRq(newDocs)) {
                validateRelatedLotInRq(dto, contractProcess)
                canDocuments.addAll(newDocs!!)
            }
        }
        can.apply {
            documents = canDocuments
        }
        canEntity.jsonData = toJson(can)
        canDao.save(canEntity)

        return ResponseDto(data = UpdateDocumentsRs(
                contract = UpdateDocumentContract(
                        id = canId,
                        documents = canDocuments
                )
        ))
    }

    private fun validateRelatedLotInRq(dto: UpdateDocumentsRq, contractProcess: ContractProcess) {
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

    private fun newDocumentsInRq(dtoDocuments: List<UpdateDocument>, canDocuments: List<DocumentContract>): List<DocumentContract>? {
        val newDocuments: ArrayList<DocumentContract> = arrayListOf()
        val canDocumentsIds: ArrayList<String> = arrayListOf()
        canDocuments.forEach {
            canDocumentsIds.add(it.id)
        }
        dtoDocuments.forEach {
            if (!canDocumentsIds.contains(it.id)) {
                newDocuments.add(DocumentContract(
                        id = it.id,
                        documentType = it.documentType,
                        title = it.title,
                        description = it.description,
                        relatedLots = it.relatedLots
                ))
            }
        }
        return newDocuments
    }

    private fun isNewDocumentsInRq(documents: List<DocumentContract>?): Boolean {
        if (documents != null && documents.isNotEmpty()) return true
        return false
    }

    private fun DocumentContract.update(documentDto: UpdateDocument?) {
        if (documentDto != null) {
            this.title = documentDto.title
            this.description = documentDto.description
        }
    }

    private fun validateDocumentTypeInRequest(documents: List<UpdateDocument>){
        documents.forEach{
            if(it.documentType!=DocumentTypeContract.EVALUATION_REPORT) throw ErrorException(ErrorType.DOCUMENTS_IS_NOT_EVALUATION_REPORTS)

        }
    }

}

