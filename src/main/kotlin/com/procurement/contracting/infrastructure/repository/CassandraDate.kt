package com.procurement.contracting.infrastructure.repository

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDate.toCassandraLocalDate(): com.datastax.driver.core.LocalDate =
    com.datastax.driver.core.LocalDate.fromDaysSinceEpoch(this.toEpochDay().toInt())

fun com.datastax.driver.core.LocalDate.toLocalDate(): LocalDate =
    LocalDate.ofEpochDay(this.daysSinceEpoch.toLong())

fun Date.toLocalDateTime(): LocalDateTime =
    this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toCassandraTimestamp(): Date =
    Date.from(this.atZone(ZoneId.systemDefault()).toInstant())