package com.procurement.contracting.domain.model

import com.procurement.contracting.exception.EnumElementProviderException
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.Result.Companion.failure
import com.procurement.contracting.lib.functional.Result.Companion.success

abstract class EnumElementProvider<T>(val info: EnumInfo<T>) where T : Enum<T>,
                                                                   T : EnumElementProvider.Element {

    interface Element {
        val key: String
        val isNeutralElement: Boolean
            get() = false

        val deprecated: Boolean
            get() = false
    }

    class EnumInfo<T>(
        val target: Class<T>,
        val values: Array<T>
    )

    companion object {
        inline fun <reified T : Enum<T>> info() = EnumInfo(target = T::class.java, values = enumValues())

        fun <T> Collection<T>.keysAsStrings(): List<String> where T : Enum<T>,
                                                                  T : Element = this
            .map { element -> element.key + if (element.deprecated) " (Deprecated)" else "" }
    }

    val allowedElements: Set<T> = info.values.toSet()

    private val elements: Map<String, T> = allowedElements.associateBy { it.key.toUpperCase() }

    fun orNull(key: String): T? = elements[key.toUpperCase()]

    fun orThrow(key: String): T = orNull(key)
        ?: throw EnumElementProviderException(
            enumType = info.target.canonicalName,
            value = key,
            values = info.values.joinToString { it.key }
        )

    fun tryOf(key: String): Result<T, String> {
        val element = orNull(key)
        return if (element != null)
            success(element)
        else {
            val enumType = info.target.canonicalName
            val allowedValues = info.values.joinToString { it.key }
            failure("Unknown value '$key' for enum type '$enumType'. Allowed values are '$allowedValues'.")
        }
    }

    operator fun contains(key: String): Boolean = orNull(key) != null
}