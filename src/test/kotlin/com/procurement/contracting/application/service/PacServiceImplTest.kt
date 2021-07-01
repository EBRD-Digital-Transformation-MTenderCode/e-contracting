package com.procurement.contracting.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.contracting.application.repository.can.CANRepository
import com.procurement.contracting.application.repository.fc.FrameworkContractRepository
import com.procurement.contracting.application.repository.pac.PacRepository
import com.procurement.contracting.application.service.model.pacs.DoPacsParams
import com.procurement.contracting.application.service.model.pacs.DoPacsResult
import com.procurement.contracting.application.service.rule.RulesService
import com.procurement.contracting.domain.model.DynamicValue
import com.procurement.contracting.domain.model.Owner
import com.procurement.contracting.domain.model.Token
import com.procurement.contracting.domain.model.award.AwardId
import com.procurement.contracting.domain.model.bid.BidId
import com.procurement.contracting.domain.model.fc.PacEntity
import com.procurement.contracting.domain.model.lot.LotId
import com.procurement.contracting.domain.model.pac.PacId
import com.procurement.contracting.domain.model.pac.PacStatus
import com.procurement.contracting.domain.model.process.Cpid
import com.procurement.contracting.domain.model.process.Ocid
import com.procurement.contracting.get
import com.procurement.contracting.lib.functional.MaybeFail
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

internal class PacServiceImplTest {

    companion object {
        private val CPID = Cpid.orNull("ocds-b3wdp1-MD-1580458690892")!!
        private val OCID = Ocid.orNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791896")!!
        private val PAC_ID = PacId.orNull("c9e933a7-027e-497b-bbee-eb334fb795bb")!!
        private val OWNER: Owner = Owner.orNull("d0da4c24-1a2a-4b39-a1fd-034cb887c93b")!!
        private val TOKEN: Token = Token.orNull("62a72137-cdfa-4056-a56a-e519e56798a4")!!
        private val LOT_ID: LotId = LotId.orNull("f02720a6-de85-4a50-aa3d-e9348f1669dc")!!

        private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
            .withResolverStyle(ResolverStyle.STRICT)
        private val DATE = LocalDateTime.parse("2020-02-10T08:49:55Z", FORMATTER)
    }

    lateinit var pacRepository: PacRepository
    lateinit var canRepository: CANRepository
    lateinit var frameworkContractRepository: FrameworkContractRepository
    lateinit var pacService: PacService
    lateinit var transform: Transform
    lateinit var generationService: GenerationService
    lateinit var rulesService: RulesService


    @BeforeEach
    fun init() {
        pacRepository = mock()
        transform = mock()
        generationService = mock()
        rulesService = mock()
        frameworkContractRepository = mock()
        canRepository = mock()
        pacService = PacServiceImpl(generationService, transform, pacRepository, canRepository, frameworkContractRepository, rulesService)
    }

    @Test
    fun createWithoutAwards() {
        val paramsWithoutAwards = getParams().copy(awards = emptyList())
        whenever(generationService.pacId())
            .thenReturn(PAC_ID)
        whenever(pacRepository.save(records = ArgumentMatchers.anyCollection()))
            .thenReturn(MaybeFail.none())
        whenever(pacRepository.findBy(cpid = any(), ocid = any()))
            .thenReturn(Result.success(emptyList()))
        whenever(transform.trySerialization(value = any<PacEntity>()))
            .thenReturn("string".asSuccess())


        val actual = pacService.create(paramsWithoutAwards).get()
        val expected = null

        assertEquals(expected, actual)
    }

    @Test
    fun createWithAwards() {
        val paramsWithAwards = getParams()
        whenever(generationService.pacId())
            .thenReturn(PAC_ID)
        whenever(generationService.token())
            .thenReturn(TOKEN)
        whenever(pacRepository.save(records = ArgumentMatchers.anyCollection()))
            .thenReturn(MaybeFail.none())
        whenever(pacRepository.findBy(cpid = any(), ocid = any()))
            .thenReturn(Result.success(emptyList()))
        whenever(transform.trySerialization(value = any<PacEntity>()))
            .thenReturn("string".asSuccess())


        val actual = pacService.create(paramsWithAwards).get()
        val expected = getExpectedPacsResult(paramsWithAwards)

        assertEquals(expected, actual)
    }

    private fun getExpectedPacsResult(
        params: DoPacsParams
    ) = DoPacsResult(
        contracts = listOf(
            DoPacsResult.Contract(
                id = PAC_ID,
                status = PacStatus.PENDING,
                date = DATE,
                token = TOKEN,
                relatedLots = listOf(LOT_ID),
                suppliers = listOf(
                    DoPacsResult.Contract.Supplier(
                        id = "tenderer.id",
                        name = "tenderer.name"
                    )
                ),
                awardId = params.awards.first().id,
                agreedMetrics = listOf(
                    DoPacsResult.Contract.AgreedMetric(
                        id = "criteria.id",
                        title = "criteria.title",
                        observations = listOf(
                            DoPacsResult.Contract.AgreedMetric.Observation(
                                id = "requirementResponse.id",
                                notes = "requirement[0].title",
                                period = DoPacsResult.Contract.AgreedMetric.Observation.Period(
                                    startDate = DATE,
                                    endDate = DATE.plusDays(1)
                                ),
                                unit = DoPacsResult.Contract.AgreedMetric.Observation.Unit(
                                    id = "unit.id",
                                    name = "unit.name"
                                ),
                                relatedRequirementId = "requirement[0].id",
                                measure = DynamicValue.String("requirementResponse.value")
                            )
                        )
                    )
                )
            )
        )
    )

