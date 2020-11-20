package com.procurement.contracting.infrastructure.repository

object Database {
    const val KEYSPACE = "ocds"

    object History {
        const val TABLE = "contracting_history"
        const val COMMAND_ID = "operation_id"
        const val COMMAND_NAME = "command"
        const val COMMAND_DATE = "operation_date"
        const val JSON_DATA = "json_data"
    }

    object CAN {
        const val TABLE = "contracting_can"
        const val COLUMN_CPID = "cp_id"
        const val COLUMN_CANID = "can_id"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_AWARD_ID = "award_id"
        const val COLUMN_LOT_ID = "lot_id"
        const val COLUMN_CONTRACT_ID = "ac_id"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object AC {
        const val TABLE = "contracting_ac"
        const val COLUMN_CPID = "cp_id"
        const val COLUMN_CONTRACT_ID = "ac_id"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_MPC = "mpc"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object Template {
        const val TABLE = "contracting_templates"
        const val COLUMN_COUNTRY = "country"
        const val COLUMN_PMD = "pmd"
        const val COLUMN_TEMPLATE_ID = "template_id"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_TEMPLATE = "template"
    }
}
