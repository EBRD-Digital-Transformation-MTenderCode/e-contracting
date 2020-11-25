package com.procurement.contracting.infrastructure.bind.amount

class AmountValueException(amount: String, description: String = "") :
    RuntimeException("Incorrect value of the amount: '$amount'. $description")