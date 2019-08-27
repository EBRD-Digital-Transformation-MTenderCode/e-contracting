package com.procurement.contracting.infrastructure.exception

class QuantityValueException(quantity: String, description: String = "") :
    RuntimeException("Incorrect value of the quantity: '$quantity'. $description")
