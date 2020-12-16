package com.procurement.contracting.domain.model

import java.math.BigDecimal

sealed class DynamicValue {

    data class Boolean(val underlying: kotlin.Boolean) : DynamicValue(), Comparable<Boolean> {
        override fun compareTo(other: Boolean): Int = underlying.compareTo(other.underlying)
    }

    data class String(val underlying: kotlin.String) : DynamicValue(), Comparable<String> {
        override fun compareTo(other: String): Int = underlying.compareTo(other.underlying)
    }

    data class Integer(val underlying: Long) : DynamicValue(), Comparable<Integer> {
        override fun compareTo(other: Integer): Int =
            when {
                underlying > other.underlying -> 1
                underlying < other.underlying -> -1
                else -> 0
            }
    }

    data class Number(val underlying: BigDecimal) : DynamicValue(), Comparable<Number> {
        override fun compareTo(other: Number): Int =
            when {
                underlying > other.underlying -> 1
                underlying < other.underlying -> -1
                else -> 0
            }
    }

    enum class DataType(override val key: kotlin.String) : EnumElementProvider.Element {

        BOOLEAN("boolean"),
        INTEGER("integer"),
        NUMBER("number"),
        STRING("string");

        override fun toString(): kotlin.String = key

        companion object : EnumElementProvider<DataType>(info = info())
    }
}

val DynamicValue.dataType: DynamicValue.DataType
    get() = when (this) {
        is DynamicValue.Boolean -> DynamicValue.DataType.BOOLEAN
        is DynamicValue.String -> DynamicValue.DataType.STRING
        is DynamicValue.Integer -> DynamicValue.DataType.INTEGER
        is DynamicValue.Number -> DynamicValue.DataType.NUMBER
    }

fun DynamicValue.isDataTypeMatched(dataType: DynamicValue.DataType) = this.dataType == dataType
