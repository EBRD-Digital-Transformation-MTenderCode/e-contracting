package com.procurement.contracting.config.properties

import com.procurement.contracting.infrastructure.io.orThrow
import com.procurement.contracting.infrastructure.web.dto.ApiVersion2
import java.util.*

object GlobalProperties2 {
    val service = Service()

    object App {
        val apiVersion = ApiVersion2(major = 1, minor = 0, patch = 0)
    }

    class Service {
        val id: String = "7"
        val name: String = "e-evaluation"
        val version: String = loadVersion()

        private fun loadVersion(): String {
            val gitProps: Properties = try {
                GlobalProperties::class.java.getResourceAsStream("/git.properties")
                    .use { stream ->
                        Properties().apply { load(stream) }
                    }
            } catch (expected: Exception) {
                throw IllegalStateException(expected)
            }
            return gitProps.orThrow("git.commit.id.abbrev")
        }
    }
}