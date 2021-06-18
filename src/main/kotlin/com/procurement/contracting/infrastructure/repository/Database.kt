package com.procurement.contracting.infrastructure.repository

object Database {
    const val KEYSPACE = "ocds"
    const val KEYSPACE_CONTRACTING = "contracting"

    object History {
        const val TABLE = "contracting_history"
        const val COMMAND_ID = "operation_id"
        const val COMMAND_NAME = "command"
        const val COMMAND_DATE = "operation_date"
        const val JSON_DATA = "json_data"
    }

    object History_V2 {
        const val TABLE = "history"
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
        const val COLUMN_AWARD_CONTRACT_ID = "ac_id"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object AC {
        const val TABLE = "contracting_ac"
        const val COLUMN_CPID = "cp_id"
        const val COLUMN_ID = "ac_id"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_MPC = "mpc"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object AC_V2 {
        const val TABLE = "ac"
        const val COLUMN_CPID = "cpid"
        const val COLUMN_OCID = "ocid"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object FC {
        const val TABLE = "fc"
        const val COLUMN_CPID = "cpid"
        const val COLUMN_OCID = "ocid"
        const val COLUMN_ID = "id"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object PAC {
        const val TABLE = "pac"
        const val COLUMN_CPID = "cpid"
        const val COLUMN_OCID = "ocid"
        const val COLUMN_ID = "id"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object PO {
        const val TABLE = "po"
        const val COLUMN_CPID = "cpid"
        const val COLUMN_OCID = "ocid"
        const val COLUMN_TOKEN = "token_entity"
        const val COLUMN_OWNER = "owner"
        const val COLUMN_CREATED_DATE = "created_date"
        const val COLUMN_STATUS = "status"
        const val COLUMN_STATUS_DETAILS = "status_details"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object ConfirmationRequest {
        const val TABLE = "confirmation_requests"
        const val COLUMN_CPID = "cpid"
        const val COLUMN_OCID = "ocid"
        const val COLUMN_CONTRACT_ID = "contract_id"
        const val COLUMN_ID = "id"
        const val COLUMN_REQUESTS = "requests"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object ConfirmationResponse {
        const val TABLE = "confirmation_responses"
        const val COLUMN_CPID = "cpid"
        const val COLUMN_OCID = "ocid"
        const val COLUMN_CONTRACT_ID = "contract_id"
        const val COLUMN_ID = "id"
        const val COLUMN_REQUEST_ID = "request_id"
        const val COLUMN_JSON_DATA = "json_data"
    }

    object Template {
        const val TABLE = "templates"
        const val COLUMN_COUNTRY = "country"
        const val COLUMN_PMD = "pmd"
        const val COLUMN_TEMPLATE_ID = "template_id"
        const val COLUMN_LANGUAGE = "language"
        const val COLUMN_TEMPLATE = "template"
    }

    object Rules {
        const val TABLE = "rules"
        const val PARAMETER = "parameter"
        const val VALUE = "value"
        const val KEY = "key"
    }
}
