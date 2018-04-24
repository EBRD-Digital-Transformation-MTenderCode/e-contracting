package com.procurement.contracting.service;

import com.procurement.contracting.model.dto.bpe.ResponseDto;
import com.procurement.contracting.model.dto.contractAwardNotice.CreateCanRQ;
import org.springframework.stereotype.Service;

public interface CANService {

    ResponseDto createCAN(String cpId, String owner, CreateCanRQ contractDto);

    ResponseDto checkCAN(String cpId, String token, String idPlatform);

    ResponseDto changeStatus(String cpId, String awardId);
}
