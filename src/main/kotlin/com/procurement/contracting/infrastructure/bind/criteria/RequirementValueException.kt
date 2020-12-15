package com.procurement.contracting.infrastructure.bind.criteria

class RequirementValueException(requirementValue: String, description: String = "") :
    RuntimeException("Incorrect value in requirement: '$requirementValue'. $description")