    @Test
    fun createWithAwardsWithoutMatchingSuppliers() {
        val paramsWithAwards = getParams()
        val paramsWithUnmatchingSuppliers = paramsWithAwards.copy(
            awards = paramsWithAwards.awards.first().copy(
                suppliers = listOf(
                    DoPacsParams.Award.Supplier(
                        id = "unmatching.id",
                        name = "tenderer.name",
                    )
                )
            ).let { listOf(it) }
        )

        whenever(generationService.pacId())
            .thenReturn(PAC_ID)
        whenever(generationService.token())
            .thenReturn(TOKEN)
        whenever(pacRepository.save(records = ArgumentMatchers.anyCollection()))
            .thenReturn(MaybeFail.none())
        whenever(pacRepository.findBy(cpid = any(), ocid = any()))
            .thenReturn(Result.success(emptyList()))
        whenever(transform.trySerialization(value = any<PacEntity>()))
            .thenReturn("string".asSuccess())


        val actual = pacService.create(paramsWithUnmatchingSuppliers).get()
        val expected = getExpectedPacsResultWithoutObservations(paramsWithUnmatchingSuppliers)

        assertEquals(expected, actual)
    }

    private fun getExpectedPacsResultWithoutObservations(
        params: DoPacsParams
    ) = DoPacsResult(
        contracts = listOf(
            DoPacsResult.Contract(
                id = PAC_ID,
                status = PacStatus.PENDING,
                date = DATE,
                token = TOKEN,
                relatedLots = listOf(LOT_ID),
                suppliers = listOf(
                    DoPacsResult.Contract.Supplier(
                        id = "unmatching.id",
                        name = "tenderer.name"
                    )
                ),
                awardId = params.awards.first().id,
                agreedMetrics = listOf(
                    DoPacsResult.Contract.AgreedMetric(
                        id = "criteria.id",
                        title = "criteria.title",
                        observations = emptyList()
                    )
                )
            )
        )
    )


    private fun getParams() =
        DoPacsParams(
            cpid = CPID,
            ocid = OCID,
            date = DATE,
            owner = OWNER,
            awards = DoPacsParams.Award(
                id = AwardId.generate(),
                suppliers = listOf(
                    DoPacsParams.Award.Supplier(
                        id = "tenderer.id",
                        name = "tenderer.name",
                    )
                )
            ).let { listOf(it) },
            tender = DoPacsParams.Tender(
                lots = listOf(
                    DoPacsParams.Tender.Lot(
                        id = LOT_ID
                    )
                ),
                criteria = DoPacsParams.Tender.Criteria(
                    id = "criteria.id",
                    title = "criteria.title",
                    relatesTo = "criteria.relatesTo",
                    relatedItem = "criteria.relatedItem",
                    requirementGroups = listOf(
                        DoPacsParams.Tender.Criteria.RequirementGroup(
                            id = "requirementGroup.id",
                            requirements = listOf(
                                DoPacsParams.Tender.Criteria.RequirementGroup.Requirement(
                                    id = "requirement[0].id",
                                    title = "requirement[0].title"
                                ),
                                DoPacsParams.Tender.Criteria.RequirementGroup.Requirement(
                                    id = "requirement[1].id",
                                    title = "requirement[1].title"
                                )
                            )
                        )
                    )
                ).let { listOf(it) },
                targets = listOf(
                    DoPacsParams.Tender.Target(
                        id = "target.id",
                        observations = listOf(
                            DoPacsParams.Tender.Target.Observation(
                                id = "observation.id",
                                relatedRequirementId = "requirement[0].id",
                                unit = DoPacsParams.Tender.Target.Observation.Unit(
                                    id = "unit.id",
                                    name = "unit.name"
                                )
                            )
                        )
                    )
                )
            ),
            bids = DoPacsParams.Bids(
                details = DoPacsParams.Bids.Detail(
                    id = BidId.randomUUID(),
                    tenderers = listOf(
                        DoPacsParams.Bids.Detail.Tenderer(
                            id = "tenderer.id",
                            name = "tenderer.name",
                        )
                    ),
                    requirementResponses = DoPacsParams.Bids.Detail.RequirementResponse(
                        id = "requirementResponse.id",
                        value = DynamicValue.String("requirementResponse.value"),
                        requirement = DoPacsParams.Bids.Detail.RequirementResponse.Requirement(id = "requirement[0].id"),
                        period = DoPacsParams.Bids.Detail.RequirementResponse.Period(
                            startDate = DATE,
                            endDate = DATE.plusDays(1)
                        )
                    ).let { listOf(it) }
                ).let { listOf(it) }
            )
        )
}