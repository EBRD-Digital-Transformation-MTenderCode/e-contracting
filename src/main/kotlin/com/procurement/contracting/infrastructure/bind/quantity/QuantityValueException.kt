package com.procurement.contracting.infrastructure.bind.quantity

class QuantityValueException(quantity: String, description: String = "") :
    RuntimeException("Incorrect value of the quantity: '$quantity'. $description")
