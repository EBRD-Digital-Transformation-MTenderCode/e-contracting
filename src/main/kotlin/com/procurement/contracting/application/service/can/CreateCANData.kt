package com.procurement.contracting.application.service.can

import java.util.*

data class CreateCANData(val award: Award?) {
    data class Award(val id: UUID)
}
