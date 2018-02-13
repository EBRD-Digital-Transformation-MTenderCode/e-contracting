package com.procurement.contracting.service;

import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.createCAN.CreateCanRQ;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface CANServise {
    ResponseDto createCAN(String cpId, String owner, CreateCanRQ contractDto);
}
