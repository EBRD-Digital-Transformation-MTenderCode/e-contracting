package com.procurement.contracting.extension

const val UUID_PATTERN: String = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"

val UUIDRegex = UUID_PATTERN.toRegex()

val String.isUUID: Boolean
    get() = UUIDRegex.matches(this)
