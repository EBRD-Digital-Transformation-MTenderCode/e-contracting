package com.procurement.contracting

import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.converter.ArgumentConversionException
import org.junit.jupiter.params.converter.ArgumentConverter

abstract class AbstractArgumentConverter<T : Any> : ArgumentConverter {
    abstract fun converting(source: String): T

    override fun convert(source: Any?, context: ParameterContext?): Any {
        checkSource(source)
        val sourceString = source as String
        return converting(sourceString)
    }

    private fun checkSource(source: Any?) {
        if (source == null)
            throw ArgumentConversionException("Cannot convert null source object")

        val sourceString = source as? String
            ?: throw ArgumentConversionException("Cannot convert source object because it's not a string")

        if (sourceString.isBlank()) {
            throw ArgumentConversionException("Cannot convert an empty source string")
        }
    }
}