package com.procurement.contracting.infrastructure.fail

import com.procurement.contracting.application.service.Logger
import com.procurement.contracting.domain.model.EnumElementProvider
import com.procurement.contracting.lib.functional.Result
import com.procurement.contracting.lib.functional.ValidationResult

sealed class Fail {

    abstract val code: String
    abstract val description: String
    val message: String
        get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."

    abstract fun logging(logger: Logger)

    abstract class Error() : Fail() {
        companion object {
            fun <T, E : Error> E.toResult(): Result<T, E> = Result.failure(this)
            fun <E : Error> E.toValidationResult(): ValidationResult<E> = ValidationResult.error(this)
        }

        override fun logging(logger: Logger) {
            logger.error(message = message)
        }
    }

    sealed class Incident(val level: Level, number: String, override val description: String) : Fail() {
        override val code: String = "INC-$number"

        override fun logging(logger: Logger) {
            when (level) {
                Level.ERROR -> logger.error(message)
                Level.WARNING -> logger.warn(message)
                Level.INFO -> logger.info(message)
            }
        }

        sealed class Database(val number: String, override val description: String) :
            Incident(level = Level.ERROR, number = number, description = description) {

            abstract val exception: Exception

            class DatabaseInteractionIncident(override val exception: Exception) : Database(
                number = "1.1",
                description = "Database incident."
            ) {
                override fun logging(logger: Logger) {
                    logger.error(message = message, exception = exception)
                }
            }

            class SaveIncident(message: String) : Incident(
                level = Level.ERROR,
                number = "1.2",
                description = "Database consistency incident. $message"
            )
        }

        sealed class Transform(val number: String, override val description: String) :
            Incident(level = Level.ERROR, number = number, description = description) {

            abstract val exception: Exception?

            override fun logging(logger: Logger) {
                logger.error(message = message, exception = exception)
            }

            class ParseFromDatabaseIncident(val jsonData: String, override val exception: Exception) : Transform(
                number = "2.1",
                description = "Could not parse data stored in database."
            ) {
                override fun logging(logger: Logger) {
                    logger.error(message = message, mdc = mapOf("jsonData" to jsonData), exception = exception)
                }
            }

            class Parsing(className: String, override val exception: Exception) :
                Transform(number = "2.2", description = "Error parsing to $className.")

            class Mapping(description: String, override val exception: Exception? = null) :
                Transform(number = "2.4", description = description)

            class Deserialization(description: String, override val exception: Exception) :
                Transform(number = "2.5", description = description)

            class Serialization(description: String, override val exception: Exception) :
                Transform(number = "2.6", description = description)
        }

        enum class Level(override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}
