package com.procurement.contracting.application.service.model

import com.procurement.contracting.domain.model.organization.OrganizationRole
import com.procurement.contracting.domain.model.parseCpid
import com.procurement.contracting.domain.model.parseEnum
import com.procurement.contracting.domain.model.parseOcid
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.domain.util.extension.getDuplicate
import com.procurement.contracting.infrastructure.fail.error.DataErrors
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asFailure
import com.procurement.contracting.lib.functional.asSuccess

class CreateConfirmationRequestsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val role: OrganizationRole,
    val contracts: List<Contract>,
    val access: Access?,
    val dossier: Dossier?,
    val submission: Submission?,
) {
    companion object {
        private val allowedRoles = OrganizationRole.allowedElements
            .filter {
                when (it) {
                    OrganizationRole.BUYER,
                    OrganizationRole.SUPPLIER,
                    OrganizationRole.PROCURING_ENTITY,
                    OrganizationRole.INVITED_CANDIDATE -> true
                }
            }
            .toSet()

        fun tryCreate(
            cpid: String,
            ocid: String,
            role: String,
            contracts: List<Contract>,
            access: Access?,
            dossier: Dossier?,
            submission: Submission?
        ): Result<CreateConfirmationRequestsParams, DataErrors.Validation> {
            val cpidParsed = parseCpid(value = cpid).onFailure { return it }
            val ocidParsed = parseOcid(value = ocid).onFailure { return it }
            val organizationRole = parseEnum(role, allowedRoles, "role", OrganizationRole).onFailure { return it }

            val duplicate = contracts.getDuplicate { it.id }
            if (duplicate != null)
                return DataErrors.Validation.UniquenessDataMismatch(name = "contract.id", value = duplicate.id).asFailure()

            return CreateConfirmationRequestsParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                role = organizationRole,
                contracts = contracts,
                access = access,
                dossier = dossier,
                submission = submission
            ).asSuccess()
        }
    }

    class Contract private constructor(
        val id: String,
        val documents: List<Document>
    ) {
        companion object {
            fun tryCreate(id: String, documents: List<Document>): Result<Contract, DataErrors.Validation.UniquenessDataMismatch> {
                val duplicate = documents.getDuplicate { it.id }
                if (duplicate != null)
                    return DataErrors.Validation.UniquenessDataMismatch(
                        name = "contract.documents.id",
                        value = duplicate.id
                    ).asFailure()

                return Contract(id = id, documents = documents).asSuccess()
            }
        }

        data class Document(
            val id: String
        )
    }

    class Access private constructor(
        val buyers: List<Buyer>,
        val procuringEntity: ProcuringEntity?
    ) {
        companion object {
            fun tryCreate(buyers: List<Buyer>, procuringEntity: ProcuringEntity?): Result<Access, DataErrors.Validation.UniquenessDataMismatch> {
                val duplicate = buyers.getDuplicate { it.id }
                if (duplicate != null)
                    return DataErrors.Validation.UniquenessDataMismatch(name = "access.buyers.id", value = duplicate.id)
                        .asFailure()

                return Access(buyers, procuringEntity).asSuccess()
            }
        }

        data class Buyer(
            val id: String,
            val name: String,
            val owner: String
        )

        data class ProcuringEntity(
            val id: String,
            val name: String,
            val owner: String
        )
    }

    data class Dossier(
        val candidates: List<Candidate>
    ) {

        class Candidate private constructor(
            val owner: String,
            val organizations: List<Organization>
        ) {

            companion object {
                fun tryCreate(owner: String, organizations: List<Organization>): Result<Candidate, DataErrors.Validation.UniquenessDataMismatch> {
                    val duplicate = organizations.getDuplicate { it.id }
                    if (duplicate != null)
                        return DataErrors.Validation.UniquenessDataMismatch(
                            name = "dossier.candidates.organizations.id",
                            value = duplicate.id
                        ).asFailure()

                    return Candidate(owner, organizations).asSuccess()
                }
            }

            data class Organization(
                val id: String,
                val name: String
            )
        }
    }

    data class Submission(
        val tenderers: List<Tenderer>
    ) {
        class Tenderer private constructor(
            val owner: String,
            val organizations: List<Organization>
        ) {

            companion object {
                fun tryCreate(
                    owner: String,
                    organizations: List<Organization>
                ): Result<Tenderer, DataErrors.Validation.UniquenessDataMismatch> {
                    val duplicate = organizations.getDuplicate { it.id }
                    if (duplicate != null)
                        return DataErrors.Validation.UniquenessDataMismatch(
                            name = "submission.tenderers.organizations.id",
                            value = duplicate.id
                        ).asFailure()

                    return Tenderer(owner, organizations).asSuccess()
                }
            }

            data class Organization(
                val id: String,
                val name: String
            )
        }
    }
}