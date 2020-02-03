package com.procurement.contracting.service

import com.procurement.contracting.dao.AcDao
import com.procurement.contracting.dao.CanDao
import com.procurement.contracting.domain.model.can.CANId
import com.procurement.contracting.domain.model.can.status.CANStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatus
import com.procurement.contracting.domain.model.contract.status.ContractStatusDetails
import com.procurement.contracting.domain.model.document.type.DocumentTypeContract
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.exception.ErrorException
import com.procurement.contracting.exception.ErrorType
import com.procurement.contracting.exception.ErrorType.CONTEXT
import com.procurement.contracting.exception.ErrorType.DOCS_RELATED_LOTS
import com.procurement.contracting.model.dto.ContractProcess
import com.procurement.contracting.model.dto.DocumentUpdate
import com.procurement.contracting.model.dto.UpdateDocumentContract
import com.procurement.contracting.model.dto.UpdateDocumentsRq
import com.procurement.contracting.model.dto.UpdateDocumentsRs
import com.procurement.contracting.model.dto.bpe.CommandMessage
import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.DocumentContract
import com.procurement.contracting.utils.toJson
import com.procurement.contracting.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class UpdateDocumentsService(private val canDao: CanDao,
                             private val acDao: AcDao) {

    fun updateCanDocs(cm: CommandMessage): UpdateDocumentsRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val canId: CANId = cm.context.id?.let{ UUID.fromString(it) } ?: throw ErrorException(CONTEXT)
        val dto = toObject(UpdateDocumentsRq::class.java, cm.data)

        val canEntity = canDao.getByCpIdAndCanId(cpId, canId)
        val can = toObject(Can::class.java, canEntity.jsonData)
        if (can.status != CANStatus.PENDING) throw ErrorException(ErrorType.INVALID_CAN_STATUS)

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
                    documentType = DocumentTypeContract.fromString(it.documentType.toString()),
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

